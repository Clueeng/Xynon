package fr.flaily.xynon.events.render;

import best.azura.eventbus.core.Event;
import lombok.Getter;

public class WorldRenderEvent implements Event {
    @Getter
    private final float partialTicks;

    public WorldRenderEvent(float particleTicks) {
        this.partialTicks = particleTicks;
    }
}
