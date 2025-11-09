package fr.flaily.xynon.utils.render.shader.impl;

import fr.flaily.xynon.utils.Utils;
import fr.flaily.xynon.utils.render.RenderUtil;
import fr.flaily.xynon.utils.render.shader.ShaderUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.shader.Framebuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.glUniform1;

public class MenuShader implements Utils {

    public static ShaderUtil blurShader = new ShaderUtil("shaders/test.frag");

    public static Framebuffer framebuffer = new Framebuffer(1, 1, false);


    // public static void setupUniforms(float opacity) {
    //     // blurShader.setUniformf("time", (System.currentTimeMillis() - blurShader.CREATION_TIME) / 1000f);
    //     // blurShader.setUniformf("resolution", mc.displayWidth + 200, mc.displayHeight);
    //     // blurShader.setUniformf("opacity", opacity);
    //     final FloatBuffer weightBuffer = BufferUtils.createFloatBuffer(256);

    //     weightBuffer.rewind();
    //     glUniform1(blurShader.getUniform("weights"), weightBuffer);
    // }

    public static void setupUniforms(float opacity) {
        float time = (System.currentTimeMillis() - blurShader.CREATION_TIME) / 1000f;
        blurShader.setUniformf("time", time);
        blurShader.setUniformf("resolution", mc.displayWidth, mc.displayHeight);
    }


    public static void render(float opacity) {
        GlStateManager.enableBlend();
        GlStateManager.color(1, 1, 1, 1);
        OpenGlHelper.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ZERO);

        blurShader.init();
        setupUniforms(opacity);
        ShaderUtil.drawQuads();
        blurShader.unload();

        RenderUtil.resetColor();
        GlStateManager.bindTexture(0);
    }

}