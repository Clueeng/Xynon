package fr.flaily.xynon.click.pvp.frame;

import fr.flaily.xynon.Xynon;
import fr.flaily.xynon.click.pvp.PvpGUI;
import fr.flaily.xynon.module.impl.pvp.ModulePvP;
import fr.flaily.xynon.utils.render.RenderUtil;

import java.io.IOException;
import java.util.ArrayList;

public class CategoryFrame {
    public static final int MODULES_PER_LINE = 3;
    public static final int MODULE_SPACING = 4;
    public static final int GUI_PADDING = 5;

    public PvpGUI pvpGUI;
    public ModulePvP.Category selectedCategory;
    public ArrayList<ModuleFrame> childrenFrames = new ArrayList<>();

    private float x, y;
    private void updatePos(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public CategoryFrame(PvpGUI parentFrame, ModulePvP.Category selectedCategory) {
        this.selectedCategory = selectedCategory;
        this.pvpGUI = parentFrame;
        for(ModulePvP module : Xynon.INSTANCE.getModuleManager().getModulesPvP(selectedCategory)) {
            childrenFrames.add(new ModuleFrame(this, module));
        }
    }

    // Since its a box, we draw in a relative way, so we need the first x and y
    public void drawScreen(float x, float y, int mouseX, int mouseY, float partialTicks) {
        float curModX = x + GUI_PADDING;
        float curModY = y + GUI_PADDING;
        updatePos(x, y);
        int i = 0;

        RenderUtil.prepareScissorBox(x, y, x + pvpGUI.guiWidth, y + (pvpGUI.guiHeight - PvpGUI.BOTTOM_BAR_HEIGHT));
        for(ModuleFrame frame : childrenFrames) {
            frame.drawScreen(curModX, curModY, mouseX, mouseY, partialTicks);

            curModX += frame.getWidth() + MODULE_SPACING;
            i++;
            if(i > (MODULES_PER_LINE - 1)) {
                curModX = x + GUI_PADDING;
                curModY += frame.getHeight() + MODULE_SPACING;
                i = 0;
            }
        }
    }
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        float curModX = pvpGUI.x + GUI_PADDING;
        float curModY = pvpGUI.y + GUI_PADDING + PvpGUI.TITLE_BAR_HEIGHT;
        int i = 0;

        for (ModuleFrame frame : childrenFrames) {
            if (frame.isHovered(curModX, curModY, mouseX, mouseY)) {
                frame.mouseClicked(mouseButton);
                return;
            }

            curModX += frame.getWidth() + MODULE_SPACING;
            i++;

            if (i > (MODULES_PER_LINE - 1)) {
                curModX = pvpGUI.x + GUI_PADDING;
                curModY += frame.getHeight() + MODULE_SPACING;
                i = 0;
            }
        }
    }

    public void mouseReleased(int mouseX, int mouseY, int state) {

    }
    public void keyTyped(char typedChar, int keyCode) {

    }
}
