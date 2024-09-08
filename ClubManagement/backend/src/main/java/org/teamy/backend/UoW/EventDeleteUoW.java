package org.teamy.backend.UoW;

import org.teamy.backend.DataMapper.EventDataMapper;
import org.teamy.backend.DataMapper.TicketDataMapper;
import org.teamy.backend.model.Event;
import org.teamy.backend.model.RSVP;
import org.teamy.backend.model.Ticket;

import java.util.ArrayList;
import java.util.List;

public class EventDeleteUoW implements UnitOfWork{
    private final EventDataMapper eventDataMapper;
    private final TicketDataMapper ticketDataMapper;
    private List<Integer> eventsId = new ArrayList<>();

    @Override
    public String toString() {
        return "EventDeleteUoW{" +
                "eventsId=" + eventsId +
                '}';
    }

    public EventDeleteUoW(EventDataMapper eventDataMapper, TicketDataMapper ticketDataMapper) {
        this.eventDataMapper = eventDataMapper;
        this.ticketDataMapper = ticketDataMapper;
    }

    public void addDeleteEvents(Integer eventId) {
        eventsId.add(eventId);
    }

    @Override
    public void commit() throws Exception {
        try {
            System.out.println(eventsId);
            for (Integer eventId:eventsId){
                eventDataMapper.deleteEvent(eventId);
                System.out.println("Event Deleted");
                List<Ticket> tickets = ticketDataMapper.getTicketsFromEvent(eventId);
                for (Ticket ticket : tickets) {
                    ticketDataMapper.deleteTicket(ticket.getId());
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error committing UoW: " + e.getMessage());
        } finally {
            clear();
        }
    }

    @Override
    public void clear() {
        eventsId.clear();
    }
}
