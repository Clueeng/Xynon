package fr.flaily.xynon.module.impl.player;

import org.lwjgl.input.Keyboard;

import best.azura.eventbus.handler.EventHandler;
import fr.flaily.xynon.events.game.EventMoveInput;
import fr.flaily.xynon.events.player.UpdateEvent;
import fr.flaily.xynon.module.FeatureInfo;
import fr.flaily.xynon.module.Module;
import fr.flaily.xynon.module.settings.impl.BooleanSetting;

@FeatureInfo(name = "Sprint", category = Module.Category.Player, key = -1)
public class Sprint extends Module {

    // Check EntityPlayerSP for omni
    // EntityPlayerSP#onLivingUpdate (line 877)
    public BooleanSetting omni = bool("Omni", true, () -> true);

    @EventHandler
    public void onUpdate(UpdateEvent event) {
        mc.gameSettings.keyBindSprint.pressed = true;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        mc.gameSettings.keyBindSprint.pressed = Keyboard.isKeyDown(mc.gameSettings.keyBindSprint.getKeyCode());
    }
}
