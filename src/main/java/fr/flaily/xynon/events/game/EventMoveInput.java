package fr.flaily.xynon.events.game;

import best.azura.eventbus.core.Event;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class EventMoveInput implements Event {
    public boolean sneak, jump;
    public float forward, sideways;
}
