package fr.flaily.xynon.utils.render.shader.impl;

import fr.flaily.xynon.utils.render.RenderUtil;
import fr.flaily.xynon.utils.render.shader.ShaderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.shader.Framebuffer;
import org.lwjgl.opengl.GL11;

import java.awt.Color;

public class Bloom {
    private static final ShaderUtil bloomShader = new ShaderUtil("shaders/bloom.frag");
    private static Framebuffer out = new Framebuffer(1, 1, false);
    private static Framebuffer in = new Framebuffer(1, 1, false);

    public static void renderBloom(float radius, int color, Runnable mask) {
        Minecraft mc = Minecraft.getMinecraft();
        Color c = new Color(color, true);

        in = RenderUtil.createFrameBuffer(in);
        out = RenderUtil.createFrameBuffer(out);

        // 1. Capture the shape
        in.framebufferClear();
        in.bindFramebuffer(true);
        mask.run();
        in.unbindFramebuffer();

        // 2. Horizontal Blur
        out.framebufferClear();
        out.bindFramebuffer(true);
        bloomShader.init();
        setupUniforms(1, 0, radius, c);
        RenderUtil.bindTexture(in.framebufferTexture);
        ShaderUtil.drawQuads();
        bloomShader.unload();
        out.unbindFramebuffer();

        // 3. Final Pass with Stencil "Hole"
        mc.getFramebuffer().bindFramebuffer(true);

        // --- STENCIL START ---
        GL11.glEnable(GL11.GL_STENCIL_TEST);
        GL11.glClear(GL11.GL_STENCIL_BUFFER_BIT);
        GL11.glColorMask(false, false, false, false); // Don't draw colors yet
        GL11.glStencilFunc(GL11.GL_ALWAYS, 1, 0xFF);
        GL11.glStencilOp(GL11.GL_REPLACE, GL11.GL_REPLACE, GL11.GL_REPLACE);

        mask.run(); // Draw the boxes into the stencil buffer

        GL11.glColorMask(true, true, true, true); // Turn colors back on
        GL11.glStencilFunc(GL11.GL_NOTEQUAL, 1, 0xFF); // ONLY draw where stencil is NOT 1
        GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_KEEP);
        // --- STENCIL END ---

        bloomShader.init();
        setupUniforms(0, 1, radius, c);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);

        RenderUtil.bindTexture(out.framebufferTexture);
        ShaderUtil.drawQuads();

        bloomShader.unload();
        GL11.glDisable(GL11.GL_STENCIL_TEST); // Clean up
    }

    private static void setupUniforms(float x, float y, float radius, Color color) {
        Minecraft mc = Minecraft.getMinecraft();
        bloomShader.setUniformf("texelSize", 1.0f / mc.displayWidth, 1.0f / mc.displayHeight);
        bloomShader.setUniformf("direction", x, y);
        bloomShader.setUniformf("radius", radius);
        bloomShader.setUniformf("color", color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f);
    }
}