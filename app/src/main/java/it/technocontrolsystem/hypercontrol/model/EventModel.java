package it.technocontrolsystem.hypercontrol.model;

import it.technocontrolsystem.hypercontrol.domain.Event;

/**
 * Modello dati Eventi.
 * Questo modello non ha informazioni di stato
 *
 * Created by alex on 10-07-2015.
 */
public class EventModel implements ModelIF {
    private Event event;

    public EventModel(Event event) {
        this.event = event;
    }

    @Override
    public int getNumber() {
        return 0;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    @Override
    public void clearStatus() { }
}
