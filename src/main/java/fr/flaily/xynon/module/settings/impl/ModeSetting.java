package fr.flaily.xynon.module.settings.impl;

import fr.flaily.xynon.module.settings.Setting;

import java.util.function.Supplier;

public class ModeSetting extends Setting<String> {
    public int current, oldCurrent;
    public String[] modes;

    public ModeSetting(String name, String value, Supplier<Boolean> supplier, String... modes) {
        super(name, value, supplier);

        this.modes = new String[modes.length];
        for(int i = 0; i < modes.length; i++) {
            if(modes[i].equalsIgnoreCase(value)) {
                current = i;
                oldCurrent = i;
            }

            this.modes[i] = modes[i];
        }
    }

    public boolean is(String mode) {
        return modes[current].equalsIgnoreCase(mode);
    }

    public String getCurrent() {
        return this.modes[current];
    }

    public void cycle(int mb) {
        switch (mb) {
            case 0: {
                this.current += 1;
                if(this.current >= this.modes.length) this.current = 0;
                break;
            }
            case 1: {
                this.current -= 1;
                if(this.current < 0) this.current = this.modes.length - 1;
                break;
            }
        }
        if(onUpdate != null) {
            onUpdate.run();
        }
    }

    private Runnable onUpdate;
    public void onValueUpdate(Runnable runnable) {
        onUpdate = runnable;
    }
}
