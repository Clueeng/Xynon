package fr.flaily.xynon.module.impl.render.overlay;

import best.azura.eventbus.handler.EventHandler;
import fr.flaily.xynon.Xynon;
import fr.flaily.xynon.events.network.PacketEvent;
import fr.flaily.xynon.events.render.ScreenEvent;
import fr.flaily.xynon.module.FeatureInfo;
import fr.flaily.xynon.module.Module;
import fr.flaily.xynon.module.impl.render.Render;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.S0CPacketSpawnPlayer;
import net.minecraft.network.play.server.S13PacketDestroyEntities;

import java.awt.*;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Set;
import java.util.stream.Stream;

@FeatureInfo(name = "Overlay", category = Module.Category.Render)
public class Overlay extends Module implements Render {

    public HashMap<Integer, BedwarsPlayer> players = new HashMap<>();
    public HashMap<Integer, BedwarsPlayer> readyToAdd = new HashMap<>();

    @EventHandler
    public void onPacket(final PacketEvent event) {
        if(event.getPacket() instanceof S0CPacketSpawnPlayer spawn) {
            BedwarsPlayer player = new BedwarsPlayer(spawn);

            if(mc.thePlayer.ticksExisted == 0) {
                readyToAdd.put(player.entityId, player);
            }else{
                players.put(player.entityId, player);
            }
        }
        if(event.getPacket() instanceof S13PacketDestroyEntities destroy) {
            for(int playerId : destroy.getEntityIDs()) {
//                players.remove(playerId);
            }
        }
        players.forEach((id, player) -> player.updateState(event));
    }

    @EventHandler
    public void onRender(ScreenEvent event) {
        if(mc.thePlayer.ticksExisted == 0) {
            players.clear();
            return;
        }
        if(!readyToAdd.isEmpty() && mc.thePlayer.ticksExisted % 10 == 2) {
            players.putAll(readyToAdd);
            readyToAdd.clear();
        }

        int index = 0;
        Stream<Integer> keys = players.keySet().stream().sorted(Comparator.comparingDouble(key -> {
            Color c = players.get(key).getTeamColor();
            return 0.2126 * c.getRed()
                    + 0.7152 * c.getGreen()
                    + 0.0722 * c.getBlue();
        }));

        for(int playerId : keys.toList()) {
            BedwarsPlayer player = players.get(playerId);
            if(!player.isBedwars()) continue;

            ItemStack hand = player.entityStuff[0];
            String test = player.entityId + " = " + player.getName();
            if(hand != null) {
                test += " " + hand.getDisplayName();
            }
//            test += " BW:" + player.isBedwars();
//            test += " col=" + player.getTeamColor();
            medium.drawStringWithShadow(test, 4, 100 + (12 * index), player.getTeamColor().getRGB());

            index++;
        }
    }

}
