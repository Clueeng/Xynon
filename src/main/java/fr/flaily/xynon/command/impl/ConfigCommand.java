package fr.flaily.xynon.command.impl;

import java.util.ArrayList;
import java.util.Arrays;

import fr.flaily.xynon.Xynon;
import fr.flaily.xynon.command.Command;
import fr.flaily.xynon.utils.ConfigUtil;
import net.minecraft.util.EnumChatFormatting;

public class ConfigCommand extends Command {

    public ConfigCommand() {
        super("Config");
    }

    @Override
    public void execute(String[] args) {
        if(args.length == 0) return;

        String sub = args[0];
        switch (sub) {
            case "save": {
                if(args.length == 1) {
                    Xynon.INSTANCE.gameLogger().sendLog("Arg. missing: config name");
                    return;
                }
                String name = args[1];
                ConfigUtil.saveConfig(name);
                    Xynon.INSTANCE.gameLogger().sendLog("Saved config to " + name + ".json");
                break;
            }
            case "load": {
                if(args.length == 1) {
                    Xynon.INSTANCE.gameLogger().sendLog("Arg. missing: config name");
                    return;
                }
                String name = args[1];
                ConfigUtil.loadConfig(name);
                Xynon.INSTANCE.gameLogger().sendLog("Loaded config " + name + ".json");
                break;
            }
            case "test": {
                ConfigUtil.loadConfig("test");
                break;
            }
            case "list": {
                Xynon.INSTANCE.gameLogger().sendLog(EnumChatFormatting.BOLD + "List of saved configs");
                for(String config : ConfigUtil.getConfigs()) {
                    Xynon.INSTANCE.gameLogger().sendLog(" - " + config.replace(".json", ""));
                }
                break;
            }
            
            default:
                Xynon.INSTANCE.gameLogger().sendLog("Invalid sub command");
                break;
        }
    }

    @Override
    public String[] helpMessage() {
        return new String[] {
            "Config command:",
            "Used to manage your configs",
            "Usage: .config <save/load> <name>"
        };
    }

    

}
