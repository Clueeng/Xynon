package fr.flaily.xynon.click.classic.frame.module.settings.impl;

import fr.flaily.xynon.click.classic.frame.module.ModuleFrame;
import fr.flaily.xynon.click.classic.frame.module.settings.SettingFrame;
import fr.flaily.xynon.click.classic.frame.module.settings.SettingStyle;
import fr.flaily.xynon.module.settings.impl.RangeSetting;
import fr.flaily.xynon.utils.render.RenderUtil;
import net.minecraft.client.gui.Gui;

public class RangeFrame extends SettingFrame<RangeSetting> implements SettingStyle {

    private boolean dragging = false;

    public RangeFrame(ModuleFrame parentFrame, RangeSetting setting, int relY) {
        this.setting = setting;
        this.parentFrame = parentFrame;
        this.relY = relY;
    }

    public int getX() { return parentFrame.getX(); }
    public int getY() { return (int) (parentFrame.getY() + relY); }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        RangeSetting setting = this.setting;

        double valMin = setting.getValueMin();
        double valMax = setting.getValueMax();

        double min = setting.minimum;
        double max = setting.maximum;

        Gui.drawRect(getX(), getY(), getX() + width(), getY() + height(), 0xFF1F1F1F);

        // Draw min handle
        double ratioMin = (valMin - min) / (max - min);
        int fillMin = (int) (ratioMin * width());

        // Draw max handle
        double ratioMax = (valMax - min) / (max - min);
        int fillMax = (int) (ratioMax * width());

        // Draw range bar
        Gui.drawRect(getX() + fillMin, getY(), getX() + fillMax, getY() + height(), 0xFF3FA9F5);

        // Draw setting label
        font.drawStringWithShadow(
                setting.name + ": " + String.format("%.2f", valMin) + " - " + String.format("%.2f", valMax),
                getX() + 4, getY() + (height() / 2f) - (font.getHeight(setting.name) / 2f),
                -1
        );

        if (dragging) {
            double percent = (mouseX - getX()) / (double) width();
            percent = Math.max(0, Math.min(1, percent));

            // Determine whether user is dragging min or max handle
            double distToMin = Math.abs(mouseX - (getX() + fillMin));
            double distToMax = Math.abs(mouseX - (getX() + fillMax));

            if (distToMin < distToMax) {
                setting.setValueMin(min + (max - min) * percent);
            } else {
                setting.setValueMax(min + (max - min) * percent);
            }
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (RenderUtil.hover((float) getX(), getY() + getScrollOffset(), mouseX, mouseY, width(), (int) (height() - 1))) {
            dragging = true;
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        dragging = false;
    }

    @Override
    public float height() {
        return 24;
    }

    @Override
    public int width() {
        return 120;
    }
}
