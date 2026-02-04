package fr.flaily.xynon.module.impl.macro;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;

import best.azura.eventbus.handler.EventHandler;
import fr.flaily.xynon.events.game.EventClick;
import fr.flaily.xynon.events.game.EventOverrideInput;
import fr.flaily.xynon.events.player.UpdateEvent;
import fr.flaily.xynon.events.render.WorldRenderEvent;
import fr.flaily.xynon.module.FeatureInfo;
import fr.flaily.xynon.module.Module;
import fr.flaily.xynon.utils.MouseUtils;
import fr.flaily.xynon.utils.WorldUtils;
import fr.flaily.xynon.utils.logger.Logger;
import fr.flaily.xynon.utils.path.PathNode;
import fr.flaily.xynon.utils.render.RenderUtil;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.MathHelper;

@FeatureInfo(name = "AutoFish", category = Module.Category.Macros, key = -1)
public class AutoFish extends Module {

    private List<BlockPos> currentPath = new ArrayList<>();
    private int ticksSincePathUpdate = 0;
    private BlockPos posTarget;

    @EventHandler
    public void onUpdate(UpdateEvent event) {
        if(mc.thePlayer.ticksExisted % 10 == 0) {
            this.posTarget = getTargetBlock();
        }
        if (this.posTarget == null) {
            currentPath.clear();
            stopWalking();
            return;
        }

        // 1. Calculate/Update path every 20 ticks (1 second) to save performance
        if (currentPath.isEmpty() || mc.thePlayer.ticksExisted % 10 == 0) {
            currentPath = findPath(new BlockPos(mc.thePlayer), this.posTarget);
        }

        mc.gameSettings.keyBindAttack.pressed = looksAtBlock();
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @EventHandler
    public void onMouseClick(EventClick eventClick) {
        
    }


    private boolean looksAtBlock() {
        if(this.posTarget != null) {
            if(mc.objectMouseOver.getBlockPos().equals(this.posTarget)) {
                return true;
            }
        }
        return false;
    }

    @EventHandler
    public void overrideKey(EventOverrideInput input) {
        if(this.posTarget != null) {
            MouseUtils mouse = new MouseUtils(input);
            if(!mc.objectMouseOver.getBlockPos().equals(this.posTarget)) {
                mouse.moveTo(this.posTarget, 0, 0);
            }
        }


        // 2. Execute movement
        if (currentPath != null && !currentPath.isEmpty()) {
            traversePath(input);
        } else {
            stopWalking();
        }
    }

    @EventHandler
    public void onRenderWorld(WorldRenderEvent event) {
        RenderUtil.renderBoxBlock(this.posTarget);

        if (currentPath != null && !currentPath.isEmpty()) {
            for (BlockPos pathPos : currentPath) {
                RenderUtil.renderBoxBlock(pathPos); // Highlights the path blocks
            }
        }
    }

    private BlockPos getTargetBlock() {
        return WorldUtils.getClosestBlock(Material.cactus, 32);
    }



    public List<BlockPos> findPath(BlockPos start, BlockPos target) {
        PriorityQueue<PathNode> openSet = new PriorityQueue<>(Comparator.comparingDouble(PathNode::getFCost));
        HashSet<BlockPos> closedSet = new HashSet<>();

        openSet.add(new PathNode(start, null, 0, start.distanceSq(target)));

        while (!openSet.isEmpty()) {
            PathNode current = openSet.poll();
            if (current.pos.equals(target) || current.pos.distanceSq(target) < 2) {
                return retracePath(current);
            }

            closedSet.add(current.pos);

            for (BlockPos neighbor : getNeighbors(current.pos)) {
                if (closedSet.contains(neighbor)) continue;

                double newCost = current.gCost + current.pos.distanceSq(neighbor);
                PathNode node = new PathNode(neighbor, current, newCost, neighbor.distanceSq(target));
                openSet.add(node);
            }
            
            // Safety break to prevent infinite loops if no path is found
            if (closedSet.size() > 1000) break;
        }
        return null;
    }

    private List<BlockPos> retracePath(PathNode endNode) {
        List<BlockPos> path = new ArrayList<>();
        PathNode currentState = endNode;

        // Work backwards from the end to the start using the parent references
        while (currentState != null) {
            path.add(currentState.pos);
            currentState = currentState.parent;
        }

        // Since we went from Target -> Start, we must reverse it to get Start -> Target
        java.util.Collections.reverse(path);
        
        // Optional: Remove the first node because that's usually the block the player is already standing on
        if (!path.isEmpty()) {
            path.remove(0);
        }

        return path;
    }

    private List<BlockPos> getNeighbors(BlockPos pos) {
        List<BlockPos> neighbors = new ArrayList<>();
        int[] dirs = {-1, 0, 1};

        for (int x : dirs) {
            for (int z : dirs) {
                if (x == 0 && z == 0) continue;
                if (Math.abs(x) == 1 && Math.abs(z) == 1) {
                    if (!mc.theWorld.isAirBlock(pos.add(x, 0, 0)) || !mc.theWorld.isAirBlock(pos.add(0, 0, z))) {
                        continue;
                    }
                }
                
                BlockPos check = pos.add(x, 0, z);
                
                // Basic Walkability: Is there space for legs/head and a floor?
                if (isWalkable(check)) {
                    neighbors.add(check);
                } 
                // Jump Check: Can we jump up 1 block?
                else if (isWalkable(check.up())) {
                    neighbors.add(check.up());
                }
                // Fall Check: Can we drop down 1 block?
                else if (isWalkable(check.down())) {
                    neighbors.add(check.down());
                }
            }
        }
        return neighbors;
    }

    private boolean isWalkable(BlockPos p) {
        Minecraft mc = Minecraft.getMinecraft();
        return mc.theWorld.isAirBlock(p) && // Legs
            mc.theWorld.isAirBlock(p.up()) && // Head
            mc.theWorld.getBlockState(p.down()).getBlock().isFullCube(); // Floor
    }

    private void traversePath(EventOverrideInput event) {
        if (currentPath.isEmpty()) return;
        MouseUtils mouseUtils = new MouseUtils(event);

        BlockPos next = currentPath.get(0);

        // 1. Handle Rotation via MouseUtils (moveMouse logic)
        // We target the center of the next block. 
        // Jerkiness is 0 for navigation to ensure stable movement.
        mouseUtils.moveTo(next, 0.0f, 0.05f);

        // 2. Handle Movement Inputs via the Event
        double dx = (next.getX() + 0.5) - mc.thePlayer.posX;
        double dz = (next.getZ() + 0.5) - mc.thePlayer.posZ;
        double distanceSq = dx * dx + dz * dz;

        // Press Forward using the event system
        event.send(mc.gameSettings.keyBindForward);

        // 3. Handle Jumping
        // Check if the node is higher than our current feet level
        if (next.getY() > Math.floor(mc.thePlayer.posY) && mc.thePlayer.onGround) {
            event.send(mc.gameSettings.keyBindJump);
        }else{
            mc.gameSettings.keyBindJump.pressed = false;
        }

        // 4. Node Completion Logic
        // We use a slightly larger threshold (0.4) for navigation to prevent "circling" the point
        if (distanceSq < 0.4) {
            currentPath.remove(0);
            
            // If the path is now empty, stop the player
            if (currentPath.isEmpty()) {
                mc.gameSettings.keyBindForward.pressed = false;
            }
        }
    }

    private void stopWalking() {
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.getKeyCode(), false);
    }
}
