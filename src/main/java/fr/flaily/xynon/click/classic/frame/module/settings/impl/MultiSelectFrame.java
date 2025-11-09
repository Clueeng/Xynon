package fr.flaily.xynon.click.classic.frame.module.settings.impl;

import fr.flaily.xynon.Xynon;
import fr.flaily.xynon.click.classic.frame.module.ModuleFrame;
import fr.flaily.xynon.click.classic.frame.module.settings.SettingFrame;
import fr.flaily.xynon.click.classic.frame.module.settings.SettingStyle;
import fr.flaily.xynon.module.settings.impl.ModeSetting;
import fr.flaily.xynon.module.settings.impl.MultiSelectSetting;
import fr.flaily.xynon.utils.AnimFloat;
import fr.flaily.xynon.utils.render.RenderUtil;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.function.Consumer;

public class MultiSelectFrame extends SettingFrame<MultiSelectSetting> implements SettingStyle {
    // Animation
    public AnimFloat expandAnim = new AnimFloat(0.0f, 0.07f, AnimFloat.Easing.EASE_IN_OUT);
    private boolean expanded = false;

    public MultiSelectFrame(ModuleFrame parentFrame, MultiSelectSetting setting, int relY) {
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
        // Render top frame
        expandAnim.update(partialTicks);
        int panelHeight = 24;
        Gui.drawRect(0, 0, width(), panelHeight, backgroundColor);
        font.drawStringWithShadow(setting.name + ": ", 4, 8, -1);

        GL11.glPushMatrix();

        int belowPanel = getParentFrame().parentFrame.y + 24;
        float yPos = Math.max(belowPanel, getY() + getScrollOffset());
//        RenderUtil.prepareScissorBox(getX(), yPos, getX() + width(), yPos + height());

        if(expandAnim.getValue() > 0.01f) {
            Gui.drawRect(0, panelHeight, width(), height(), backgroundColor);
            for(int i = 0; i < setting.getOptions().size(); i++) {
//                float yPos = getY() + getScrollOffset() + );
                String option = setting.getOptions().get(i);
                font.drawStringWithShadow(option, 12, 4 + panelHeight + (i * getChoiceHeight()),
                        setting.isSelected(option) ? Color.cyan.getRGB() : -1);
            }
        }

//        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        GL11.glPopMatrix();
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        // Panel right click check for expand
        if(RenderUtil.hover(getX(), getY() + getScrollOffset(), mouseX, mouseY, width(), getChoiceHeight())) {
            if(mouseButton == 1) {
                expanded = !expanded;
                if(expanded) {
                    expandAnim.setTarget(1.0f);
                }
                else {
                    expandAnim.setTarget(0.0f);
                }
            }
        }

        // Rest of choices
        if(mouseButton == 0 && expanded) {
            for(int i = 0; i < setting.getOptions().size(); i++) {
                float yPos = getY() + getScrollOffset() + (i * getChoiceHeight()) + getChoiceHeight() + 4;
                if(RenderUtil.hover(getX(), yPos, mouseX, mouseY, width(), getChoiceHeight())) {
                    // Clicked within a mode
                    String option = setting.getOptions().get(i);
                    setting.toggle(option);

                    if(setting.onChange != null) {
                        setting.onChange.accept(option);
                    }
                }
            }
        }
    }

    public int getChoiceHeight() {
        return 16;
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {

    }

    @Override public float height() {
        int modesAmount = setting.getOptions().size();
//        return (24 + (expanded ? ((modesAmount * getChoiceHeight()) * expandAnim.getValue()) : 0));
//        return 24;
        return 24 + ((modesAmount * getChoiceHeight()) * expandAnim.getValue());
    }
    @Override public int width()  { return 120; }
}
