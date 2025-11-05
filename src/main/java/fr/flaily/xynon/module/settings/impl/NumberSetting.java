package fr.flaily.xynon.module.settings.impl;

import fr.flaily.xynon.module.settings.Setting;

import java.util.function.Supplier;

public class NumberSetting extends Setting<Double> {
    public double minimum, maximum, value;

    public NumberSetting(String name, double min, double max, double value, Supplier<Boolean> supplier) {
        super(name, value, supplier);
        this.value = value;
        this.minimum = min;
        this.maximum = max;
    }

    @Override
    public Double getValue() {
        return value;
    }
}
