package fr.flaily.xynon.utils;

import net.minecraft.client.Minecraft;

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
}
