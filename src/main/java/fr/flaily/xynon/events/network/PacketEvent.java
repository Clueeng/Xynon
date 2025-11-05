package fr.flaily.xynon.events.network;

import best.azura.eventbus.events.CancellableEvent;
import lombok.AllArgsConstructor;
import net.minecraft.network.Packet;

@AllArgsConstructor
public class PacketEvent extends CancellableEvent {
    public Packet packet;
    public PacketType type;

    public enum PacketType {
        CLIENT_ORIGIN,
        SERVER_ORIGIN
    }
}
