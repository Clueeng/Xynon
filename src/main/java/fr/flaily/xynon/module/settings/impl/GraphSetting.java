package fr.flaily.xynon.module.settings.impl;

import fr.flaily.xynon.module.settings.Setting;

import java.util.function.Supplier;

public class GraphSetting extends Setting<Double[]> {
    // Bounds
    public double minX, maxX,
                  minY, maxY;
    public double x, y;
    public double incrX, incrY;

    public GraphSetting(String name, double x, double y,
                        double minX, double maxX, double minY, double maxY,
                        double incrementX, double incrementY,
                        Supplier<Boolean> dependency) {
        super(name, new Double[]{x, y}, dependency);
        this.x = x;
        this.y = y;
        this.minX = minX;
        this.maxX = maxX;
        this.minY = minY;
        this.maxY = maxY;
        this.incrX = incrementX;
        this.incrY = incrementY;
    }
    public GraphSetting(String name, double x, double y,
                        double minX, double maxX, double minY, double maxY,
                        double incrementX, double incrementY) {
        super(name, new Double[]{x, y}, () -> true);
        this.x = x;
        this.y = y;
        this.minX = minX;
        this.maxX = maxX;
        this.minY = minY;
        this.maxY = maxY;
        this.incrX = incrementX;
        this.incrY = incrementY;
    }

    public void setX(double x) {
        double precisionX = 1.0 / this.incrX;
        this.x = Math.round(Math.max(this.minX, Math.min(this.maxX, x)) * precisionX) / precisionX;
        updateValue();
    }

    public void setY(double y) {
        double precisionY = 1.0 / this.incrY;
        this.y = Math.round(Math.max(this.minY, Math.min(this.maxY, y)) * precisionY) / precisionY;
        updateValue();
    }

    private void updateValue() {
        setValue(new Double[]{this.x, this.y});
    }
}
