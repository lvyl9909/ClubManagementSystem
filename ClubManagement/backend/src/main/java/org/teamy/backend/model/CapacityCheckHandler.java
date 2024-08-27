package org.teamy.backend.model;

import org.teamy.backend.service.EventService;

public class CapacityCheckHandler implements RSVPHandler {
    private RSVPHandler nextHandler;
    @Override
    public void setNextHandler(RSVPHandler handler) {
        this.nextHandler = handler;
    }

    @Override
    public void handle(RSVP rsvp) {
        if (rsvp.getEvent().getCapacity() > 0) {
            rsvp.getEvent().updateCapacity(rsvp.getEvent().getCapacity()-rsvp.getNumber());
            if (nextHandler != null) {
                nextHandler.handle(rsvp); // Pass to next handler
            }
        } else {
            rsvp.setStatus(RSVPStatus.Waitlisted);
            rsvp.getEvent().getWaitlist().addToWaitlist(rsvp); // Add to waitlist if no capacity
        }
    }
}