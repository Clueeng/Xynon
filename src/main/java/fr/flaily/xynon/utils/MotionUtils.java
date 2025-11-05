package fr.flaily.xynon.utils;

import fr.flaily.xynon.events.player.MotionEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.network.play.client.C03PacketPlayer;
import org.lwjgl.input.Keyboard;

import java.util.Arrays;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import org.lwjgl.input.Keyboard;

import java.util.Arrays;
import java.util.List;

public class MotionUtils implements Utils {
    public static boolean isMoving() {
        Minecraft mc = Minecraft.getMinecraft();
        return mc.thePlayer.moveForward != 0 || mc.thePlayer.moveStrafing != 0;
    }

    public static boolean isMovingKeybind() {
        return Keyboard.isKeyDown(mc.gameSettings.keyBindForward.getKeyCode()) ||
                Keyboard.isKeyDown(mc.gameSettings.keyBindLeft.getKeyCode()) ||
                Keyboard.isKeyDown(mc.gameSettings.keyBindBack.getKeyCode()) ||
                Keyboard.isKeyDown(mc.gameSettings.keyBindRight.getKeyCode());
    }

    public static List<KeyBinding> moveKeys = Arrays.asList(mc.gameSettings.keyBindForward, mc.gameSettings.keyBindLeft, mc.gameSettings.keyBindBack, mc.gameSettings.keyBindRight, mc.gameSettings.keyBindSneak);

    public static void releaseKeys() {
        for (KeyBinding k : moveKeys) {
            k.pressed = false;
        }
    }

    public static void pressKeys() {
        for (KeyBinding k : moveKeys) {
            k.pressed = Keyboard.isKeyDown(k.getKeyCode());
        }
    }

    public static double getMotion() {
        Minecraft mc = Minecraft.getMinecraft();
        return distance(mc.thePlayer.prevPosX, mc.thePlayer.prevPosZ, mc.thePlayer.posX, mc.thePlayer.posZ);
    }

    public static void damage(MotionEvent e) {
        for (int i = 0; i <= 4 / 0.0625; i++) {
            PacketUtil.sendNoPacketEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.0625, mc.thePlayer.posZ, false));
            PacketUtil.sendNoPacketEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false));
        }
        PacketUtil.sendNoPacketEvent(new C03PacketPlayer(true));
    }

    public static int getSpeedEffect() {
        if (mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
            return mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier();
        }
        return 0;
    }

    public static void stopSprint() {
        mc.gameSettings.keyBindSprint.pressed = false;
        mc.thePlayer.setSprinting(false);
        ModuleSprint.forceStop = true;
    }

    public static double distance(final double srcX, final double srcZ,
                                  final double dstX, final double dstZ) {
        final double xDist = dstX - srcX;
        final double zDist = dstZ - srcZ;
        return Math.sqrt(xDist * xDist + zDist * zDist);
    }

    public static double direction() {
        Minecraft mc = Minecraft.getMinecraft();
        ModuleKillaura killaura = Midnight.INSTANCE.getModuleManager().getModule(ModuleKillaura.class);
        ModuleTargetStrafe targetStrafe = Midnight.INSTANCE.getModuleManager().getModule(ModuleTargetStrafe.class);
        float rotationYaw = mc.thePlayer.rotationYaw;
        if(killaura.isToggled() && killaura.currentTarget != null && targetStrafe.isToggled()){
            if(dev.stella.api.utils.MathHelper.accurateDistance(killaura.currentTarget) < targetStrafe.range.getValue()){
                rotationYaw = dev.stella.api.utils.MathHelper.getRotations(killaura.currentTarget)[0];
            }
        }
        float forward = 1f;
        if (mc.thePlayer.moveForward < 0f) {
            rotationYaw += 180f;
            forward = -0.5f;
        }
        if (mc.thePlayer.moveForward > 0f)
            forward = 0.5f;

        if (mc.thePlayer.moveStrafing > 0f)
            rotationYaw -= 90f * forward;

        if (mc.thePlayer.moveStrafing < 0f)
            rotationYaw += 90f * forward;

        return Math.toRadians(rotationYaw);
    }

    public static float moveDirection() {
        Minecraft mc = Minecraft.getMinecraft();
        float rotationYaw = mc.thePlayer.rotationYaw;
        float forward = 1f;
        if (mc.gameSettings.keyBindBack.pressed) {
            rotationYaw += 180f;
            forward = -0.5f;
        }
        if (mc.gameSettings.keyBindForward.pressed)
            forward = 0.5f;

        if (mc.gameSettings.keyBindLeft.pressed)
            rotationYaw -= 90f * forward;

        if (mc.gameSettings.keyBindRight.pressed)
            rotationYaw += 90f * forward;

        return rotationYaw;
    }

    public static void strafe(double speed) {
        if (!MotionUtils.isMoving()) return;
        Minecraft mc = Minecraft.getMinecraft();
        double yaw = direction();
        mc.thePlayer.motionX = -Math.sin(yaw) * speed;
        mc.thePlayer.motionZ = Math.cos(yaw) * speed;
    }

    public static double getSpeed() {
        return Math.hypot(mc.thePlayer.motionX, mc.thePlayer.motionZ);
    }

    public static void strafe(double speed, float maxTurn) {
        final float motionYaw = (float) Math.toDegrees(Math.atan2(mc.thePlayer.motionZ, mc.thePlayer.motionX)) - 90F;
        final float delta = net.minecraft.util.MathHelper.wrapAngleTo180_float(MotionUtils.moveDirection() - motionYaw);
        final float yaw = (motionYaw + Math.clamp(delta, -maxTurn, maxTurn)) * 0.017453292F;
        mc.thePlayer.motionX = -net.minecraft.util.MathHelper.sin(yaw) * speed;
        mc.thePlayer.motionZ = MathHelper.cos(yaw) * speed;
    }

    public static void strafeNoMove(double speed) {
        Minecraft mc = Minecraft.getMinecraft();
        double yaw = direction();
        mc.thePlayer.motionX = -Math.sin(yaw) * speed;
        mc.thePlayer.motionZ = Math.cos(yaw) * speed;
    }

    public static double distToGround(int halfBlockLimit) {
        boolean halfUp = mc.thePlayer.posY - (int)(mc.thePlayer.posY) > 0.5;
        double up = halfUp ? 0.5 : 0;
        double start = Math.floor(mc.thePlayer.posY) + up;

        Vec3 currentPos = new Vec3(mc.thePlayer.posX, start, mc.thePlayer.posZ);

        double retValue = 0;
        int breakCondition = 0;
        while(mc.theWorld.isAirBlock(new BlockPos(currentPos.xCoord, currentPos.yCoord, currentPos.zCoord)) && breakCondition < halfBlockLimit){
            currentPos = currentPos.addVector(0, -0.5, 0);
            retValue += 0.5;
            breakCondition++;
        }
        //return breakCondition >= halfBlockLimit ? -1 : retValue;
        return retValue;
    }
}
