package fr.flaily.xynon.module.impl.combat;

import best.azura.eventbus.handler.EventHandler;
import fr.flaily.xynon.Xynon;
import fr.flaily.xynon.events.EventTime;
import fr.flaily.xynon.events.game.EventOverrideInput;
import fr.flaily.xynon.events.network.PacketEvent;
import fr.flaily.xynon.events.player.UpdateEvent;
import fr.flaily.xynon.module.FeatureInfo;
import fr.flaily.xynon.module.Module;
import fr.flaily.xynon.module.settings.impl.ModeSetting;
import fr.flaily.xynon.module.settings.impl.RangeSetting;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import org.lwjgl.input.Keyboard;

@FeatureInfo(name = "Velocity", category = Module.Category.Combat)
public class Velocity extends Module {
    public ModeSetting veloMode = mode("Mode", "Cancel", "Cancel", "Jump Reset");
    public RangeSetting x = range("X", 0.0f, 100.0f, 50.0f, 70.0f, 0.5f, () -> veloMode.is("Cancel"));
    public RangeSetting y = range("Y", 0.0f, 100.0f, 50.0f, 70.0f, 0.5f, () -> veloMode.is("Cancel"));

    boolean resetState;

    @EventHandler
    public void onPacket(PacketEvent e) {
        if(e.isServer()) {
            Packet p = e.getPacket();
            if(p instanceof S12PacketEntityVelocity velocity) {
                if(velocity.getEntityID() == mc.thePlayer.getEntityId()) {
                    switch (veloMode.getValue()) {
                        case "Cancel":{
                            float threshold = 5.0f;
                            boolean isZeroX = x.getValueHigh() <= threshold && x.getValueLow() <= threshold;
                            boolean isZeroY = y.getValueHigh() <= threshold && y.getValueLow() <= threshold;
                            if(isZeroX && isZeroY) {
                                e.setCancelled(true);
                            }else{
                                int[] oVelocity = new int[] {
                                        velocity.getMotionX(),
                                        velocity.getMotionY(),
                                        velocity.getMotionZ()
                                };
                                int[] tVelocity = transformVelocity(oVelocity);
                                velocity.setMotionX(tVelocity[0]);
                                velocity.setMotionY(tVelocity[1]);
                                velocity.setMotionZ(tVelocity[2]);
                            }
                            break;
                        }
                        case "Jump Reset": {
                            resetState = true;
                            break;
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void overrideInput(EventOverrideInput event) {
        if(resetState) {
            event.choke(mc.gameSettings.keyBindJump);
        }
    }

    @EventHandler
    public void onUpdate(UpdateEvent e) {
        if(e.time == EventTime.PRE) {
            if(resetState) {
                if(mc.thePlayer.onGround) {
                    mc.gameSettings.keyBindForward.pressed = true;
                    mc.gameSettings.keyBindJump.pressed = true;
                }
            }
        }
        if(e.time == EventTime.POST) {
            if(resetState) {
                mc.gameSettings.keyBindForward.pressed = Keyboard.isKeyDown(mc.gameSettings.keyBindForward.getKeyCode());
            }
            resetState = false;
        }
    }

    private int[] transformVelocity(int[] oVelocity) {
        return new int[] {
                (int) (oVelocity[0] * (x.getRandomValue() / 100f)),
                (int) (oVelocity[1] * (y.getRandomValue() / 100f)),
                (int) (oVelocity[2] * (x.getRandomValue() / 100f))
        };
    }

}
