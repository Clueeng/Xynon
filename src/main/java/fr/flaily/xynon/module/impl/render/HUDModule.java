package fr.flaily.xynon.module.impl.render;

import best.azura.eventbus.handler.EventHandler;
import fr.flaily.xynon.Xynon;
import fr.flaily.xynon.events.render.ScreenEvent;
import fr.flaily.xynon.module.FeatureInfo;
import fr.flaily.xynon.module.Module;
import fr.flaily.xynon.module.settings.impl.ColorSetting;
import fr.flaily.xynon.module.settings.impl.ModeSetting;
import fr.flaily.xynon.module.settings.impl.MultiSelectSetting;
import fr.flaily.xynon.utils.font.CustomFontRenderer;
import fr.flaily.xynon.utils.render.ColorUtils;
import net.minecraft.client.gui.Gui;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

@FeatureInfo(name = "HUD", key = -1, category = Module.Category.Render)
public class HUDModule extends Module {
    private CustomFontRenderer font = Xynon.INSTANCE.getFontManager().getFunnel().size(20);

    public MultiSelectSetting components = multi(
            "Components", Arrays.asList(), Arrays.asList("ArrayList", "Watermark"), () -> true
    );

    public ModeSetting hudColorMode = mode("Color mode", "Single", () -> true, "Single", "Double", "Astolfo");
    public ColorSetting mainColor = color("Main Color", 255, 255, 255, 255, () -> hudColorMode.is("Single") || hudColorMode.is("Double"));
    public ColorSetting accentColor = color("Accent Color", 255, 255, 255, 255, () -> hudColorMode.is("Double"));

    public int hudColor;

    @EventHandler
    public void onRender(ScreenEvent event) {

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
        float padding = 4.0f;
        float margin = 4.0f;
        float lineHeight = 4.0f;

        float xPos = event.getSr().getScaledWidth() - padding;
        float yPos = 0.0f + padding;
        float height = font.getHeight("A") + 1.0f + lineHeight;

        int index = 0;
        ArrayList<Module> test = Xynon.INSTANCE.getModuleManager().modules;
        ArrayList<Module> sort = Xynon.INSTANCE.getModuleManager().lengthSortedModules(font, test);

        // TODO: Replace this later for animations
        Module lastModule;
        for(Module module : sort) {
            if(module.getModAnimation().getValue() < 0.01f) continue;

            this.hudColor = getColor(index);
            float modLength = font.getWidth(module.getDisplayName()) * module.getModAnimation().getValue();
            float moduleX = xPos - modLength;

            Gui.drawRect(moduleX - margin, yPos, moduleX + modLength, yPos + (height) * module.getModAnimation().getValue(),
                    new Color(0, 0, 0, 110).getRGB());
            font.drawStringWithShadow(module.getDisplayName(), moduleX - (margin / 2), yPos + (lineHeight / 2f), this.hudColor);


            yPos += (height) * module.getModAnimation().getValue();

            lastModule = module;
            index++;
        }
    }
}
