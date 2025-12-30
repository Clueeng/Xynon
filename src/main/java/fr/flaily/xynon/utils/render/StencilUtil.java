package fr.flaily.xynon.utils.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

public class StencilUtil {

    private static final Minecraft mc = Minecraft.getMinecraft();

    public static void checkSetupFBO() {
        // Sets up the Framebuffer to support stencil if it doesn't already
        net.minecraft.client.shader.Framebuffer fbo = mc.getFramebuffer();
        if (fbo != null) {
            if (fbo.depthBuffer > -1) {
                setupFBO(fbo);
                fbo.depthBuffer = -1;
            }
        }
    }

    public static void setupFBO(net.minecraft.client.shader.Framebuffer fbo) {
        // Re-creates the framebuffer with the stencil bit enabled
        org.lwjgl.opengl.EXTFramebufferObject.glDeleteRenderbuffersEXT(fbo.depthBuffer);
        int stencilDepthBufferID = org.lwjgl.opengl.EXTFramebufferObject.glGenRenderbuffersEXT();
        org.lwjgl.opengl.EXTFramebufferObject.glBindRenderbufferEXT(org.lwjgl.opengl.EXTFramebufferObject.GL_RENDERBUFFER_EXT, stencilDepthBufferID);
        org.lwjgl.opengl.EXTFramebufferObject.glRenderbufferStorageEXT(org.lwjgl.opengl.EXTFramebufferObject.GL_RENDERBUFFER_EXT, org.lwjgl.opengl.EXTPackedDepthStencil.GL_DEPTH_STENCIL_EXT, mc.displayWidth, mc.displayHeight);
        org.lwjgl.opengl.EXTFramebufferObject.glFramebufferRenderbufferEXT(org.lwjgl.opengl.EXTFramebufferObject.GL_FRAMEBUFFER_EXT, org.lwjgl.opengl.EXTFramebufferObject.GL_DEPTH_ATTACHMENT_EXT, org.lwjgl.opengl.EXTFramebufferObject.GL_RENDERBUFFER_EXT, stencilDepthBufferID);
        org.lwjgl.opengl.EXTFramebufferObject.glFramebufferRenderbufferEXT(org.lwjgl.opengl.EXTFramebufferObject.GL_FRAMEBUFFER_EXT, org.lwjgl.opengl.EXTFramebufferObject.GL_STENCIL_ATTACHMENT_EXT, org.lwjgl.opengl.EXTFramebufferObject.GL_RENDERBUFFER_EXT, stencilDepthBufferID);
    }

    public static void initStencilToWrite() {
        // Initialize the Stencil Buffer to write 1s where we draw
        mc.getFramebuffer().bindFramebuffer(false);
        checkSetupFBO();
        GL11.glClear(GL11.GL_STENCIL_BUFFER_BIT);
        GL11.glEnable(GL11.GL_STENCIL_TEST);

        GL11.glStencilFunc(GL11.GL_ALWAYS, 1, 1);
        GL11.glStencilOp(GL11.GL_REPLACE, GL11.GL_REPLACE, GL11.GL_REPLACE);
        GL11.glColorMask(false, false, false, false); // Disable color writing
    }

    public static void readStencilBuffer(int ref) {
        // Change Stencil mode to only render where the stencil value equals 'ref' (1)
        GL11.glColorMask(true, true, true, true); // Re-enable color writing
        GL11.glStencilFunc(GL11.GL_EQUAL, ref, 1);
        GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_KEEP);
    }

    public static void uninitStencilBuffer() {
        // Disable Stencil Buffer
        GL11.glDisable(GL11.GL_STENCIL_TEST);
    }
}