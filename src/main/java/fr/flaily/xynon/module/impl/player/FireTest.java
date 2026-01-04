package fr.flaily.xynon.module.impl.player;

import best.azura.eventbus.handler.EventHandler;
import fr.flaily.xynon.Xynon;
import fr.flaily.xynon.events.network.PacketEvent;
import fr.flaily.xynon.module.FeatureInfo;
import fr.flaily.xynon.module.Module;
import fr.flaily.xynon.module.settings.impl.NumberSetting;
import fr.flaily.xynon.utils.PacketUtil;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;

@FeatureInfo(name = "FireTest", category = Module.Category.Player)
public class FireTest extends Module {
    public NumberSetting n = num("Fbs", 1.0, 100.0, 10.0, () -> true);

    @EventHandler
    public void onPacket(PacketEvent event) {
        if(event.getPacket() instanceof C08PacketPlayerBlockPlacement) {
            Xynon.INSTANCE.gameLogger().sendLog("test");
            for(int i = 0 ; i < n.getValue().intValue(); i++) {
                PacketUtil.sendNoPacketEvent(event.getPacket());
            }
        }
    }

}
