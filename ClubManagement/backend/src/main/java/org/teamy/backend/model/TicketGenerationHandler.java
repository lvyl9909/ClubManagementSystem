package org.teamy.backend.model;

import org.teamy.backend.service.EventService;

public class TicketGenerationHandler implements RSVPHandler{
    @Override
    public void setNextHandler(RSVPHandler handler) {
        // Last in chain, no next handler
    }

    @Override
    public void handle(RSVP rsvp) {
        for (int i = 0; i < rsvp.getNumber(); i++) {
            Ticket ticket = new Ticket(rsvp.getParticipants().get(i),rsvp,TicketStatus.Issued);
            rsvp.getParticipants().get(i).addTicket(ticket);
            rsvp.setStatus(RSVPStatus.Confirmed);
        }
    }
}
