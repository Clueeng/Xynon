package fr.flaily.xynon.module;

import fr.flaily.xynon.Xynon;
import fr.flaily.xynon.module.impl.TestModule;
import fr.flaily.xynon.module.impl.movement.FlightModule;
import fr.flaily.xynon.module.impl.render.ClickGUIModule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
        register(new ClickGUIModule());
        register(new FlightModule());

        Xynon.INSTANCE.debugLogger().sendLog("Finished registering modules");
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

}
