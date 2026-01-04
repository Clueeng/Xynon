package fr.flaily.xynon.events.render;

import best.azura.eventbus.core.Event;
import net.minecraft.entity.EntityLivingBase;

import java.awt.*;

public class EventTeamColor implements Event {
    public Color oColor;
    public EntityLivingBase entityIn;

    public Color getoColor() {
        return oColor;
    }

    public void setoColor(Color oColor) {
        this.oColor = oColor;
    }

    public EntityLivingBase getEntityIn() {
        return entityIn;
    }

    public void setEntityIn(EntityLivingBase entityIn) {
        this.entityIn = entityIn;
    }

    public EventTeamColor(EntityLivingBase entityIn, Color oColor) {
        this.oColor = oColor;
        this.entityIn = entityIn;
    }
}
