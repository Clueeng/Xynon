package fr.flaily.xynon.events.render;

import best.azura.eventbus.core.Event;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class OverScreenEvent implements Event {
    public float partialTicks;
}
