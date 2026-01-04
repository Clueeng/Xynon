package fr.flaily.xynon.click.pvp.frame;

import fr.flaily.xynon.click.pvp.frame.setting.PvpSettingFrame;
import fr.flaily.xynon.module.impl.pvp.ModulePvP;
import fr.flaily.xynon.utils.render.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;

import java.awt.*;

public class ModuleFrame {
    public ModulePvP parent;
    public CategoryFrame owner;
    public ModuleFrame(CategoryFrame owner, ModulePvP parent) {
        this.parent = parent;
        this.owner = owner;
    }

    public void drawScreen(float x, float y, int mouseX, int mouseY, float partialTicks) {
        RenderUtil.drawRoundedRect3(x, y, getWidth(), getHeight(), 8f, new Color(0, 0, 0, 80).getRGB());
        owner.pvpGUI.titleFont.drawCenteredString(parent.name, x + (getWidth() / 2f), y + (getHeight() / 2f), -1, !parent.isToggled());

        GlStateManager.resetColor();
    }

    public void mouseClicked(int mouseButton) {
        if (mouseButton == 0) {
            parent.toggle();
        }
        if(mouseButton == 1) {
            Minecraft.getMinecraft().displayGuiScreen(new PvpSettingFrame(parent));
        }
    }

    public void mouseReleased(int mouseX, int mouseY, int state) {

    }
    public void keyTyped(char typedChar, int keyCode) {

    }

    public float getWidth() {
        float spacing = CategoryFrame.MODULE_SPACING;
        float padding = CategoryFrame.GUI_PADDING * 2;

        float totalSpacing = spacing * (CategoryFrame.MODULES_PER_LINE - 1);
        float availableWidth = owner.pvpGUI.guiWidth - padding - totalSpacing;

        return availableWidth / CategoryFrame.MODULES_PER_LINE;
    }

    public float getHeight() {
        return 62;
    }
    public boolean isHovered(float x, float y, int mouseX, int mouseY) {
        return mouseX >= x &&
                mouseX <= x + getWidth() &&
                mouseY >= y &&
                mouseY <= y + getHeight();
    }
}
