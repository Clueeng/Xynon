package fr.flaily.xynon.events.network;

import best.azura.eventbus.events.CancellableEvent;
import lombok.AllArgsConstructor;
import net.minecraft.network.Packet;

@AllArgsConstructor
public class PacketEvent extends CancellableEvent {
    public Packet getPacket() {
        return packet;
    }

    public void setPacket(Packet packet) {
        this.packet = packet;
    }

    public Packet packet;
    public PacketType type;

    public enum PacketType {
        CLIENT_ORIGIN,
        SERVER_ORIGIN
    }

    public boolean isServer() {
        return this.type == PacketType.SERVER_ORIGIN;
    }
    public boolean isClient() {
        return this.type == PacketType.CLIENT_ORIGIN;
    }
}
