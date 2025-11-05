package fr.flaily.xynon.events.player;

import best.azura.eventbus.core.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter @Setter
public class RotationEvent implements Event {
    public float yaw, pitch, prevYaw, prevPitch;
}
