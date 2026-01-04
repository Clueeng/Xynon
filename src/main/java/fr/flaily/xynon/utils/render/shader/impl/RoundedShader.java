package fr.flaily.xynon.utils.render.shader.impl;

import fr.flaily.xynon.utils.Utils;
import fr.flaily.xynon.utils.render.shader.ShaderUtil;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;

import static org.lwjgl.opengl.GL11.*;

public class RoundedShader implements Utils {

    public static ShaderUtil roundedShader = new ShaderUtil("shaders/rounded.frag");

    /**
     * @param x, y, width, height: The dimensions of the object being drawn
     * @param radius: Corner roundness
     * @param mask: The rendering code (e.g., RenderUtil.renderEntityHead)
     */
    public static void drawRounded(float x, float y, float width, float height, float radius, Runnable mask) {
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        
        roundedShader.init();
        setupUniforms(x, y, width, height, radius);
        
        mask.run();
        
        roundedShader.unload();
        GlStateManager.disableBlend();
    }

    private static void setupUniforms(float x, float y, float width, float height, float radius) {
        ScaledResolution sr = new ScaledResolution(mc);
        int factor = sr.getScaleFactor();

        // Location and Size need to be converted to Screen Pixels (not GUI scale)
        // OpenGL Y starts from the bottom, so we flip it
        roundedShader.setUniformf("location", x * factor, (mc.displayHeight - (y * factor) - (height * factor)));
        roundedShader.setUniformf("rectSize", width * factor, height * factor);
        roundedShader.setUniformf("radius", radius * factor);
        roundedShader.setUniformi("textureIn", 0);
        roundedShader.setUniformf("alpha", 1.0f);
    }
}