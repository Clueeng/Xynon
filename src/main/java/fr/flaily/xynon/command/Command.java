package fr.flaily.xynon.command;

import java.util.ArrayList;

import fr.flaily.xynon.utils.Utils;

public abstract class Command implements Utils {
    public String name;

    public Command(String name) {
        this.name = name;
    }

    public abstract void execute(String[] args);
    public abstract String[] helpMessage();
}
