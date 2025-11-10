package fr.flaily.xynon.module.settings.impl;

import fr.flaily.xynon.module.settings.Setting;

import java.util.function.Supplier;

public class NumberSetting extends Setting<Double> {
    public double minimum, maximum, value;
    public double step;

    public NumberSetting(String name, double min, double max, double value, Supplier<Boolean> supplier) {
        super(name, value, supplier);
        this.value = value;
        setValue(value);
        this.minimum = min;
        this.maximum = max;
        this.step = 0.0;
    }
    public NumberSetting(String name, double min, double max, double value, double step, Supplier<Boolean> supplier) {
        super(name, value, supplier);
        this.value = value;
        setValue(value);
        this.minimum = min;
        this.maximum = max;
        this.step = step;
    }

    public void setValue(double value) {
        double s = step;
        if(s == 0.0) {
            this.value = value;
            return;
        }
        double v = value;
        double r;
        r = s * Math.round(v / s);
        System.out.println("v="+value);
        System.out.println("s="+r);
        this.value = r;
    }

    @Override
    public Double getValue() {
        return value;
    }
}
