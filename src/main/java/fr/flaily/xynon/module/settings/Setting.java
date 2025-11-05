package fr.flaily.xynon.module.settings;

import fr.flaily.xynon.module.Module;
import lombok.Getter;
import lombok.Setter;

import java.util.function.Predicate;
import java.util.function.Supplier;

@Getter
public class Setting<T> {
    // Constructor
    public Setting(String name, T value, Supplier<Boolean> supplier) {
        this.name = name;
        this.value = value;
        this.supplier = supplier;
    }

    // Supplier for clickgui
    private Supplier<Boolean> supplier;
    @Setter
    private T value;

    public String name, description;
    public Setting<?> parentSetting;

    private Predicate<Module> canShow = (module) -> true;

    public boolean canShow() {
        if(parentSetting == null) {
            return supplier.get();
        }else{
            return supplier.get() && parentSetting.supplier.get();
        }
    }

    public <E> E setCanShow(Predicate<Module> predicate) {
        this.canShow = predicate;
        return (E) this;
    }
}
