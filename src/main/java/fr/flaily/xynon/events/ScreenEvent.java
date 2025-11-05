package fr.flaily.xynon.events;

import best.azura.eventbus.events.CancellableEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ScreenEvent extends CancellableEvent {
    private float partialTicks;
}
