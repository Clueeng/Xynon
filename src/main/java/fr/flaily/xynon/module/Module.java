package fr.flaily.xynon.module;

import com.google.common.collect.Lists;
import fr.flaily.xynon.Xynon;
import fr.flaily.xynon.click.classic.frame.module.settings.impl.MultiSelectFrame;
import fr.flaily.xynon.module.settings.Setting;
import fr.flaily.xynon.module.settings.impl.*;
import lombok.Getter;
import net.minecraft.client.Minecraft;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

@Getter
public class Module {
    public enum Category {
        Combat,
        Render,
        Movement;
    }

    public Category category;
    public int keyCode;
    public String name;
    public boolean toggled;

    public String getDisplayName() {
        return name;
    }

    public Module() {
        if(this.getClass().isAnnotationPresent(FeatureInfo.class)) {
            FeatureInfo info = getClass().getAnnotation(FeatureInfo.class);
            this.name = info.name();
            this.keyCode = info.key();
            this.category = info.category();
        }else{
            throw new RuntimeException("Feature does not contain any data");
        }
    }

    public void onEnable() {
        Xynon.INSTANCE.getEventBus().subscribe(this);
    }

    public void onDisable() {
        Xynon.INSTANCE.getEventBus().unsubscribe(this);
    }

    public void toggle() {
        toggled = !toggled;
        if(toggled) onEnable();
        else onDisable();
    }

    public void disable() {
        this.toggled = false;
        onDisable();
    }

    @Override
    public String toString() {
        return getDisplayName();
    }

    public Minecraft mc = Minecraft.getMinecraft();



    // Settings
    private ArrayList<Setting<?>> settings = new ArrayList<>();

    public void add(Setting<?> setting) {
        this.settings.add(setting);
    }

    public BooleanSetting bool(String name, boolean value, Supplier<Boolean> supplier) {
        BooleanSetting set = new BooleanSetting(name, value, supplier);
        add(set);
        return set;
    }
    public NumberSetting num(String name, double min, double max, double val, Supplier<Boolean> supplier) {
        NumberSetting num = new NumberSetting(name, min, max, val, supplier);
        add(num);
        return num;
    }
    public MultiSelectSetting multi(String name, List<String> options, List<String> defaultValues, Supplier<Boolean> supplier) {
        MultiSelectSetting setting = new MultiSelectSetting(name, defaultValues, supplier, defaultValues.toArray(new String[0]));
        add(setting);
        return setting;
    }
    public ColorSetting color(String name, float a, float b, float c, int alpha) {
        ColorSetting colorSetting = new ColorSetting(name, a, b, c, alpha);
        add(colorSetting);
        return colorSetting;
    }
    public ColorSetting color(String name, float a, float b, float c, int alpha, Supplier<Boolean> supplier) {
        ColorSetting colorSetting = new ColorSetting(name, a, b, c, alpha, supplier);
        add(colorSetting);
        return colorSetting;
    }
    public ModeSetting mode(String name, String value, String... modes) {
        ModeSetting modeSetting = new ModeSetting(name, value, () -> true, modes);
        add(modeSetting);
        return modeSetting;
    }
    public ModeSetting mode(String name, String value, Supplier<Boolean> supplier, String... modes) {
        ModeSetting modeSetting = new ModeSetting(name, value, supplier, modes);
        add(modeSetting);
        return modeSetting;
    }
}
