package fr.flaily.xynon.module.settings.impl;

import best.azura.eventbus.handler.EventHandler;
import fr.flaily.xynon.Xynon;
import fr.flaily.xynon.events.render.ScreenEvent;
import fr.flaily.xynon.module.settings.Setting;

import java.awt.*;
import java.util.function.Supplier;

public class ColorSetting extends Setting<Integer> {

    private float hue;        // 0-360
    private float saturation; // 0-1
    private float brightness; // 0-1 (HSV)
    private int alpha;        // 0-255

    public ColorSetting(String name,
                        float hue, float saturation, float brightness, int alpha,
                        Supplier<Boolean> dependency) {
        super(name, hsvToColor(hue, saturation, brightness, alpha), dependency);

        this.hue = hue;
        this.saturation = saturation;
        this.brightness = brightness;
        this.alpha = alpha;

        Xynon.INSTANCE.getEventBus().subscribe(this);
    }

    public ColorSetting(String name,
                        float hue, float saturation, float brightness, int alpha) {
        this(name, hue, saturation, brightness, alpha, () -> true);
    }

    public boolean rainbow;

    @EventHandler
    public void updateRainbow(ScreenEvent event) {
        if(!rainbow) return;
        
        this.setHue(this.getHue() + 0.5f);
        if(this.getHue() >= 360F) {
            this.setHue(0f);
        }
    }

    public float getHue() { return hue; }
    public float getSaturation() { return saturation; }
    public float getBrightness() { return brightness; } // renamed from value
    public int getAlpha() { return alpha; }

    public Color getColor() {
        return new Color(getValue(), true);
    }
    public Integer getRed() {
        return new Color(getValue(), true).getRed();
    }
    public Integer getGreen() {
        return new Color(getValue(), true).getGreen();
    }
    public Integer getBlue() {
        return new Color(getValue(), true).getBlue();
    }

    /* ========== Setters called by ColorPickerFrame ========== */

    public void setHue(float hue) {
        this.hue = clamp(hue, 0f, 360f);
        updateColor();
    }

    public void setSaturation(float sat) {
        this.saturation = clamp(sat, 0f, 1f);
        updateColor();
    }

    public void setBrightness(float b) {
        this.brightness = clamp(b, 0f, 1f);
        updateColor();
    }

    public void setAlpha(int alpha) {
        this.alpha = Math.min(255, Math.max(0, alpha));
        updateColor();
    }

    /* ========== HSV → RGB conversion and setting update ========== */

    private void updateColor() {
        int color = hsvToColor(hue, saturation, brightness, alpha);
        setValue(color);
    }

    private static int hsvToColor(float h, float s, float b, int alpha) {
        int rgb = Color.HSBtoRGB(h / 360f, s, b);
        // Inject alpha into ARGB integer
        return (alpha << 24) | (rgb & 0x00FFFFFF);
    }

    private static float clamp(float v, float min, float max) {
        return Math.max(min, Math.min(max, v));
    }
}
