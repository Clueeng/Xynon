package fr.flaily.xynon.module.impl.player.scaffold;

import best.azura.eventbus.handler.EventHandler;
import fr.flaily.xynon.Xynon;
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
import fr.flaily.xynon.module.settings.impl.RangeSetting;
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
    public BooleanSetting tellyBridge = bool("Telly", true, () -> true);
    public ModeSetting vectoringMode = mode("Vector", "Furthest", "Furthest", "Sideways", "Back", "Eagle");
    public BooleanSetting smoothOut = bool("Smooth Out", true, () -> true);
    public NumberSetting yawSmoothSpeed = num("Yaw Speed", 20.0, 180.0, 40.0, 1.0, () -> smoothOut.isToggled());
    public NumberSetting pitchSmoothSpeed = num("Pitch Speed", 20.0, 90.0, 40.0, 1.0, () -> smoothOut.isToggled());

    public BooleanSetting keepYSetting = bool("Keep Y", false, () -> true);
    public NumberSetting expandSetting = num("Expand", 0.0, 4.0, 0.5, 0.125, () -> true);
    public BooleanSetting checkRotationsSetting = bool("Check Rotations", false, () -> true);

    private Tuple<BlockPos, EnumFacing> blockData = null;

    private float[] rotNeeded = null;
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

    private boolean canTelly() {
        if(!tellyBridge.isToggled()) {
            return true;
        }
        return !mc.thePlayer.onGround && mc.thePlayer.motionY <= 0.2;
    }


    @EventHandler
    public void onUpdate(UpdateEvent event) {
        // Other states
        BlockPos below = getBlockBelow();

        // Place blocks
        if(mc.theWorld.isAirBlock(below) && lookingAt() && canTelly()) {
            // TODO: Change this condition above to have some sort of canTelly()
            if(placeBlock()) {
                mc.thePlayer.swingItem();
            }
        }
    }

    @EventHandler
    public void onMotion(MotionEvent event) {
        if (event.time == EventTime.POST) {
            if (shouldLook && rotNeeded != null) {
                if(smoothOut.isToggled()) {
                    // 1. Generate random smooth thresholds (mimicking your killaura snippet)
                    float yawSmoothThreshold = (float) (Math.random() * (yawSmoothSpeed.getValue() - (yawSmoothSpeed.getValue() - 5)) + (yawSmoothSpeed.getValue() - 5));
                    float pitchSmoothThreshold = (float) (Math.random() * (pitchSmoothSpeed.getValue() - (pitchSmoothSpeed.getValue() - 5)) + (pitchSmoothSpeed.getValue() - 5));

                    // 2. Calculate the difference between current server rotations and the goal
                    float yawChange = MathHelper.wrapAngleTo180_float(rotNeeded[0] - mc.thePlayer.serverYaw);
                    float pitchChange = MathHelper.wrapAngleTo180_float(rotNeeded[1] - mc.thePlayer.serverPitch);

                    // 3. Clamp the change to our smooth thresholds
                    yawChange = Math.max(-yawSmoothThreshold, Math.min(yawSmoothThreshold, yawChange));
                    pitchChange = Math.max(-pitchSmoothThreshold, Math.min(pitchSmoothThreshold, pitchChange));

                    // 4. Apply the change to the target rotations
                    this.targetYaw = mc.thePlayer.serverYaw + yawChange;
                    this.targetPitch = mc.thePlayer.serverPitch + pitchChange;
                }else{
                    this.targetYaw = rotNeeded[0];
                    this.targetPitch = rotNeeded[1];
                }
            } else {
                this.targetYaw = mc.thePlayer.rotationYaw;
                this.targetPitch = mc.thePlayer.rotationPitch;
            }
            return;
        }

        if (event.time == EventTime.PRE) {
            this.currentBlock = getBlockBelow();
            blockData = findBlock(currentBlock);

            if (blockData == null) {
                shouldLook = false;
                return;
            }

            if (tellyBridge.isToggled()) {
                if (canTelly()) {
                    // PHASE 2: Mid-air. Look back and place.
                    Vec3 playerEyes = mc.thePlayer.getPositionEyes(1f);
                    Vec3 target = WorldUtils.getBlockVector(blockData, vectoringMode);

                    this.rotNeeded = WorldUtils.getRotationDeltaToBlock(playerEyes, target);
                    this.shouldLook = true;
                } else {
                    // PHASE 1: Preparing to jump.
                    this.shouldLook = false;

                    // 1. Check if we are sprinting and moving forward
                    boolean isMovingForward = mc.thePlayer.moveForward > 0;
                    boolean isSprinting = mc.thePlayer.isSprinting();

                    // 2. Check if the player is actually looking in the direction they are moving
                    // This prevents "clunky" jumps where the player is still turning.
                    float yawDiff = Math.abs(MathHelper.wrapAngleTo180_float(mc.thePlayer.rotationYaw - mc.thePlayer.rotationYawHead));
                    boolean lookingForward = yawDiff < 5f;

                    if (mc.thePlayer.onGround && isMovingForward && isSprinting && lookingForward) {
                        mc.thePlayer.jump();
                        Xynon.INSTANCE.gameLogger().sendLog("hegllo " + tellyBridge.isToggled());
                    }
                }
            } else {
                Xynon.INSTANCE.gameLogger().sendLog("hoi");
                // Standard Scaffold logic
                Vec3 playerEyes = mc.thePlayer.getPositionEyes(1f);
                Vec3 target = WorldUtils.getBlockVector(blockData, vectoringMode);
                this.rotNeeded = WorldUtils.getRotationDeltaToBlock(playerEyes, target);
                this.shouldLook = true;
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
        if(shouldLook && canTelly()) {
            event.invertMovement(mc);
        }else{
            event.resetState(mc);
        }
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
        if (keepYSetting.isToggled())
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
        if (!checkRotationsSetting.isToggled()) return true;
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

    @Override
    public void onDisable() {
        super.onDisable();
        mc.gameSettings.keyBindForward.pressed = Keyboard.isKeyDown(mc.gameSettings.keyBindForward.getKeyCode());
        mc.gameSettings.keyBindLeft.pressed = Keyboard.isKeyDown(mc.gameSettings.keyBindLeft.getKeyCode());
        mc.gameSettings.keyBindRight.pressed = Keyboard.isKeyDown(mc.gameSettings.keyBindRight.getKeyCode());
        mc.gameSettings.keyBindBack.pressed = Keyboard.isKeyDown(mc.gameSettings.keyBindBack.getKeyCode());
    }
}
