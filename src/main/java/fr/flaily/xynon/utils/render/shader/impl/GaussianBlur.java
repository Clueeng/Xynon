package fr.flaily.xynon.utils.render.shader.impl;

import fr.flaily.xynon.utils.MathHelper;
import fr.flaily.xynon.utils.Utils;
import fr.flaily.xynon.utils.render.RenderUtil;
import fr.flaily.xynon.utils.render.StencilUtil; // Import the class from Step 1
import fr.flaily.xynon.utils.render.shader.ShaderUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.shader.Framebuffer;
import org.lwjgl.BufferUtils;
import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.glUniform1;

public class GaussianBlur implements Utils {

    public static ShaderUtil blurShader = new ShaderUtil("shaders/gaussian.frag");
    public static Framebuffer framebuffer = new Framebuffer(1, 1, false);

    public static void setupUniforms(float dir1, float dir2, float radius) {
        blurShader.setUniformi("textureIn", 0);
        blurShader.setUniformf("texelSize", 1.0F / (float) mc.displayWidth, 1.0F / (float) mc.displayHeight);
        blurShader.setUniformf("direction", dir1, dir2);
        blurShader.setUniformf("radius", radius);

        final FloatBuffer weightBuffer = BufferUtils.createFloatBuffer(256);
        for (int i = 0; i <= radius; i++) {
            weightBuffer.put(MathHelper.calculateGaussianValue(i, radius / 2));
        }

        weightBuffer.rewind();
        glUniform1(blurShader.getUniform("weights"), weightBuffer);
    }

    // New Method for Rounded Blur
    public static void renderBlur(float radius, Runnable mask) {
        GlStateManager.enableBlend();
        GlStateManager.color(1, 1, 1, 1);
        OpenGlHelper.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ZERO);

        framebuffer = RenderUtil.createFrameBuffer(framebuffer);
        framebuffer.framebufferClear();
        framebuffer.bindFramebuffer(true);

        // --- Pass 1: Horizontal Blur (No Stencil) ---
        // We render the whole screen (or a large quad) to the FBO
        // to ensure we have pixel data for the edges.
        blurShader.init();
        setupUniforms(1, 0, radius);
        RenderUtil.bindTexture(mc.getFramebuffer().framebufferTexture);
        ShaderUtil.drawQuads();
        framebuffer.unbindFramebuffer();
        blurShader.unload();

        // --- Setup Stencil Mask ---
        // 1. Initialize Stencil to write
        StencilUtil.initStencilToWrite();
        // 2. Run the user's draw code (e.g., rounded rect) to define the shape
        mask.run();
        // 3. Configure Stencil to only allow drawing INSIDE that shape
        StencilUtil.readStencilBuffer(1);

        // --- Pass 2: Vertical Blur (With Stencil) ---
        // We draw the final blurred result, but the Stencil clips it to the rounded rect.
        mc.getFramebuffer().bindFramebuffer(true);
        blurShader.init();
        setupUniforms(0, 1, radius);
        RenderUtil.bindTexture(framebuffer.framebufferTexture);
        ShaderUtil.drawQuads();
        blurShader.unload();

        // --- Cleanup ---
        StencilUtil.uninitStencilBuffer();
        RenderUtil.resetColor();
        GlStateManager.bindTexture(0);
    }
}