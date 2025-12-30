package fr.flaily.xynon.module.settings.impl;

import fr.flaily.xynon.module.settings.Setting;
import lombok.Getter;

import java.util.function.Supplier;

@Getter
public class RangeSetting extends Setting<Double> {
    public double minimum, maximum, valueLow, valueHigh;
    public double step;

    public RangeSetting(String name, double min, double max, double valueLow, double valueHigh, Supplier<Boolean> supplier) {
        super(name, valueLow, supplier);
        this.valueLow = valueLow;
        this.valueHigh = valueHigh;
        this.minimum = min;
        this.maximum = max;
        this.step = 0.0;
    }
    public RangeSetting(String name, double min, double max, double valueLow, double valueHigh, double step, Supplier<Boolean> supplier) {
        super(name, valueLow, supplier);
        this.valueLow = valueLow;
        this.valueHigh = valueHigh;
        this.minimum = min;
        this.maximum = max;
        this.step = step;
    }

    public void setValueLow(double value) {
        double s = step;
        if(s == 0) {
            valueLow = value;
            return; 
        }
        double v = value;
        double r;
        r = s * Math.round(v / s);
        this.valueLow = r;
    }

    public void setValueHigh(double value) {
        double s = step;
        if(s == 0) {
            valueHigh = value;
            return; 
        }

        double v = value;
        double r;
        r = s * Math.round(v / s);
        this.valueHigh = r;
    }

    @Override
    public Double getValue() {
        return this.valueLow;
    }

    public double getRandomValue() {
        return (getValueLow() + (Math.random() * (getValueHigh() - getValueLow())));
    }
}
