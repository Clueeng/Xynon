package fr.flaily.xynon.notification.impl;

import fr.flaily.xynon.module.impl.render.Render;
import fr.flaily.xynon.notification.Notification;
import fr.flaily.xynon.notification.NotificationType;
import fr.flaily.xynon.utils.AnimFloat;
import fr.flaily.xynon.utils.AnimFloat.Easing;
import net.minecraft.client.gui.ScaledResolution;

public class LoneNotification extends Notification implements Render {

    private float x, y, width, height;
    private float startY = -10;
    private boolean canDisappear = false;
    private final ScaledResolution sr = new ScaledResolution(mc);

    public LoneNotification(String title, String description, long delay) {
        super(title, description, delay, NotificationType.LONE);
        this.animFloat = new AnimFloat(0.0f, 0.05f, Easing.EASE_IN_OUT);
        this.animFloat.setTarget(1.0f);

        this.width = medium.getWidth(description) + 20;
        this.height = 32;
        this.x = sr.getScaledWidth() / 2f;
        this.y = startY - height;
    }

    @Override
    public void render(float partialTicks, int index) {
        this.animFloat.update(partialTicks);
        applyPositions();

        medium.drawCenteredString(description, x, y, -1);
    }

    private void applyPositions() {
        this.x = sr.getScaledWidth() / 2f;
        this.y = startY + ((height + 4) * animFloat.getValue());

        if (animFloat.getTarget() >= 0.99f) {
            if (!canDisappear) this.created = System.currentTimeMillis();
            this.canDisappear = true;
            if (hasElapsed()) this.animFloat.setTarget(0.0f);
        }
    }

    @Override
    public boolean shouldDisappear() {
        return hasElapsed() && this.y <= startY;
    }
}
