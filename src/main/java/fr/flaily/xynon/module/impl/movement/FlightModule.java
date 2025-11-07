package fr.flaily.xynon.module.impl.movement;

import best.azura.eventbus.handler.EventHandler;
import fr.flaily.xynon.events.EventTime;
import fr.flaily.xynon.events.player.MotionEvent;
import fr.flaily.xynon.module.FeatureInfo;
import fr.flaily.xynon.module.Module;
import fr.flaily.xynon.module.settings.impl.ModeSetting;
import fr.flaily.xynon.module.settings.impl.NumberSetting;
import fr.flaily.xynon.utils.MotionUtils;
import org.lwjgl.input.Keyboard;

@FeatureInfo(name = "Flight", key = Keyboard.KEY_G, category = Module.Category.Movement)
public class FlightModule extends Module {

    public ModeSetting modes = mode("Mode", "Motion", "Motion", "Grim");
    public NumberSetting speed = num("Speed", 0.2f, 2.0f, 1.0f, 0.1, () -> modes.is("Motion"));

    @EventHandler
    public void onMotion(MotionEvent event) {
        switch (modes.getValue()) {
            case "Motion": {
                if(event.getTime() == EventTime.PRE) {
                    mc.thePlayer.motionY = 0.0f;
                    if(mc.gameSettings.keyBindJump.isKeyDown()) {
                        mc.thePlayer.motionY += (speed.getValue() * 0.5f);
                    }
                    if(mc.gameSettings.keyBindSneak.isKeyDown()) {
                        mc.thePlayer.motionY -= (speed.getValue() * 0.5f);
                    }
                    if(MotionUtils.isMovingKeybind()) {
                        MotionUtils.strafe(speed.getValue());
                    }else{
                        MotionUtils.strafeNoMove(0f);
                    }
                }
                break;
            }
        }
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }
}
