package fr.flaily.xynon.utils.render.shader.impl;

import fr.flaily.xynon.utils.Utils;
import fr.flaily.xynon.utils.render.shader.ShaderUtil;
import net.minecraft.client.Minecraft;

public class RainbowShader implements Utils {

    public static ShaderUtil shader = new ShaderUtil("shaders/rainbow.frag", "shaders/rainbow.vsh");
    public static final long startTime = System.currentTimeMillis();

    public static void setupUniforms(float x, float y, float w, float h) {
        float time = (System.currentTimeMillis() - startTime) / 1000f;

//        shader.setUniformf("screen", mc.displayWidth, mc.displayHeight);
//        shader.setUniformf("pos", x, y);
//        shader.setUniformf("size", w, h);
        shader.setUniformf("resolution", mc.displayWidth, mc.displayHeight);
        shader.setUniformf("time", time);
    }

    public static void render(float x, float y, float w, float h) {
        shader.init();
        setupUniforms(x, y, w, h);
        ShaderUtil.drawQuads();
        shader.unload();
    }
}
