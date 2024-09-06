package org.teamy.backend.UoW;

import org.teamy.backend.DataMapper.EventDataMapper;
import org.teamy.backend.DataMapper.RSVPDataMapper;
import org.teamy.backend.DataMapper.TicketDataMapper;
import org.teamy.backend.model.RSVP;
import org.teamy.backend.model.Ticket;

import java.util.ArrayList;
import java.util.List;
public class RSVPUoW implements UnitOfWork{
    private final RSVPDataMapper rsvpDataMapper;
    private final TicketDataMapper ticketDataMapper;
    private List<RSVP> newRsvps = new ArrayList<>();
    private List<Ticket> newTickets = new ArrayList<>();

    public RSVPUoW(RSVPDataMapper rsvpDataMapper, TicketDataMapper ticketDataMapper) {
        this.rsvpDataMapper = rsvpDataMapper;
        this.ticketDataMapper = ticketDataMapper;
    }
    public void registerNewRSVP(RSVP rsvp) {
        newRsvps.add(rsvp);
    }

    public void registerNewTicket(Ticket ticket) {
        newTickets.add(ticket);
    }
    @Override
    public void commit() throws Exception {
        try {
            for (RSVP rsvp : newRsvps) {
                rsvpDataMapper.saveRSVP(rsvp);
            }
            for (Ticket ticket : newTickets) {
                ticketDataMapper.saveTicket(ticket);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error committing UoW: " + e.getMessage());
        } finally {
            clear();
        }
    }
    @Override
    public void clear() {
        newRsvps.clear();
        newTickets.clear();
    }
}
