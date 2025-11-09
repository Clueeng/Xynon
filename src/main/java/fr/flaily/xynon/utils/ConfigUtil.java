package fr.flaily.xynon.utils;

import java.awt.List;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

import org.bouncycastle.jcajce.provider.asymmetric.dsa.DSASigner.stdDSA;

import com.google.common.collect.RangeSet;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import fr.flaily.xynon.Xynon;
import fr.flaily.xynon.module.Module;
import fr.flaily.xynon.module.settings.Setting;
import fr.flaily.xynon.module.settings.impl.BooleanSetting;
import fr.flaily.xynon.module.settings.impl.ColorSetting;
import fr.flaily.xynon.module.settings.impl.ModeSetting;
import fr.flaily.xynon.module.settings.impl.MultiSelectSetting;
import fr.flaily.xynon.module.settings.impl.NumberSetting;
import fr.flaily.xynon.module.settings.impl.RangeSetting;
import lombok.val;

public class ConfigUtil {

    public static File configFolder = new File(Xynon.INSTANCE.clientFolder, "configs");
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();


    public static void saveConfig(String configName) {
        if (!configFolder.exists()) configFolder.mkdirs();

        File configFile = new File(configFolder, configName + ".json");
        JsonArray modulesArray = new JsonArray();

        for (Module module : Xynon.INSTANCE.getModuleManager().getModules()) {
            JsonObject moduleObj = new JsonObject();
            moduleObj.addProperty("name", module.getName());
            moduleObj.addProperty("toggled", module.isToggled());
            moduleObj.addProperty("keyCode", module.getKeyCode());

            JsonArray settingsArray = new JsonArray();
            for (Setting<?> setting : module.getSettings()) {
                JsonObject settingObj = new JsonObject();
                settingObj.addProperty("name", setting.getName());
                settingObj.addProperty("type", setting.getClass().getSimpleName());

                if (setting instanceof MultiSelectSetting multi) {
                    JsonArray values = new JsonArray();
                    for (String selected : multi.getSelectedOptions()) values.add(selected);
                    settingObj.add("value", values);
                } else if (setting instanceof RangeSetting range) {
                    settingObj.addProperty("min", range.getValueMin());
                    settingObj.addProperty("max", range.getValueMax());
                } else if (setting instanceof ColorSetting color) {
                    settingObj.addProperty("value", color.getValue());
                } else {
                    settingObj.addProperty("value", String.valueOf(setting.getValue()));
                }

                settingsArray.add(settingObj);
            }

            moduleObj.add("settings", settingsArray);
            modulesArray.add(moduleObj);
        }

        try (var writer = new java.io.FileWriter(configFile)) {
            gson.toJson(modulesArray, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadConfig(String configName) {
        File configFile = new File(configFolder, configName + ".json");
        if (!configFile.exists()) {
            System.out.println("Config not found: " + configName);
            return;
        }

        try (FileReader reader = new FileReader(configFile)) {
            JsonArray modulesArray = JsonParser.parseReader(reader).getAsJsonArray();

            for (JsonElement element : modulesArray) {
                JsonObject moduleObj = element.getAsJsonObject();
                String moduleName = moduleObj.get("name").getAsString();

                Optional<Module> moduleOpt = Xynon.INSTANCE.getModuleManager()
                        .getModules()
                        .stream()
                        .filter(m -> m.getName().equalsIgnoreCase(moduleName))
                        .findFirst();

                if (moduleOpt.isEmpty()) continue;
                Module module = moduleOpt.get();

                if (moduleObj.has("keyCode"))
                    module.keyCode = moduleObj.get("keyCode").getAsInt();

                if (moduleObj.has("toggled") && moduleObj.get("toggled").getAsBoolean() != module.isToggled())
                    module.toggle();

                JsonArray settingsArray = moduleObj.getAsJsonArray("settings");
                for (JsonElement sElem : settingsArray) {
                    JsonObject sObj = sElem.getAsJsonObject();
                    String name = sObj.get("name").getAsString();
                    JsonElement value = sObj.get("value");

                    for (Setting<?> setting : module.getSettings()) {
                        if (setting.getName().equalsIgnoreCase(name)) {
                            if(setting instanceof RangeSetting rangeSetting) {
                                // well i dont think its best code but oh well
                                double min = sObj.get("min").getAsDouble();
                                double max = sObj.get("max").getAsDouble();
                                rangeSetting.setValueMax(max);
                                rangeSetting.setValueMin(min);

                            }else {
                                applySettingValue(setting, value);
                            }
                            break;
                        }
                    }
                }
            }

            System.out.println("Loaded config: " + configName);

        } catch (IOException | IllegalStateException e) {
            e.printStackTrace();
        }
    }
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static void applySettingValue(Setting setting, JsonElement valueElement) {
        try {
            if (setting instanceof BooleanSetting bool) {
                bool.setValue(valueElement.getAsBoolean());
            } else if (setting instanceof NumberSetting num) {
                num.setValue(valueElement.getAsDouble());
            } else if (setting instanceof RangeSetting range) {
                range.setValue(valueElement.getAsDouble());
            } else if (setting instanceof ModeSetting mode) {
                String saved = valueElement.getAsString();
                mode.setValue(saved);

                for (int i = 0; i < mode.modes.length; i++) {
                    if (mode.modes[i].equalsIgnoreCase(saved)) {
                        mode.current = i;
                        System.out.println("FOUND THE MODE " + mode.modes[mode.current]);
                        break;
                    }
                }
            } else if (setting instanceof ColorSetting color) {
                int rgb = valueElement.getAsInt();
                color.setValue(rgb);
                
                float[] hsb = java.awt.Color.RGBtoHSB(
                    (rgb >> 16) & 0xFF,
                    (rgb >> 8) & 0xFF,
                    rgb & 0xFF,
                    null
                );
                
                color.setHue(hsb[0] * 360.0f);
                color.setSaturation(hsb[1]);
                color.setBrightness(hsb[2]);

            } else if (setting instanceof MultiSelectSetting multi && valueElement.isJsonArray()) {
                java.util.List<String> selections = new ArrayList<>();
                for (JsonElement e : valueElement.getAsJsonArray())
                    selections.add(e.getAsString());
                multi.setSelectedOptions(selections);
            } else {
                Object current = setting.getValue();
                Object parsed = null;

                if (current instanceof Boolean) parsed = Boolean.parseBoolean(valueElement.getAsString());
                else if (current instanceof Integer) parsed = Integer.parseInt(valueElement.getAsString());
                else if (current instanceof Double) parsed = Double.parseDouble(valueElement.getAsString());
                else if (current instanceof Float) parsed = Float.parseFloat(valueElement.getAsString());
                else parsed = valueElement.getAsString();

                setting.setValue(parsed);
            }

        } catch (Exception e) {
            System.err.println("Failed to apply setting " + setting.getName() + ": " + e.getMessage());
        }
    }

}
