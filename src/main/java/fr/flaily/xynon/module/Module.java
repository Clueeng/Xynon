package fr.flaily.xynon.module;

import fr.flaily.xynon.Xynon;
import fr.flaily.xynon.module.impl.pvp.ModulePvP;
import fr.flaily.xynon.module.settings.Setting;
import fr.flaily.xynon.utils.AnimFloat;
import fr.flaily.xynon.utils.Utils;
import lombok.Getter;

import java.util.ArrayList;

@Getter
public class Module implements SettingInitializer, Utils {
    public enum Category {
        Combat,
        Render,
        Player,
        Movement,
        Macros;
    }

    public Category category;
    public int keyCode;
    public String name;
    public boolean toggled;
    // Used for per-module animations in scenarios we do not have a separate class like ModuleFrame
    // I don't think it'd be great to have a new class just holding a module and a float
    public AnimFloat modAnimation = new AnimFloat(0.0f, 0.07f, AnimFloat.Easing.EASE_IN_OUT);


    // ------------------ Settings --------------------- //
    private ArrayList<Setting<?>> settings = new ArrayList<>();

    @Override
    public ArrayList<Setting<?>> getSettings() {
        return settings;
    }

    public String getDisplayName() {
        return name;
    }

    public String getListName() {
        return this.name;
    }

    public Module() {
        if(this instanceof ModulePvP) return;

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
        getModAnimation().setTarget(1.0f);
        Xynon.INSTANCE.getEventBus().subscribe(this);
    }

    public void onDisable() {
        getModAnimation().setTarget(0.0f);
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

}
