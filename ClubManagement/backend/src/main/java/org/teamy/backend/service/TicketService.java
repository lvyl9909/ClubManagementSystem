package org.teamy.backend.service;

import org.teamy.backend.DataMapper.TicketDataMapper;
import org.teamy.backend.model.RSVP;
import org.teamy.backend.model.Ticket;

public class TicketService {
    private final TicketDataMapper ticketDataMapper;

    public TicketService(TicketDataMapper ticketDataMapper) {
        this.ticketDataMapper = ticketDataMapper;
    }
    public Ticket getTicketById(int id) throws Exception {
        if (id <= 0) {
            throw new IllegalArgumentException("Club ID must be positive");
        }

        Ticket ticket = null;
        try {
            ticket = ticketDataMapper.findTicketById(id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return ticket;
    }
}
