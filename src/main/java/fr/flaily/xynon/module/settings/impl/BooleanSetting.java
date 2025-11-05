package fr.flaily.xynon.module.settings.impl;

import fr.flaily.xynon.module.settings.Setting;
import lombok.Getter;

import java.util.function.Supplier;

@Getter
public class BooleanSetting extends Setting<Boolean> {
    public BooleanSetting(String name, boolean value, Supplier<Boolean> supplier) {
        super(name, value, supplier);
        this.name = name;
        this.value = value;
    }
    public BooleanSetting(String name, boolean value) {
        super(name, value, () -> true);
        this.name = name;
        this.value = value;
    }

    private boolean value;

    public void toggle() {
        this.value = !value;
    }
}
