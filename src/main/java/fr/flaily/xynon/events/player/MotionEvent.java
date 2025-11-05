package fr.flaily.xynon.events.player;

import best.azura.eventbus.core.Event;
import fr.flaily.xynon.events.EventTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter @Setter
public class MotionEvent implements Event {
    public double posX, posY, posZ;
    public boolean onGround;
    public EventTime time;
}
