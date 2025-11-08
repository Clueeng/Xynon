package fr.flaily.xynon.utils;

import fr.flaily.xynon.Xynon;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;

public class AnimFloat {
    @Setter
    private float value;
    @Getter
    private float target;
    private float speed;
    private Easing easing;
    private long lastTime = -1;

    public enum Easing { LINEAR, EASE_OUT_BACK, EASE_IN_OUT, LERP }

    public AnimFloat(float start, float speed, Easing easing) {
        this.value = start;
        this.target = start;
        this.speed = speed;
        this.easing = easing;
    }

    public void update(float partialTicks) {
        if (!Float.isFinite(partialTicks) || !Float.isFinite(value) || !Float.isFinite(target) || !Float.isFinite(speed)) {
            return;
        }
        long now = System.currentTimeMillis();
        int fps = Minecraft.getDebugFPS();
        if (fps <= 0) fps = 60;
        
        float delta = (((target - value) * speed * partialTicks) / fps) * 300f;
        if (!Float.isFinite(delta)) delta = 0f;

        lastTime = now;
        value += delta;
        value = Math.max(0f, Math.min(1f, value));
    }
    public void update(float partialTicks, boolean clamp) {
        float delta = (target - value) * speed * partialTicks;
        value += delta;
        if(clamp) {
            value = Math.max(0f, Math.min(1f, value));
        }
    }

    public float getValue() {
        switch (easing) {
            case LERP:
                return value;
            case EASE_OUT_BACK:
                return easeOutBack(value);
            case EASE_IN_OUT:
                return easeInOut(value);
            default:
                return value;
        }
    }

    public void setTarget(float target) {
        this.target = target;
    }
    public void setSpeed(float speed) {
        this.speed = speed;
    }

    private float easeOutBack(float t) {
        float c1 = 1.70158f;
        float c3 = c1 + 1f;
        return 1f + c3 * (float)Math.pow(t - 1f, 3) + c1 * (float)Math.pow(t - 1f, 2);
    }

    private float easeInOut(float t) {
        return t < 0.5 ? 2*t*t : -1+(4-2*t)*t;
    }
}
