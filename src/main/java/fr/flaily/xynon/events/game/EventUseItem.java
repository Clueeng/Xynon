package fr.flaily.xynon.events.game;

import best.azura.eventbus.events.CancellableEvent;
import fr.flaily.xynon.utils.Utils;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class EventUseItem extends CancellableEvent implements Utils {

    public void forceUse() {
        if(mc.thePlayer.getHeldItem() == null) return;
        mc.playerController.sendUseItem(mc.thePlayer, mc.theWorld, mc.thePlayer.getHeldItem());
    }
}
