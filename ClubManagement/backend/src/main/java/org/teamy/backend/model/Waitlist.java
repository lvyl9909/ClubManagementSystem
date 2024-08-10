package org.teamy.backend.model;

import java.util.LinkedList;
import java.util.Queue;

public class Waitlist implements CapacityObserver {

    private Queue<RSVP> waitlist = new LinkedList<>();
    private Event event;

    public Waitlist(Event event) {
        this.event = event;
        this.event.registerObserver(this); // Registering as observer of Event
    }

    // Add RSVP to waitlist
    public void addToWaitlist(RSVP rsvp) {
        waitlist.offer(rsvp);
        rsvp.setStatus(RSVPStatus.Waitlisted);
    }

    // Remove RSVP from waitlist
    public void removeFromWaitlist(RSVP rsvp) {
        waitlist.remove(rsvp);
    }

    // Process the waitlist when Event's capacity is updated
    public void processWaitlist() {
        while (event.getCapacity() > 0 && !waitlist.isEmpty()) {
            RSVP rsvp = waitlist.poll();
            rsvp.submit(); // Re-submit the RSVP to handle capacity and ticket allocation
        }
    }

    @Override
    public void update(Event event) {
        if (event.getCapacity() > 0) {
            processWaitlist(); // Process waitlist when event capacity increases
        }
    }
}