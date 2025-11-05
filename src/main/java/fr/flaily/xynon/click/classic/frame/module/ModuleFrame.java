package fr.flaily.xynon.click.classic.frame.module;

import fr.flaily.xynon.click.classic.frame.CategoryFrame;
import fr.flaily.xynon.click.classic.frame.module.settings.SettingFrame;
import fr.flaily.xynon.click.classic.frame.module.settings.impl.BooleanFrame;
import fr.flaily.xynon.click.classic.frame.module.settings.impl.ColorPickerFrame;
import fr.flaily.xynon.click.classic.frame.module.settings.impl.ModeFrame;
import fr.flaily.xynon.click.classic.frame.module.settings.impl.NumberFrame;
import fr.flaily.xynon.module.Module;
import fr.flaily.xynon.module.settings.Setting;
import fr.flaily.xynon.module.settings.impl.BooleanSetting;
import fr.flaily.xynon.module.settings.impl.ColorSetting;
import fr.flaily.xynon.module.settings.impl.ModeSetting;
import fr.flaily.xynon.module.settings.impl.NumberSetting;
import fr.flaily.xynon.utils.AnimFloat;
import fr.flaily.xynon.utils.render.RenderUtil;
import net.minecraft.client.gui.Gui;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;

public class ModuleFrame implements ModuleStyle {
    public CategoryFrame parentFrame;
    public Module parent;
    public float relY;
    public boolean guiShown;

    public boolean showSettings;
    public AnimFloat settingAnim = new AnimFloat(0f, 0.15f, AnimFloat.Easing.LERP);

    public ArrayList<SettingFrame<?>> settingFrames = new ArrayList<>();


    public ModuleFrame(CategoryFrame parentFrame, Module parent, int relY) {
        this.parentFrame = parentFrame;
        this.parent = parent;
        this.relY = relY;

        int offset = baseHeight;
        for(Setting<?> setting : parent.getSettings()) {
            if (setting instanceof BooleanSetting)
                settingFrames.add(new BooleanFrame(this, (BooleanSetting) setting, offset));
            else if (setting instanceof NumberSetting)
                settingFrames.add(new NumberFrame(this, (NumberSetting) setting, offset));
            else if (setting instanceof ColorSetting)
                settingFrames.add(new ColorPickerFrame(this, (ColorSetting) setting, offset));
            else if (setting instanceof ModeSetting)
                settingFrames.add(new ModeFrame(this, (ModeSetting) setting, offset));


            offset += settingFrames.get(settingFrames.size()-1).height();
        }
    }

    public int getX() {
        return parentFrame.x;
    }
    public float getY() {
        return parentFrame.y + relY;
    }

    @Override
    public int width() {
        return parentFrame.width();
    }

    @Override
    public int height() {
        int settingHeight = 0;
        if(settingAnim.getValue() > 0.01f) {
            for(SettingFrame<?> frame : settingFrames) {
                settingHeight += frame.height();
            }
        }
        return baseHeight + settingHeight;
    }

    public void render(int mouseX, int mouseY, float partialTicks, float scroll) {
        settingAnim.update(partialTicks);
        float settingScale = settingAnim.getValue();

        // ugly fix
        if(!parentFrame.showModules) {
            showSettings = false;
            settingAnim.setTarget(0.0f);
        }

        // Draw module head
        Gui.drawRect(getX(), (int) getY(), getX() + width(), (int) (getY() + baseHeight)+1, getColor(this.parent.isToggled()));
        font.drawStringWithShadow(parent.getName(), getX() + 4, getY() + 4, -1);

        if (!showSettings || !parentFrame.showModules) {
            settingAnim.setTarget(0f);
        }

        // Draw settings below module head
        float currentY = baseHeight; // relative to module frame
        for (SettingFrame<?> s : settingFrames) {
            if(!((Setting)s.setting).canShow()) continue;

            if (settingScale > 0f) {
                GL11.glPushMatrix();
                GL11.glTranslatef(getX(), getY() + currentY, 0);
                GL11.glScalef(1f, settingScale, 1f);
                s.render(mouseX, mouseY, partialTicks);
                s.setScrollOffset(scroll);
                GL11.glPopMatrix();
            }
            currentY += s.height() * settingScale; // advance Y with scale
        }
    }

    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton, float scrollOffset) {
        if(RenderUtil.hover(getX(), getY() + scrollOffset, mouseX, mouseY, width(), baseHeight-1)) {
            switch (mouseButton) {
                case 0 : {
                    this.parent.toggle();
                    break;
                }
                case 1 : {
                    this.showSettings = !this.showSettings;
                    settingAnim.setTarget(showSettings ? 1f : 0f);
                    break;
                }
            }
            return true;
        }
        if(showSettings) {
            settingFrames.forEach(s -> s.mouseClicked(mouseX, mouseY, mouseButton));
        }

        return false;
    }

    public void mouseReleased(int mouseX, int mouseY, int state, float scrollOffset) {
        if(showSettings) {
            settingFrames.forEach(s -> s.mouseReleased(mouseX, mouseY, state));
        }
    }
}
