package fr.flaily.xynon.module.impl.render;

import best.azura.eventbus.handler.EventHandler;
import fr.flaily.xynon.Xynon;
import fr.flaily.xynon.events.render.ScreenEvent;
import fr.flaily.xynon.module.FeatureInfo;
import fr.flaily.xynon.module.Module;
import fr.flaily.xynon.module.settings.impl.ColorSetting;
import fr.flaily.xynon.module.settings.impl.ModeSetting;
import fr.flaily.xynon.module.settings.impl.MultiSelectSetting;
import fr.flaily.xynon.utils.render.ColorUtils;
import net.minecraft.client.gui.Gui;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

@FeatureInfo(name = "HUD", key = -1, category = Module.Category.Render)
public class HUD extends Module implements Render {

    public MultiSelectSetting components = multi(
            "Components", Arrays.asList("ArrayList", "Watermark"), Arrays.asList("ArrayList", "Watermark"), () -> true
    );

    public ModeSetting rectMode = mode("Rect", "None", () -> components.isSelected("ArrayList"), "None", "Left", "Right", "Outline");
    public ModeSetting hudColorMode = mode("Color mode", "Single", () -> true, "Single", "Double", "Astolfo");
    public ColorSetting mainColor = color("Main Color", 360.0f, 1.0f, 1.0f, 255, () -> hudColorMode.is("Single") || hudColorMode.is("Double"));
    public ColorSetting accentColor = color("Accent Color", 360.0f, 1.0f, 1.0f, 255, () -> hudColorMode.is("Double"));

    public int hudColor;

    @EventHandler
    public void onRender(ScreenEvent event) {
        if(components.isSelected("Watermark")) {
            String watermark = "Xynon";
            mc.fontRendererObj.drawStringWithShadow(watermark, 6, 6, -1);
        }

        if(components.isSelected("ArrayList")) {
            drawArrayList(event);
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

        for(Module module : sort) {
            if(module.getModAnimation().getValue() < 0.01f) continue;
            float anim = module.getModAnimation().getValue();

            this.hudColor = getColor(index);
            float modLength = big.getWidth(module.getListName()) * anim;
            float moduleX = xPos - modLength;
            int rectOffset = rectMode.is("Right") ? 6 : 0;

            Gui.drawRect(moduleX - margin - (rectOffset / 2f), yPos, moduleX + modLength, yPos + (height) * anim,
                    new Color(0, 0, 0, 110).getRGB());
            big.drawStringWithShadow(module.getListName(), moduleX - (margin / 2) - (rectOffset / 2f), yPos + (lineHeight / 2f), this.hudColor);

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
                Gui.drawRect(event.getSr().getScaledWidth() - margin, yPos, 
                event.getSr().getScaledWidth() - margin + 1, yPos + (height * anim), hudColor);
            }


            yPos += (height) * anim;
            index++;
        }
    }
}
