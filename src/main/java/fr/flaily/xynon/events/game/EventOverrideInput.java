package fr.flaily.xynon.events.game;

import best.azura.eventbus.core.Event;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;

import java.security.Key;
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
    public void invertMovement(Minecraft mc) {
        KeyBinding forward = mc.gameSettings.keyBindForward;
        KeyBinding back    = mc.gameSettings.keyBindBack;
        KeyBinding left    = mc.gameSettings.keyBindLeft;
        KeyBinding right   = mc.gameSettings.keyBindRight;

        swap(forward, back);
        swap(left, right);
    }

    public void resetState(Minecraft mc) {
        mc.gameSettings.keyBindForward.pressed = Keyboard.isKeyDown(mc.gameSettings.keyBindForward.getKeyCode());
        mc.gameSettings.keyBindBack.pressed = Keyboard.isKeyDown(mc.gameSettings.keyBindBack.getKeyCode());
        mc.gameSettings.keyBindLeft.pressed =  Keyboard.isKeyDown(mc.gameSettings.keyBindLeft.getKeyCode());
        mc.gameSettings.keyBindRight.pressed =  Keyboard.isKeyDown(mc.gameSettings.keyBindRight.getKeyCode());
    }

    private void swap(KeyBinding a, KeyBinding b) {
        boolean aPressed = Keyboard.isKeyDown(a.getKeyCode());
        boolean bPressed = Keyboard.isKeyDown(b.getKeyCode());

        a.setPressed(bPressed);
        b.setPressed(aPressed);
    }
}
