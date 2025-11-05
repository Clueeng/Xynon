package fr.flaily.xynon.utils;

public class EaseHelper {

    // Linear interpolation
    public static float lerp(float start, float end, float t) {
        return start + (end - start) * t;
    }

    // Clamp t between 0 and 1
    private static float clamp(float t) {
        return Math.max(0f, Math.min(1f, t));
    }

    // Quadratic easing
    public static float easeInQuad(float t) {
        t = clamp(t);
        return t * t;
    }

    public static float easeOutQuad(float t) {
        t = clamp(t);
        return t * (2 - t);
    }

    public static float easeInOutQuad(float t) {
        t = clamp(t);
        return t < 0.5f ? 2 * t * t : -1 + (4 - 2 * t) * t;
    }

    // Cubic easing
    public static float easeInCubic(float t) {
        t = clamp(t);
        return t * t * t;
    }

    public static float easeOutCubic(float t) {
        t = clamp(t);
        t -= 1;
        return t * t * t + 1;
    }

    public static float easeInOutCubic(float t) {
        t = clamp(t);
        return t < 0.5f ? 4 * t * t * t : (t - 1) * (2 * t - 2) * (2 * t - 2) + 1;
    }

    // Quartic easing
    public static float easeInQuart(float t) {
        t = clamp(t);
        return t * t * t * t;
    }

    public static float easeOutQuart(float t) {
        t = clamp(t);
        t -= 1;
        return 1 - t * t * t * t;
    }

    public static float easeInOutQuart(float t) {
        t = clamp(t);
        return t < 0.5f ? 8 * t * t * t * t : 1 - 8 * (t - 1) * (t - 1) * (t - 1) * (t - 1);
    }

    // Quintic easing
    public static float easeInQuint(float t) {
        t = clamp(t);
        return t * t * t * t * t;
    }

    public static float easeOutQuint(float t) {
        t = clamp(t);
        t -= 1;
        return 1 + t * t * t * t * t;
    }

    public static float easeInOutQuint(float t) {
        t = clamp(t);
        return t < 0.5f ? 16 * t * t * t * t * t : 1 + 16 * (t - 1) * (t - 1) * (t - 1) * (t - 1) * (t - 1);
    }

    // Sine easing
    public static float easeInSine(float t) {
        t = clamp(t);
        return (float) (1 - Math.cos((t * Math.PI) / 2));
    }

    public static float easeOutSine(float t) {
        t = clamp(t);
        return (float) Math.sin((t * Math.PI) / 2);
    }

    public static float easeInOutSine(float t) {
        t = clamp(t);
        return (float) (-0.5 * (Math.cos(Math.PI * t) - 1));
    }

    // Exponential easing
    public static float easeInExpo(float t) {
        t = clamp(t);
        return t == 0 ? 0 : (float) Math.pow(2, 10 * (t - 1));
    }

    public static float easeOutExpo(float t) {
        t = clamp(t);
        return t == 1 ? 1 : (float) (1 - Math.pow(2, -10 * t));
    }

    public static float easeInOutExpo(float t) {
        t = clamp(t);
        if (t == 0) return 0;
        if (t == 1) return 1;
        if (t < 0.5f) return (float) (Math.pow(2, 20 * t - 10) / 2);
        return (float) ((2 - Math.pow(2, -20 * t + 10)) / 2);
    }
}