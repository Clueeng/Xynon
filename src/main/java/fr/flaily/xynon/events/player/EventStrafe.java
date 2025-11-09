package fr.flaily.xynon.events.player;

import best.azura.eventbus.core.Event;
import lombok.Setter;
import lombok.Getter;


@Getter @Setter
public class EventStrafe implements Event {
    public float yaw;
    public EventStrafe(float yaw){
        this.yaw = yaw;
    }
}