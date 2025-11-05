package fr.flaily.xynon.events.player;

import best.azura.eventbus.core.Event;
import fr.flaily.xynon.events.EventTime;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class UpdateEvent implements Event {
    public EventTime time;
}
