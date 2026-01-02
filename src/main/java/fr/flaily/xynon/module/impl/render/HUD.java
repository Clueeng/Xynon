package fr.flaily.xynon.module.impl.render;

import best.azura.eventbus.handler.EventHandler;
import com.google.common.collect.Queues;
import com.google.common.util.concurrent.AtomicDouble;
import fr.flaily.xynon.Xynon;
import fr.flaily.xynon.events.render.ScreenEvent;
import fr.flaily.xynon.module.FeatureInfo;
import fr.flaily.xynon.module.Module;
import fr.flaily.xynon.module.settings.impl.ColorSetting;
import fr.flaily.xynon.module.settings.impl.ModeSetting;
import fr.flaily.xynon.module.settings.impl.MultiSelectSetting;
import fr.flaily.xynon.utils.BlurRenderer;
import fr.flaily.xynon.utils.render.ColorUtils;
import fr.flaily.xynon.utils.render.RenderUtil;
import fr.flaily.xynon.utils.render.shader.ShaderUtil;
import fr.flaily.xynon.utils.render.shader.impl.Bloom;
import fr.flaily.xynon.utils.render.shader.impl.GaussianBlur;
import fr.flaily.xynon.utils.render.shader.impl.LSDShader;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.shader.Framebuffer;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Queue;

import org.lwjgl.opengl.GL11;

@FeatureInfo(name = "HUD", key = -1, category = Module.Category.Render)
public class HUD extends Module implements Render {

    public MultiSelectSetting components = multi(
            "Components", Arrays.asList("ArrayList", "Watermark", "Debug"), Arrays.asList("ArrayList", "Watermark", "Debug"), () -> true
    );

    public ModeSetting rectMode = mode("Rect", "None", () -> components.isSelected("ArrayList"), "None", "Left", "Right", "Outline");
    public ModeSetting hudColorMode = mode("Color mode", "Single", () -> true, "Single", "Double", "Astolfo");
    public ColorSetting mainColor = color("Main Color", 360.0f, 1.0f, 1.0f, 255, () -> hudColorMode.is("Single") || hudColorMode.is("Double"));
    public ColorSetting accentColor = color("Accent Color", 360.0f, 1.0f, 1.0f, 255, () -> hudColorMode.is("Double"));

    public int hudColor;

    private AtomicDouble testPos = new  AtomicDouble(0);

    private long lastFrameTime;
    Queue<Long> frameQueue = Queues.newArrayDeque();

    private static final int MAX_SAMPLES = 1800;
    private final float[] frameTimes = new float[MAX_SAMPLES];
    private int frameIndex = 0;
    private int frameCount = 0;

    @EventHandler
    public void onRender(ScreenEvent event) {
        if(components.isSelected("ArrayList")) {
            drawArrayList(event);
        }
        if(components.isSelected("Watermark")) {
            String watermark = "Xynon - 1.0" ;
            double x = 6;
            double y = 6;
            double width = 100;
            double height = 100;
            double radius = 13;

            Runnable watermarkShape = () -> {
                RenderUtil.drawRoundedRect3(x, y, x + big.getWidth(watermark) + 2, y + big.getHeight(watermark) + 2, 12f, -1);
            };
            Bloom.renderBloom(4f, new Color(0, 0, 0, 150).getRGB(), watermarkShape);
            GaussianBlur.renderBlur(13f, watermarkShape);
//            fr.drawStringWithShadow(watermark, (float) x, (float) y, -1);
            big.drawStringWithShadow(watermark, (float) x + 4, (float) y + 4, -1);

        }
        if(components.isSelected("Debug")) {
            long ms = System.currentTimeMillis();
            long frameTime = ms - lastFrameTime;

            String fps = "Last frame: " + (frameTime) + "ms";
            fr.drawStringWithShadow(fps, 4, 4, -1);
            drawFrameTimeGraph(4, 300);

            frameTimes[frameIndex] = frameTime;
            frameIndex = (frameIndex + 1) % MAX_SAMPLES;
            frameCount = Math.min(frameCount + 1, MAX_SAMPLES);


            lastFrameTime = ms;
        }
    }

    private int getColor(int moduleOffset) {
        switch (hudColorMode.getCurrent()) {
            case "Single" : {
                return mainColor.getColor().getRGB();
            }
            case "Double": {
                return ColorUtils.blendThing(2F, (long) moduleOffset * 160, mainColor.getColor(), accentColor.getColor());
            }
            case "Astolfo": {
                return ColorUtils.astolfo(5f, 0.6f, 1.0f, (long) moduleOffset * 160);
            }
        }
        return -1;
    }

    public void drawArrayList(ScreenEvent event) {
        float padding = 0.0f;
        float margin = 3.0f;
        float lineHeight = 2.0f;

        float xPos = event.getSr().getScaledWidth() - padding;
        float yPos = 0.0f + padding;
        float height = big.getHeight("B") + 1.0f + lineHeight;

        int index = 0;
        ArrayList<Module> test = Xynon.INSTANCE.getModuleManager().modules;
        ArrayList<Module> sort = Xynon.INSTANCE.getModuleManager().lengthSortedModules(big, test);
        java.util.List<Module> shown = Xynon.INSTANCE.getModuleManager().lengthSortedModules(big, test).stream().filter(m -> m.getModAnimation().getValue() >= 0.01f).toList();

        Runnable arrayListShape = () -> {
            float currentY = 0.0f + padding;
            for (Module module : sort) {
                if (module.getModAnimation().getValue() < 0.01f) continue;
                float anim = module.getModAnimation().getValue();
                float modLength = big.getWidth(module.getListName()) * anim;
                float moduleX = xPos - modLength;
                int rectOffset = rectMode.is("Right") ? 3 : 0;

                // Draw the rectangle area.
                // Note: Use white (-1) for the shader mask pass
                Gui.drawRect(moduleX - margin - (rectOffset / 2f), currentY,
                        moduleX + modLength, currentY + (height * anim), -1);

                currentY += (height * anim);
            }
        };
        // 1. Render a LARGE, soft shadow.
        // Radius 12f-18f is the "sweet spot" for that floating look.
        if(rectMode.is("Right") || rectMode.is("None")) {
            Bloom.renderBloom(4f, new Color(0, 0, 0, 150).getRGB(), arrayListShape);
        }

        // 2. Render the blur
        GaussianBlur.renderBlur(13f, arrayListShape);


        for(Module module : sort) {
            if(module.getModAnimation().getValue() < 0.01f) continue;
            float anim = module.getModAnimation().getValue();
            testPos.set(yPos);

            this.hudColor = getColor(index);
            float modLength = big.getWidth(module.getListName()) * anim;
            float moduleX = xPos - modLength;
            int rectOffset = rectMode.is("Right") ? 3 : 0;

            Gui.drawRect(moduleX - margin - (rectOffset / 2f), yPos, moduleX + modLength, yPos + (height) * anim,
                    new Color(0, 0, 0, 110).getRGB());


//            Bloom.renderBloom(13f, new Color(0, 0, 0, 90).getRGB(), () -> {
//                big.drawStringWithShadow(module.getListName(), moduleX - (margin / 2) - (rectOffset / 2f), testPos.floatValue() + (lineHeight / 2f), this.hudColor);
//            });
            big.drawStringWithShadow(module.getListName(), moduleX - (margin / 2) - (rectOffset / 2f), testPos.floatValue() + (lineHeight / 2f), this.hudColor);

            // Rectangle
            if(rectMode.is("Outline")) {
                int size = shown.size() - 1;

                float lineY = (yPos + (height) * anim);
                if(index < size) {
                    Module nextModule = shown.get(index + 1);
                    
                    float nextModLength = big.getWidth(nextModule.getListName()) * anim;
                    float nextModuleX = xPos - nextModLength;
                    float diff = (moduleX - margin - (rectOffset / 2f)) - (nextModuleX - margin - (rectOffset / 2f));

                    Gui.drawRect(moduleX - margin - (rectOffset / 2f),
                    lineY - 1, 
                    moduleX - margin - (rectOffset / 2f) - diff,
                    lineY, this.hudColor);
                }else{
                    Gui.drawRect(moduleX - margin,
                    lineY - 1, moduleX + modLength,
                    lineY, this.hudColor);
                }
            }
            if(rectMode.is("Left") || rectMode.is("Outline")) {
                Gui.drawRect(moduleX - margin - 1, yPos, moduleX - margin, yPos + (height) * anim, this.hudColor);
            }

            if(rectMode.is("Right")) {
                int fix = 2;
                Gui.drawRect(event.getSr().getScaledWidth() - margin + fix, yPos,
                event.getSr().getScaledWidth() - margin + 1 + fix, yPos + (height * anim), hudColor);
            }


            yPos += (height) * anim;
            index++;
        }
    }

    private void drawFrameTimeGraph(int x, int y) {
        int width = 300;
        int height = 100;

        float maxMs = 20f;

        // background
        RenderUtil.drawRect(x, y - height, x + width, y, 0x90000000);

        int samples = frameCount;
        if (samples < 2) return;

        for (int i = 0; i < width; i++) {
            int sampleIndex = (frameIndex - 1 - i + MAX_SAMPLES) % MAX_SAMPLES;
            float ms = Math.min(frameTimes[sampleIndex], maxMs);

            float normalized = ms / maxMs;
            int barHeight = (int) (normalized * height);

            int color =
                    ms < 6   ? 0xFF00FF00 : // good
                            ms < 10  ? 0xFFFFFF00 : // ok
                                    0xFFFF0000; // bad

            RenderUtil.drawRect(
                    x + width - i,
                    y - barHeight,
                    x + width - i + 1,
                    y,
                    color
            );
        }
    }

}
