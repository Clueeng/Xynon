package fr.flaily.xynon.notification.impl;

import fr.flaily.xynon.module.impl.render.Render;
import fr.flaily.xynon.notification.Notification;
import fr.flaily.xynon.notification.NotificationType;
import fr.flaily.xynon.utils.AnimFloat;
import fr.flaily.xynon.utils.AnimFloat.Easing;
import net.minecraft.client.gui.ScaledResolution;

public class StackableNotification extends Notification implements Render {

    private float x, y, width, height;
    private boolean canDisappear = false;
    private final ScaledResolution sr = new ScaledResolution(mc);

    public StackableNotification(String title, String description, long delay) {
        super(title, description, delay, NotificationType.STACKABLE);
        this.animFloat = new AnimFloat(0.0f, 0.07f, Easing.EASE_IN_OUT);
        this.animFloat.setTarget(1.0f);

        this.width = medium.getWidth(description) + 20;
        this.height = 32;
    }

    @Override
    public void render(float partialTicks, int index) {
        this.animFloat.update(partialTicks);

        // offset by index in stack
        float offset = (height + 6) * index;
        float targetY = 20 + offset;

        this.x = sr.getScaledWidth() - width - 10;
        this.y = targetY * animFloat.getValue();

        medium.drawString(description, x, y, -1);

        if (animFloat.getTarget() >= 0.99f) {
            if (!canDisappear) this.created = System.currentTimeMillis();
            this.canDisappear = true;
            if (hasElapsed()) this.animFloat.setTarget(0.0f);
        }
    }

    @Override
    public boolean shouldDisappear() {
        return hasElapsed() && animFloat.getValue() <= 0.01f;
    }
}
