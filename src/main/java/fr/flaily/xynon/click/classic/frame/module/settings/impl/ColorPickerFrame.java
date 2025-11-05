package fr.flaily.xynon.click.classic.frame.module.settings.impl;

import fr.flaily.xynon.click.classic.frame.module.ModuleFrame;
import fr.flaily.xynon.click.classic.frame.module.settings.SettingFrame;
import fr.flaily.xynon.click.classic.frame.module.settings.SettingStyle;
import fr.flaily.xynon.module.settings.impl.ColorSetting;
import fr.flaily.xynon.utils.render.RenderUtil;
import net.minecraft.client.gui.Gui;
import org.lwjgl.opengl.GL11;
import java.awt.*;

import static net.minecraft.realms.RealmsMth.clamp;

public class ColorPickerFrame extends SettingFrame<ColorSetting> implements SettingStyle {

    private boolean draggingHue = false;
    private boolean draggingSL = false;

    private final int pickerSize = 100;
    private final int hueWidth = 6;
    int[] gradient = generateFullColorGradient(pickerSize);

    private long sinceLastClick = System.currentTimeMillis();
    private float oldHue = 0F;

    public ColorPickerFrame(ModuleFrame parentFrame, ColorSetting setting, int relY) {
        this.setting = setting;
        this.parentFrame = parentFrame;
        this.relY = relY;
    }

    public int getX() { return parentFrame.getX(); }
    public int getY() { return (int) (parentFrame.getY() + relY); }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        int x = 0;
        int y = 0;

        // Background
        Gui.drawRect(x, y, x + width(), y + height(), 0xFF1F1F1F);

        // Header
        int xOffset = 12;
        font.drawStringWithShadow(setting.name, x + 4, y + 4, -1);
        RenderUtil.drawRoundedRect(x + width() - xOffset, y + 4, 9, 9, 3f, new Color(255, 255, 255).getRGB());
        RenderUtil.drawRoundedRect(x + width() - (xOffset - 1), y + 5, 7, 7, 3f, setting.getValue());

        int boxX = x + 4;
        int boxY = y + 20;

        drawSBGradient(boxX, boxY, pickerSize, pickerSize);

        int cx = boxX + (int)(setting.getSaturation() * pickerSize);
        int cy = boxY + (int)((1 - setting.getBrightness()) * pickerSize); // use brightness
        Gui.drawRect(cx - 2, cy - 2, cx + 2, cy + 2, 0xFFFFFFFF);

        // Hue bar
        int hueX = boxX + pickerSize + 4;
        drawHueGradient(pickerSize + 8, 20, hueWidth, pickerSize);


        int hy = boxY + (int)((setting.getHue() / 360f) * pickerSize);
        Gui.drawRect(hueX - 1, hy - 2, hueX + hueWidth + 1, hy + 2, 0xFFFFFFFF);

        if (draggingSL) {
            float localX = mouseX - (getX() + 4);
            float localY = mouseY - (getY() + 16) - getScrollOffset();

            float s = clamp(localX / pickerSize, 0f, 1f);
            float b = clamp(1f - localY / pickerSize, 0f, 1f);

            setting.setSaturation(s);
            setting.setBrightness(b);
        }

        if (draggingHue) {
            float h = clamp((float) (mouseY - (getY() + 16) - getScrollOffset()) / pickerSize * 360f, 0f, 360f);
            setting.setHue(h);
        }

    }


    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        int x = getX() + 4;
        int y = (int) (getY() + 16 + getScrollOffset());

        if (RenderUtil.hover(x, y, mouseX, mouseY, pickerSize, pickerSize))
            draggingSL = true;

        switch (mouseButton) {
            case 0: {
                if (RenderUtil.hover(x + pickerSize + 6, y, mouseX, mouseY, hueWidth, pickerSize))
                    draggingHue = true;
                break;
            }
            case 1: {
                if (RenderUtil.hover(x + pickerSize + 6, y, mouseX, mouseY, hueWidth, pickerSize)) {
                    if(System.currentTimeMillis() - sinceLastClick < 500) {
                        this.setting.rainbow = !this.setting.rainbow;
                        if(this.setting.rainbow) {
                            this.oldHue = this.setting.getHue();
                        }else {
                            this.setting.setHue(this.oldHue);
                        }
                    }
                    sinceLastClick = System.currentTimeMillis();
                }
            }
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        draggingSL = false;
        draggingHue = false;
    }

    @Override public int height() { return 20 + pickerSize + 6; }
    @Override public int width() { return 120; }

    /* Rendering utilities */
    private void drawSBGradient(int x, int y, int w, int h) {
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        int baseColor = Color.HSBtoRGB(setting.getHue() / 360f, 1f, 1f);
        Gui.drawRect(x, y, x + w, y + h, baseColor);

        RenderUtil.drawGradientSideways( x, y, x + w + 7, y + h,
                new Color(255, 255, 255, 255).getRGB(), new Color(255, 255, 255, 0).getRGB() );

        RenderUtil.drawGradientRect( x, y - 7, x + w, y + h,
                new Color(0, 0, 0, 255).getRGB(), new Color(0, 0, 0, 0).getRGB() );

        GL11.glDisable(GL11.GL_ALPHA_TEST);
    }

    private void drawHueGradient(int x, int y, int w, int h) {
        drawFullColorGradientRect(x, y, w, h);
    }


    public void drawFullColorGradientRect(int x, int y, int width, int height) {
        for (int i = 0; i < height; i++) {
            int color = gradient[i];
            Gui.drawRect(x, y + i, x + width, y + i + 1, color);
        }
    }

    public int[] generateFullColorGradient(int steps) {
        int[] gradient = new int[steps];
        int partLength = steps / (KEY_COLORS.length - 1);

        for (int i = 0; i < KEY_COLORS.length - 1; i++) {
            for (int j = 0; j < partLength; j++) {
                float fraction = (float) j / partLength;
                gradient[i * partLength + j] = interpolateColor(KEY_COLORS[i], KEY_COLORS[i + 1], fraction);
            }
        }

        gradient[steps - 1] = KEY_COLORS[KEY_COLORS.length - 1];

        return gradient;
    }
    private static final int[] KEY_COLORS = {
            //0XFFFFFFFF, // White
            0xFFFF0000, // Red
            0xFFFFFF00, // Yellow
            0xFF00FF00, // Green
            0xFF00FFFF, // Cyan
            0xFF0000FF, // Blue
            0xFFFF00FF, // Magenta
            0xFF000000  // Black
    };

    // stackoverflow
    private int interpolateColor(int colorStart, int colorEnd, float fraction) {
        int alpha = (int) ((1 - fraction) * (colorStart >> 24 & 0xFF) + fraction * (colorEnd >> 24 & 0xFF)); // ptn de format ARGB
        int red = (int) ((1 - fraction) * (colorStart >> 16 & 0xFF) + fraction * (colorEnd >> 16 & 0xFF));
        int green = (int) ((1 - fraction) * (colorStart >> 8 & 0xFF) + fraction * (colorEnd >> 8 & 0xFF));
        int blue = (int) ((1 - fraction) * (colorStart & 0xFF) + fraction * (colorEnd & 0xFF));

        return (alpha << 24) + (red << 16) + (green << 8) + blue;
    }

}
