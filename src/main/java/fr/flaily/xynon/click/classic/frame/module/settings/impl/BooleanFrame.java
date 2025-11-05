package fr.flaily.xynon.click.classic.frame.module.settings.impl;

import fr.flaily.xynon.click.classic.frame.module.ModuleFrame;
import fr.flaily.xynon.click.classic.frame.module.settings.SettingFrame;
import fr.flaily.xynon.click.classic.frame.module.settings.SettingStyle;
import fr.flaily.xynon.module.settings.impl.BooleanSetting;
import fr.flaily.xynon.utils.render.RenderUtil;
import net.minecraft.client.gui.Gui;

import java.awt.*;

public class BooleanFrame extends SettingFrame<BooleanSetting> implements SettingStyle {

    public BooleanFrame(ModuleFrame parentFrame, BooleanSetting setting, int relY) {
        this.setting = setting;
        this.parentFrame = parentFrame;
        this.relY = relY;
    }

    public int getX() {
        return parentFrame.getX();
    }

    public int getY() {
        return (int) (parentFrame.getY() + relY);
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        Gui.drawRect(0, 0, width(), height(), new Color(31, 31, 31).getRGB());
        font.drawStringWithShadow(setting.name + ": " + setting.isValue(),
                4, (height() / 2f) - (font.getHeight(setting.name + ": " + setting.isValue()) / 2f), -1);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if(RenderUtil.hover(getX(), getY() + getScrollOffset(), mouseX, mouseY, width(), height()-1)) {
            setting.toggle();
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {

    }

    @Override public int height() { return 24; }
    @Override public int width()  { return 120; }
}
