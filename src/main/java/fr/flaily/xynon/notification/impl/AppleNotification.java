package fr.flaily.xynon.notification.impl;

import fr.flaily.xynon.notification.Notification;
import net.minecraft.client.gui.ScaledResolution;

public class AppleNotification extends Notification {
    
    public float x, y;
    public ScaledResolution sr = new ScaledResolution(mc);

    public AppleNotification(String title, String description, long delay) {
        super(title, description, delay);

        this.x = sr.getScaledWidth() / 2f;
        this.y = -100;
    }
    
    @Override
    public void render() {
        
    }
}
