package fr.flaily.xynon.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PacketUtil implements Utils {

    public static void sendNoPacketEvent(Packet p){
        Minecraft.getMinecraft().thePlayer.sendQueue.addToSendQueueNoEvent(p);
    }

    private static List<Class> blinkPackets = Arrays.asList(C03PacketPlayer.class, C03PacketPlayer.C04PacketPlayerPosition.class,
            C03PacketPlayer.C05PacketPlayerLook.class, C03PacketPlayer.C06PacketPlayerPosLook.class);

    public static boolean isPacketBlink(Packet p){
        return blinkPackets.contains(p.getClass());
    }


}
