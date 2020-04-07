package no.sonkin.hardcoreParkour.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public final class RaceEndedEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private String sessionName;

    public RaceEndedEvent(String sessionName) {
        this.sessionName = sessionName;
    }

    public String getSessionName() {
        return sessionName;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
