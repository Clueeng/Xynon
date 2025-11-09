package fr.flaily.xynon.module;

import fr.flaily.xynon.Xynon;
import fr.flaily.xynon.module.impl.TestModule;
import fr.flaily.xynon.module.impl.combat.Killaura;
import fr.flaily.xynon.module.impl.movement.Flight;
import fr.flaily.xynon.module.impl.player.Noslow;
import fr.flaily.xynon.module.impl.player.Sprint;
import fr.flaily.xynon.module.impl.render.ClickGUI;
import fr.flaily.xynon.module.impl.render.HUD;
import fr.flaily.xynon.utils.font.CustomFontRenderer;
import net.minecraft.client.gui.FontRenderer;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ModuleManager {

    public ArrayList<Module> modules = new ArrayList<>();

    public ModuleManager() {
        // Register modules

        // Movement

        // Render
//        register(new TestModule());
//        register(new TestModule());
//        register(new TestModule());
        register(new ClickGUI());
        register(new Flight());
        register(new TestModule());
        register(new HUD());
        register(new Killaura());
        register(new Noslow());
        register(new Sprint());

        Xynon.INSTANCE.debugLogger().sendLog("Finished registering modules");
    }

    public ArrayList<Module> getModulesConditionally(Predicate<Module> predicate) {
        return new ArrayList<Module>((Collection<Module>) modules.stream().filter(predicate));
    }

    public ArrayList<Module> lengthSortedModules(FontRenderer c, ArrayList<Module> mods) {
        mods.sort(Comparator.comparingDouble(m ->
                c.getStringWidth(((Module)m).getDisplayName())
        ).reversed());
        return mods;
    }
    public ArrayList<Module> lengthSortedModules(CustomFontRenderer c, ArrayList<Module> mods) {
        mods.sort(Comparator.comparingDouble(m ->
                c.getWidth(((Module)m).getListName())
        ).reversed());
        return mods;
    }

    private void register(Module module) {
        Xynon.INSTANCE.debugLogger().sendLog("Registering " + module);
        modules.add(module);
    }

    public void checkForInput(int keyCode) {
        modules.stream().filter(m -> m.keyCode == keyCode).forEach(Module::toggle);
    }

    public ArrayList<Module> getModules(Module.Category... categories) {
        if(categories == null || categories.length == 0) {
            return new ArrayList<>(modules);
        }
        List<Module.Category> categoryList = Arrays.asList(categories);
        return modules.stream()
                .filter(module -> categoryList.contains(module.getCategory()))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public <T extends Module> T getModule(Class<T> tClass) {
        return (T) modules.stream().filter(mod -> mod.getClass().equals(tClass)).findFirst().orElse(null);
    }

}
