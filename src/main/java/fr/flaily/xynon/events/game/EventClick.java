package fr.flaily.xynon.events.game;

import best.azura.eventbus.core.Event;

public class EventClick implements Event {
    public enum ClickType {
        LEFT, RIGHT;
    }
    public ClickType clickType;
    public EventClick(ClickType clickType) {
        this.clickType = clickType;
    }

    public int getResetDelay() {
        return resetDelay;
    }

    public void setResetDelay(int resetDelay) {
        this.resetDelay = resetDelay;
    }

    private int resetDelay = 4;
}
