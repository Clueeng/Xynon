package fr.flaily.xynon.notification;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import best.azura.eventbus.handler.EventHandler;
import fr.flaily.xynon.Xynon;
import fr.flaily.xynon.events.render.OverScreenEvent;

public class NotificationManager {

    private final Queue<Notification> loneQueue = new LinkedList<>();
    private Notification currentLone = null;

    private final ArrayList<Notification> stackableList = new ArrayList<>();

    public NotificationManager() {
        Xynon.INSTANCE.getEventBus().subscribe(this);
    }

    @EventHandler
    public void render(OverScreenEvent event) {
        renderLone(event.partialTicks);
        renderStackable(event.partialTicks);
    }

    private void renderLone(float partialTicks) {
        if (currentLone == null && !loneQueue.isEmpty()) {
            currentLone = loneQueue.remove();
            currentLone.created = System.currentTimeMillis();
        }

        if (currentLone != null) {
            currentLone.render(partialTicks, 0);
            if (currentLone.shouldDisappear()) {
                currentLone = null;
            }
        }
    }

    private void renderStackable(float partialTicks) {
        Iterator<Notification> it = stackableList.iterator();
        int index = 0;
        while (it.hasNext()) {
            Notification n = it.next();
            n.render(partialTicks, index++);
            if (n.shouldDisappear()) {
                it.remove();
            }
        }
    }

    public void add(Notification n) {
        if (n.type == NotificationType.LONE)
            loneQueue.add(n);
        else if (n.type == NotificationType.STACKABLE)
            stackableList.add(n);
    }
}
