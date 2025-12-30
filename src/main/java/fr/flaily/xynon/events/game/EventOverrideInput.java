package fr.flaily.xynon.events.game;

import best.azura.eventbus.core.Event;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;

import java.util.ArrayList;

public class EventOverrideInput implements Event {
    ArrayList<Integer> chokedInputs = new ArrayList<>();

    public EventOverrideInput() {

    }

    public void choke(KeyBinding key) {
        key.pressTime = 0;
        key.setPressed(false);
        this.chokedInputs.add(key.getKeyCode());
    }
    public void send(KeyBinding key) {
        if(this.chokedInputs.contains(key.getKeyCode())) {
            this.chokedInputs.remove(key.getKeyCode());
        }
        key.setPressed(true);
    }
    public boolean isChoked(KeyBinding key) {
        return this.chokedInputs.contains(key.getKeyCode());
    }
}
