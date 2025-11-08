package fr.flaily.xynon.module.settings.impl;

import fr.flaily.xynon.module.settings.Setting;
import lombok.Getter;

import java.util.function.Supplier;

@Getter
public class RangeSetting extends Setting<Double> {
    public double minimum, maximum, valueMin, valueMax;
    public double step;

    public RangeSetting(String name, double min, double max, double valueMin, double valueMax, Supplier<Boolean> supplier) {
        super(name, valueMin, supplier);
        this.valueMin = valueMin;
        this.valueMax = valueMax;
        this.minimum = min;
        this.maximum = max;
        this.step = 0.0;
    }
    public RangeSetting(String name, double min, double max, double valueMin, double valueMax, double step, Supplier<Boolean> supplier) {
        super(name, valueMin, supplier);
        this.valueMin = valueMin;
        this.valueMax = valueMax;
        this.minimum = min;
        this.maximum = max;
        this.step = step;
    }

    public void setValueMin(double value) {
        double s = step;
        double v = value;
        double r;
        r = s * Math.round(v / s);
        this.valueMin = r;
    }

    public void setValueMax(double value) {
        double s = step;
        double v = value;
        double r;
        r = s * Math.round(v / s);
        this.valueMax = r;
    }

    @Override
    public Double getValue() {
        return this.valueMin;
    }
}
