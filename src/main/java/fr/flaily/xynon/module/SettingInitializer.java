package fr.flaily.xynon.module;

import java.util.List;
import java.util.function.Supplier;

import fr.flaily.xynon.module.settings.Setting;
import fr.flaily.xynon.module.settings.impl.BooleanSetting;
import fr.flaily.xynon.module.settings.impl.ColorSetting;
import fr.flaily.xynon.module.settings.impl.ModeSetting;
import fr.flaily.xynon.module.settings.impl.MultiSelectSetting;
import fr.flaily.xynon.module.settings.impl.NumberSetting;
import fr.flaily.xynon.module.settings.impl.RangeSetting;

public interface SettingInitializer {

    List<Setting<?>> getSettings();

    default void add(Setting<?> setting) {
        getSettings().add(setting);
    }

    default BooleanSetting bool(String name, boolean value, Supplier<Boolean> supplier) {
        BooleanSetting set = new BooleanSetting(name, value, supplier);
        add(set);
        return set;
    }

    default NumberSetting num(String name, double min, double max, double val, Supplier<Boolean> supplier) {
        NumberSetting num = new NumberSetting(name, min, max, val, supplier);
        add(num);
        return num;
    }

    default NumberSetting num(String name, double min, double max, double val, double step, Supplier<Boolean> supplier) {
        NumberSetting num = new NumberSetting(name, min, max, val, step, supplier);
        add(num);
        return num;
    }

    default MultiSelectSetting multi(String name, List<String> options, List<String> defaultValues, Supplier<Boolean> supplier) {
        MultiSelectSetting setting = new MultiSelectSetting(name, defaultValues, supplier, defaultValues.toArray(new String[0]));
        add(setting);
        return setting;
    }

    default ColorSetting color(String name, float a, float b, float c, int alpha) {
        ColorSetting colorSetting = new ColorSetting(name, a, b, c, alpha);
        add(colorSetting);
        return colorSetting;
    }

    default ColorSetting color(String name, float a, float b, float c, int alpha, Supplier<Boolean> supplier) {
        ColorSetting colorSetting = new ColorSetting(name, a, b, c, alpha, supplier);
        add(colorSetting);
        return colorSetting;
    }

    default ModeSetting mode(String name, String value, String... modes) {
        ModeSetting modeSetting = new ModeSetting(name, value, () -> true, modes);
        add(modeSetting);
        return modeSetting;
    }

    default ModeSetting mode(String name, String value, Supplier<Boolean> supplier, String... modes) {
        ModeSetting modeSetting = new ModeSetting(name, value, supplier, modes);
        add(modeSetting);
        return modeSetting;
    }

    default RangeSetting range(String name, double min, double max, double valueMin, double valueMax, Supplier<Boolean> supplier) {
        RangeSetting rangeSetting = new RangeSetting(name, min, max, valueMin, valueMax, supplier);
        add(rangeSetting);
        return rangeSetting;
    }

    default RangeSetting range(String name, double min, double max, double valueMin, double valueMax, double step, Supplier<Boolean> supplier) {
        RangeSetting rangeSetting = new RangeSetting(name, min, max, valueMin, valueMax, step, supplier);
        add(rangeSetting);
        return rangeSetting;
    }


}
