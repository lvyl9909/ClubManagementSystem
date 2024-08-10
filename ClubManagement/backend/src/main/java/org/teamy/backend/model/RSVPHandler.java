package org.teamy.backend.model;

public interface RSVPHandler {
    void setNextHandler(RSVPHandler handler);
    void handle(RSVP rsvp);
}
