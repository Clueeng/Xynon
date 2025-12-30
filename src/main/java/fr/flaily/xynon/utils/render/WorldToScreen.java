package fr.flaily.xynon.utils.render;

import fr.flaily.xynon.utils.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GLAllocation;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
public class WorldToScreen implements Utils {
    private static final FloatBuffer modelView = GLAllocation.createDirectFloatBuffer(16);
    private static final FloatBuffer projection = GLAllocation.createDirectFloatBuffer(16);
    private static final IntBuffer viewport = GLAllocation.createDirectIntBuffer(16);

    public static void updateMatrices() {
        GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, modelView);
        GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX, projection);
        GL11.glGetInteger(GL11.GL_VIEWPORT, viewport);
    }

    public static float[] worldToScreen(double xPos, double yPos, double zPos, ScaledResolution sr) {
        FloatBuffer screenCoords = GLAllocation.createDirectFloatBuffer(3);

        boolean result = GLU.gluProject(
                (float) (xPos - mc.getRenderManager().viewerPosX),
                (float) (yPos - mc.getRenderManager().viewerPosY),
                (float) (zPos - mc.getRenderManager().viewerPosZ),
                modelView, projection, viewport, screenCoords
        );

        if (result) {
            float x = screenCoords.get(0);
            float y = screenCoords.get(1);
            float z = screenCoords.get(2);

            if (z >= 0.0f && z < 1.0f) {
                float scaleFactor = sr.getScaleFactor();
                return new float[] {
                        x / scaleFactor,
                        ((float) Display.getHeight() - y) / scaleFactor,
                        z
                };
            }
        }
        return null;
    }
}