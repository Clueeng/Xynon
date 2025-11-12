package fr.flaily.xynon.notification;

import fr.flaily.xynon.utils.AnimFloat;
import fr.flaily.xynon.utils.Utils;

public abstract class Notification implements Utils {
    public String title;
    public String description;
    public long delay;

    public long created;
    public AnimFloat animFloat;
    public NotificationType type;

    public Notification(String title, String description, long delay, NotificationType type) {
        this.title = title;
        this.description = description;
        this.delay = delay;
        this.type = type;
        this.created = System.currentTimeMillis();
    }

    public abstract void render(float partialTicks, int index);

    public boolean shouldDisappear() { return false; }

    protected boolean hasElapsed() {
        return System.currentTimeMillis() - this.created >= this.delay;
    }
}
