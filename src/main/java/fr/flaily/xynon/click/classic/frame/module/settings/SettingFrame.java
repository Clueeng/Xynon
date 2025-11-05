package fr.flaily.xynon.click.classic.frame.module.settings;

import fr.flaily.xynon.click.classic.frame.module.ModuleFrame;
import lombok.Getter;
import lombok.Setter;

public abstract class SettingFrame<T> implements SettingStyle {
    @Setter @Getter
    public T setting;
    @Setter @Getter
    public ModuleFrame parentFrame;

    public float x, y, relY;

    @Getter @Setter
    public float scrollOffset;

    // Abstract methods
    public abstract void render(int mouseX, int mouseY, float partialTicks);
    public abstract void mouseClicked(int mouseX, int mouseY, int mouseButton);
    public abstract void mouseReleased(int mouseX, int mouseY, int mouseButton);

    // Helper
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
