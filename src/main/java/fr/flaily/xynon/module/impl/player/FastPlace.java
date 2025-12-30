package fr.flaily.xynon.module.impl.player;

import best.azura.eventbus.handler.EventHandler;
import fr.flaily.xynon.events.game.EventClick;
import fr.flaily.xynon.events.network.PacketEvent;
import fr.flaily.xynon.module.FeatureInfo;
import fr.flaily.xynon.module.Module;
import fr.flaily.xynon.module.settings.impl.NumberSetting;
import net.minecraft.event.ClickEvent;

@FeatureInfo(name = "Fast Place", category = Module.Category.Player)
public class FastPlace extends Module {

    public NumberSetting delay = num("Delay", 0.0f, 6.0f, 0.0f, 1.0f, () -> true);

    @EventHandler
    public void onClick(EventClick event) {
        if(event.clickType == EventClick.ClickType.RIGHT) {
            event.setResetDelay(delay.getValue().intValue());
        }
    }
}
