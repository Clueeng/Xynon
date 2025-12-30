package fr.flaily.xynon.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

public class MathHelper implements Utils {

    public static float[] applyGCD(float yaw, float pitch, float prevYaw, float prevPitch) {
        float[] rotations = new float[] { prevYaw, prevPitch };

        // alan wood fps simulator
        for (int i = 0; i < Minecraft.getDebugFPS() / 20; i++)
            rotations = applyGCDInternal(yaw, pitch, rotations[0], rotations[1]);

        return rotations;
    }


    private static float[] applyGCDInternal(float yaw, float pitch, float prevYaw, float prevPitch) {
        final float deltaPitch = net.minecraft.util.MathHelper.wrapAngleTo180_float(pitch - prevPitch),
                deltaYaw = net.minecraft.util.MathHelper.wrapAngleTo180_float(yaw - prevYaw);
        final int cursorDeltaX = Math.round(deltaYaw / 0.15f),
                cursorDeltaY = Math.round(deltaPitch / 0.15f);

        float f = mc.gameSettings.mouseSensitivity * 0.6F + 0.2F;
        float f1 = f * f * f * 8.0F;
        float f2 = (float) cursorDeltaX * f1;
        float f3 = (float) cursorDeltaY * f1;

        return new float[]{
                (float) ((double) prevYaw + (double) f2 * 0.15D) ,
                net.minecraft.util.MathHelper.clamp_float((float) ((double) prevPitch + (double) f3 * 0.15D), -90.0F, 90.0F)
        };
    }



    public static float randomNumber(float min, float max) {
        return (float) Math.random() * (max - min) + min;
    }

    public static float accurateDistance(EntityLivingBase entityLiving) {
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        double playerX = player.posX;
        double playerY = player.posY + player.getEyeHeight();
        double playerZ = player.posZ;

        AxisAlignedBB box = entityLiving.getEntityBoundingBox();

        double closestX = clamp(playerX, box.minX, box.maxX);
        double closestY = clamp(playerY, box.minY, box.maxY);
        double closestZ = clamp(playerZ, box.minZ, box.maxZ);

        double dx = playerX - closestX;
        double dy = playerY - closestY;
        double dz = playerZ - closestZ;
        return (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    public static float accurateDistance(Vec3 feet, EntityLivingBase entityLiving) {
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        double playerX = feet.xCoord;
        double playerY = feet.yCoord + player.getEyeHeight();
        double playerZ = feet.zCoord;

        AxisAlignedBB box = entityLiving.getEntityBoundingBox();

        double closestX = clamp(playerX, box.minX, box.maxX);
        double closestY = clamp(playerY, box.minY, box.maxY);
        double closestZ = clamp(playerZ, box.minZ, box.maxZ);

        double dx = playerX - closestX;
        double dy = playerY - closestY;
        double dz = playerZ - closestZ;
        return (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    public static float accurateDistance(EntityLivingBase entityLiving, Vec3 position) {
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        double playerX = player.posX;
        double playerY = player.posY + player.getEyeHeight();
        double playerZ = player.posZ;

        AxisAlignedBB box = entityLiving.getEntityBoundingBox().offset(
                -entityLiving.posX + position.xCoord,
                -entityLiving.posY + position.yCoord,
                -entityLiving.posZ + position.zCoord
        );

        double closestX = clamp(playerX, box.minX, box.maxX);
        double closestY = clamp(playerY, box.minY, box.maxY);
        double closestZ = clamp(playerZ, box.minZ, box.maxZ);

        double dx = playerX - closestX;
        double dy = playerY - closestY;
        double dz = playerZ - closestZ;
        return (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
    }


    public static boolean isLookingAtEntity(EntityPlayerSP player, Entity target, double maxDistance) {
        Vec3 eyePosition = player.getPositionEyes(1.0F);
        AxisAlignedBB targetBB = target.getEntityBoundingBox();
        Vec3 lookVector = player.getLook(1.0F);
        Vec3 endPosition = eyePosition.addVector(lookVector.xCoord * maxDistance, lookVector.yCoord * maxDistance, lookVector.zCoord * maxDistance);
        MovingObjectPosition result = targetBB.calculateIntercept(eyePosition, endPosition);
        return result != null;
    }

    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    public static float[] getRotations(Entity e) {
        Minecraft mc = Minecraft.getMinecraft();
        double deltaX = e.posX + (e.posX - e.lastTickPosX) - mc.thePlayer.posX,
                deltaY = e.posY - 3.5 + e.getEyeHeight() - mc.thePlayer.posY + mc.thePlayer.getEyeHeight(),
                deltaZ = e.posZ + (e.posZ - e.lastTickPosZ) - mc.thePlayer.posZ,
                distance = Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaZ, 2));

        float yaw = (float) Math.toDegrees(-Math.atan(deltaX / deltaZ)),
                pitch = (float) -Math.toDegrees(Math.atan(deltaY / distance));

        if (deltaX < 0 && deltaZ < 0) {
            yaw = (float) (90 + Math.toDegrees(Math.atan(deltaZ / deltaX)));
        } else if (deltaX > 0 && deltaZ < 0) {
            yaw = (float) (-90 + Math.toDegrees(Math.atan(deltaZ / deltaX)));
        }
        return new float[]{yaw, pitch};
    }

    public static float wrap(float value) {
        value = value % 360.0F;
        if (value >= 180.0F) value -= 360.0F;
        if (value < -180.0F) value += 360.0F;
        return value;
    }


    public static float calculateGaussianValue(float x, float sigma) {
        double PI = 3.141592653;
        double output = 1.0 / Math.sqrt(2.0 * PI * (sigma * sigma));
        return (float) (output * Math.exp(-(x * x) / (2.0 * (sigma * sigma))));
    }



    public static double lerp(double delta, double start, double end) {
        return start + delta * (end - start);
    }
    public static double easeInQuad(double delta, double start, double end) {
        return start + (end - start) * Math.pow(delta, 2);
    }
    public static double easeOutQuad(double delta, double start, double end) {
        return start + (end - start) * (1 - Math.pow(1 - delta, 2));
    }
    public static double easeInOutQuad(double delta, double start, double end) {
        if (delta < 0.5) {
            return start + (end - start) * 2 * Math.pow(delta, 2);
        } else {
            return start + (end - start) * (1 - 2 * Math.pow(1 - delta, 2));
        }
    }
    public static double easeInCubic(double delta, double start, double end) {
        return start + (end - start) * Math.pow(delta, 3);
    }
    public static double easeOutCubic(double delta, double start, double end) {
        return start + (end - start) * (1 - Math.pow(1 - delta, 3));
    }
    public static double easeInOutCubic(double delta, double start, double end) {
        if (delta < 0.5) {
            return start + (end - start) * 4 * Math.pow(delta, 3);
        } else {
            return start + (end - start) * (1 - 4 * Math.pow(1 - delta, 3));
        }
    }
    public static double easeInBounce(double delta, double start, double end) {
        return start + (end - start) * (1 - easeOutBounce(1 - delta, 0, 1));
    }

    private static double easeOutBounce(double delta, double start, double end) {
        if (delta < (1 / 2.75)) {
            return end * (7.5625 * delta * delta) + start;
        } else if (delta < (2 / 2.75)) {
            return end * (7.5625 * (delta -= (1.5 / 2.75)) * delta + 0.75) + start;
        } else if (delta < (2.5 / 2.75)) {
            return end * (7.5625 * (delta -= (2.25 / 2.75)) * delta + 0.9375) + start;
        } else {
            return end * (7.5625 * (delta -= (2.625 / 2.75)) * delta + 0.984375) + start;
        }
    }
    public static double easeInOutBounce(double delta, double start, double end) {
        if (delta < 0.5) {
            return easeInBounce(delta * 2, 0, end - start) * 0.5 + start;
        } else {
            return easeOutBounce(delta * 2 - 1, 0, end - start) * 0.5 + (end - start) * 0.5 + start;
        }
    }

}
