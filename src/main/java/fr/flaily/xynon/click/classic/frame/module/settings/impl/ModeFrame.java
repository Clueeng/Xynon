package fr.flaily.xynon.click.classic.frame.module.settings.impl;

import fr.flaily.xynon.click.classic.frame.module.ModuleFrame;
import fr.flaily.xynon.click.classic.frame.module.settings.SettingFrame;
import fr.flaily.xynon.click.classic.frame.module.settings.SettingStyle;
import fr.flaily.xynon.module.settings.impl.ModeSetting;
import fr.flaily.xynon.utils.AnimFloat;
import fr.flaily.xynon.utils.render.RenderUtil;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class ModeFrame extends SettingFrame<ModeSetting> implements SettingStyle {
    // Horizontal slide
    public int oldCurrent;
    public AnimFloat anim = new AnimFloat(0.0f, 0.05f, AnimFloat.Easing.LERP);

    public ModeFrame(ModuleFrame parentFrame, ModeSetting setting, int relY) {
        this.setting = setting;
        this.parentFrame = parentFrame;
        this.relY = relY;
        this.oldCurrent = setting.current;
        this.anim.setValue((float) setting.current / (this.setting.modes.length - 1));
        this.anim.setTarget(oldCurrent);
    }

    public int getX() {
        return parentFrame.getX();
    }

    public int getY() {
        return (int) (parentFrame.getY() + relY);
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        anim.update(partialTicks);
        int nbSettings = (this.setting.modes.length - 1);

        setting.onValueUpdate(() -> {
//            Xynon.INSTANCE.gameLogger().sendLog("Current: " + setting.current);
            this.anim.setTarget((float) setting.current / nbSettings);
        });

        Gui.drawRect(0, 0, width(), height(), new Color(31, 31, 31).getRGB());
        // Draw all modes, with a scissorbox
        float start = font.getWidth(setting.name + ": ");

//        GL11.glEnable(GL11.GL_SCISSOR_TEST);
//        RenderUtil.prepareScissorBox(getX() + start, getY(), getX() + width(), getY() + height());

        GL11.glPushMatrix();

        int space = 100;
        int offset = 0;
        float translateOffset = -space * (anim.getValue() * nbSettings);

        // Translate
        GlStateManager.translate(translateOffset, 0, 0);

        for(String mode : setting.modes) {
            font.drawStringWithShadow(mode, start + offset + 4, 8, -1);
            offset += space;
        }

        GlStateManager.translate(-translateOffset, 0, 0);
        GL11.glPopMatrix();


        Gui.drawRect(0, 0, start + 4, height(), new Color(31, 31, 31).getRGB());
        font.drawStringWithShadow(setting.name + ": ",
                4, (height() / 2f) - (font.getHeight(setting.name + ": ") / 2f), -1);

//        GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if(RenderUtil.hover(getX(), getY() + getScrollOffset(), mouseX, mouseY, width(), height()-1)) {
            setting.cycle(mouseButton);
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {

    }

    @Override public int height() { return 24; }
    @Override public int width()  { return 120; }
}
