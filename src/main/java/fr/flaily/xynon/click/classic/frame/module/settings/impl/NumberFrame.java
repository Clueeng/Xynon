package fr.flaily.xynon.click.classic.frame.module.settings.impl;

import fr.flaily.xynon.click.classic.frame.module.ModuleFrame;
import fr.flaily.xynon.click.classic.frame.module.settings.SettingFrame;
import fr.flaily.xynon.click.classic.frame.module.settings.SettingStyle;
import fr.flaily.xynon.module.settings.impl.NumberSetting;
import fr.flaily.xynon.utils.render.RenderUtil;
import net.minecraft.client.gui.Gui;

public class NumberFrame extends SettingFrame<NumberSetting> implements SettingStyle {

    private boolean dragging = false;

    public NumberFrame(ModuleFrame parentFrame, NumberSetting setting, int relY) {
        this.setting = setting;
        this.parentFrame = parentFrame;
        this.relY = relY;
    }

    public int getX() { return parentFrame.getX(); }
    public int getY() { return (int) (parentFrame.getY() + relY); }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        double val = setting.getValue();
        double min = setting.minimum;
        double max = setting.maximum;

        Gui.drawRect(0, 0, width(), height(), 0xFF1F1F1F);

        double ratio = (val - min) / (max - min);
        int fill = (int) (ratio * width());

        Gui.drawRect(0, 0, fill, height(), 0xFF3FA9F5);

        font.drawStringWithShadow(
                setting.name + ": " + String.format("%.2f", val),
                4, (height() / 2f) - (font.getHeight(setting.name) / 2f),
                -1
        );

        if (dragging) {
            double percent = (mouseX - getX()) / (double) width();
            percent = Math.max(0, Math.min(1, percent));
//            setting.value = min + (max - min) * percent;
            setting.setValue(min + (max - min) * percent);
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (RenderUtil.hover((float) getX(), getY() + getScrollOffset(), mouseX, mouseY, width(), (int) (height()-1))) {
            dragging = true;
        }
    }

    @Override public void mouseReleased(int mouseX, int mouseY, int mouseButton) { dragging = false; }
    @Override public float height() { return 24; }
    @Override public int width() { return 120; }
}
