package fr.flaily.xynon.module.impl.player.scaffold;

import best.azura.eventbus.handler.EventHandler;
import fr.flaily.xynon.events.EventTime;
import fr.flaily.xynon.events.game.EventOverrideInput;
import fr.flaily.xynon.events.player.EventStrafe;
import fr.flaily.xynon.events.player.MotionEvent;
import fr.flaily.xynon.events.player.RotationEvent;
import fr.flaily.xynon.events.player.UpdateEvent;
import fr.flaily.xynon.events.render.WorldRenderEvent;
import fr.flaily.xynon.module.FeatureInfo;
import fr.flaily.xynon.module.Module;
import fr.flaily.xynon.module.settings.impl.BooleanSetting;
import fr.flaily.xynon.module.settings.impl.ModeSetting;
import fr.flaily.xynon.module.settings.impl.NumberSetting;
import fr.flaily.xynon.utils.MotionUtils;
import fr.flaily.xynon.utils.WorldUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.Entity;
import net.minecraft.util.*;
import org.lwjgl.input.Keyboard;

@FeatureInfo(name = "Scaffold", category = Module.Category.Player, key = Keyboard.KEY_Z)
public class Scaffold extends Module {
    // TODO: fix the rotation with getBlockVector implementation from compliance
    public ModeSetting vectoringMode = mode("Vector", "Furthest", "Furthest", "Sideways", "Back", "Eagle");
    public BooleanSetting keepYSetting = bool("Keep Y", false, () -> true);
    public NumberSetting expandSetting = num("Expand", 0.0, 4.0, 0.5, 0.125, () -> true);
    public BooleanSetting checkRotationsSetting = bool("Check Rotations", false, () -> true);

    private Tuple<BlockPos, EnumFacing> blockData = null;

    private float[] deltaNeeded = null;
    private double startY, jumpY;
    private float targetYaw, targetPitch;
    private boolean shouldLook;

    private BlockPos currentBlock;

    @EventHandler
    public void onMove(EventStrafe event) {
        if(!shouldLook)
            return;
        event.setYaw(targetYaw);
    }

    @EventHandler
    public void onRotation(RotationEvent event) {
        if(!shouldLook)
            return;

        event.setYaw(targetYaw);
        event.setPitch(targetPitch);
    }


    @EventHandler
    public void onUpdate(UpdateEvent event) {
        // Other states
        BlockPos below = getBlockBelow();

        // Place blocks
        if(mc.theWorld.isAirBlock(below) && lookingAt()) {
            if(placeBlock()) {
                mc.thePlayer.swingItem();
            }
        }
    }

    @EventHandler
    public void onMotion(MotionEvent event) {
        if(event.time == EventTime.POST) {
            // update targetYaw and targetPitch here
            if(shouldLook) {
                this.targetYaw = deltaNeeded[0];
                this.targetPitch = deltaNeeded[1];
            }else{
                this.targetYaw = mc.thePlayer.rotationYaw;
                this.targetPitch = mc.thePlayer.rotationPitch;
            }
        }
        if(event.time == EventTime.PRE) {
            shouldLook = false;

            this.currentBlock = getBlockBelow();
            blockData = findBlock(currentBlock);
            if(blockData == null) return;

            Vec3 playerEyes = mc.thePlayer.getPositionEyes(1f);
            Vec3 target = WorldUtils.getBlockVector(blockData, vectoringMode);

            // only rotate when below is air
            if(mc.theWorld.isAirBlock(currentBlock)) {
                deltaNeeded = WorldUtils.getRotationDeltaToBlock(playerEyes, target);
                shouldLook = true;
            }
        }
    }

    @EventHandler
    public void onWorldRender(WorldRenderEvent event) {
        GlStateManager.depthMask(false);
        GlStateManager.disableTexture2D();
        GlStateManager.disableLighting();
        GlStateManager.disableCull();
        GlStateManager.disableBlend();
        GlStateManager.disableDepth();

        if(currentBlock != null) {
            AxisAlignedBB renderedBlock = new AxisAlignedBB(currentBlock.getX(), currentBlock.getY(), currentBlock.getZ(),
                    currentBlock.getX() + 1, currentBlock.getY() + 1, currentBlock.getZ() + 1);

            RenderGlobal.drawOutlinedBoundingBox(renderedBlock
                            .offset(-mc.getRenderManager().viewerPosX, -mc.getRenderManager().viewerPosY, -mc.getRenderManager().viewerPosZ)
                            .expand(0.001, 0.001, 0.001)
                    , 255, 0, 0, 255);

        }
        GlStateManager.enableDepth();
        GlStateManager.enableTexture2D();
        GlStateManager.enableLighting();
        GlStateManager.enableCull();
        GlStateManager.disableBlend();
        GlStateManager.depthMask(true);
    }

    @EventHandler
    public void overrideKey(EventOverrideInput event) {
//        event.invertMovement(mc);
    }

    private BlockPos getClosest(double maxDist) {
        BlockPos base = mc.thePlayer.getPosition();

        for (double r = 0; r <= maxDist; r++) {
            for (double dx = -r; dx <= r; dx++) {
                for (double dz = -r; dz <= r; dz++) {
                    if (Math.abs(dx) != r && Math.abs(dz) != r) continue;

                    for (double dy = -1; dy >= -2; dy--) {
                        BlockPos pos = base.add(dx, dy, dz);

                        if (!mc.theWorld.isAirBlock(pos)) {
                            return pos;
                        }
                    }
                }
            }
        }
        return null;
    }
    private BlockPos getBlockBelow() {
        double baseY = mc.thePlayer.posY - 1;
        if (keepYSetting.getValue())
            baseY = Math.min(baseY, startY - 1);
        else
            startY = mc.thePlayer.posY;

        final double direction = Math.toRadians(MotionUtils.moveDirection()),
                x = Math.sin(direction), z = Math.cos(direction);
        for (double d = expandSetting.step; d <= expandSetting.getValue(); d += expandSetting.step) {
            final var added = new BlockPos(mc.thePlayer.posX - x * d, baseY, mc.thePlayer.posZ + z * d);
            if (mc.theWorld.isAirBlock(added))
                return added;
        }

        return new BlockPos(mc.thePlayer.posX, baseY, mc.thePlayer.posZ);
    }

    private Tuple<BlockPos, EnumFacing> findBlock(BlockPos belowIn) {
        Vec3 playerEyes = belowIn.getCenter();
        double distance = Math.pow(mc.playerController.getBlockReachDistance(), 2);

        Tuple<BlockPos, EnumFacing> result = null;

        for (int y = -1; y <= 0; y++) {
            BlockPos below = belowIn.offset(0, y, 0);
            for (int i = 1; i <= 4; i++) {
                for (EnumFacing immutableDirection : EnumFacing.values()) {
                    EnumFacing direction = immutableDirection;
                    if (direction == EnumFacing.DOWN) continue;
                    BlockPos blockPos = below.offset(direction.getOpposite(), i);
                    if (mc.theWorld.isAirBlock(blockPos)) {
                        boolean abort = true;
                        for (int j = 1; j <= 4; j++) {
                            for (EnumFacing facing : EnumFacing.values()) {
                                if (facing.getDirectionVec().getY() != 0 || facing == direction) continue;
                                BlockPos pos = below.offset(direction.getOpposite(), i).offset(facing, j);
                                if (!mc.theWorld.isAirBlock(pos)) {
                                    direction = facing.getOpposite();
                                    blockPos = pos;
                                    abort = false;
                                    break;
                                }
                            }
                            if (!abort) break;
                        }
                        if (abort) continue;
                    }
                    double currentDistance = playerEyes.squareDistanceTo(blockPos.getCenter());
                    if (currentDistance > distance) continue;
                    distance = currentDistance;
                    result = new Tuple<>(blockPos, direction);
                }
            }
        }

        return result;
    }

    private boolean placeBlock() {
        return mc.playerController.onPlayerRightClick(
                mc.thePlayer, mc.theWorld,
                mc.thePlayer.getHeldItem(),
                blockData.getFirst(), blockData.getSecond(),
                WorldUtils.getBlockVector(blockData, vectoringMode)
        );
    }

    private boolean lookingAt() {
        return lookingAt(mc.thePlayer.serverYaw, mc.thePlayer.serverPitch);
    }

    private boolean lookingAt(final float yaw, final float pitch) {
        if (!checkRotationsSetting.getValue()) return true;
        if (blockData == null) return false;
        final float f = mc.playerController.getBlockReachDistance();
        Vec3 src = mc.thePlayer.getPositionEyes(1.0F);
        Vec3 rotationVec = Entity.getVectorForRotation(pitch, yaw);
        Vec3 dest = src.addVector(rotationVec.xCoord * f, rotationVec.yCoord * f, rotationVec.zCoord * f);
        final var currentPos = blockData.getFirst();
        if (mc.theWorld.getBlockState(currentPos) == null) return false;
        IBlockState blockState = mc.theWorld.getBlockState(currentPos);
        if (blockState.getBlock().getCollisionBoundingBox(mc.theWorld, currentPos, blockState) == null) return false;
        AxisAlignedBB bb = blockState.getBlock().getCollisionBoundingBox(mc.theWorld, currentPos, blockState);
        return bb.calculateIntercept(src, dest) != null;
    }
}
