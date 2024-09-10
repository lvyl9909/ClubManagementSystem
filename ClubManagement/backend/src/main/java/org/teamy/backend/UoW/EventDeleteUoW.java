package org.teamy.backend.UoW;

import org.teamy.backend.DataMapper.EventDataMapper;
import org.teamy.backend.DataMapper.TicketDataMapper;
import org.teamy.backend.model.Event;
import org.teamy.backend.model.RSVP;
import org.teamy.backend.model.Ticket;
import org.teamy.backend.repository.EventRepository;
import org.teamy.backend.repository.TicketRepository;

import java.util.ArrayList;
import java.util.List;

public class EventDeleteUoW implements UnitOfWork{
    private final EventRepository eventRepository;
    private final TicketRepository ticketRepository;
    private List<Integer> eventsId = new ArrayList<>();

    @Override
    public String toString() {
        return "EventDeleteUoW{" +
                "eventsId=" + eventsId +
                '}';
    }

    public EventDeleteUoW(EventRepository eventRepository, TicketRepository ticketRepository) {
        this.eventRepository = eventRepository;
        this.ticketRepository = ticketRepository;
    }

    public void addDeleteEvents(Integer eventId) {
        eventsId.add(eventId);
    }

    @Override
    public void commit() throws Exception {
        try {
            System.out.println(eventsId);
            for (Integer eventId:eventsId){
                eventRepository.deleteEvent(eventId);
                System.out.println("Event Deleted");
                List<Ticket> tickets = ticketRepository.getTicketsFromEvent(eventId);
                for (Ticket ticket : tickets) {
                    ticketRepository.deleteTicket(ticket.getId());
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
