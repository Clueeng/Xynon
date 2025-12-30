package fr.flaily.xynon.command;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import fr.flaily.xynon.command.impl.ConfigCommand;
import fr.flaily.xynon.command.impl.NotificationCommand;
import lombok.Getter;

public class CommandManager {

    @Getter
    public ArrayList<Command> commands = new ArrayList<>();

    public CommandManager() {
        commands.add(new ConfigCommand());
        commands.add(new NotificationCommand());
    }

    public Command getCommand(String name) {
        for(Command c : getCommands()) {
            if(c.name.equalsIgnoreCase(name)) return c;
        }
        return null;
    }

}
