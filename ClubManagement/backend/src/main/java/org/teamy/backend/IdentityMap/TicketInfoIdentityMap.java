package org.teamy.backend.IdentityMap;

import org.teamy.backend.model.Event;
import org.teamy.backend.model.RSVP;
import org.teamy.backend.model.Ticket;

import java.util.HashMap;
import java.util.Map;

public class TicketInfoIdentityMap {
    private Map<Integer, Ticket> tickets = new HashMap<>();
    private Map<Integer, Event> events = new HashMap<>();

    public Ticket getTicket(int ticketId) {
        return tickets.get(ticketId);
    }

    public void addTicket(Ticket ticket) {
        tickets.put(ticket.getId(), ticket);
    }

    public Event getEvent(int eventId) {
        return events.get(eventId);
    }

    public void addEvent(Event event) {
        events.put(event.getId(), event);
    }
}
