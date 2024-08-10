package org.teamy.backend.model;

public class TicketGenerationHandler implements RSVPHandler{
    @Override
    public void setNextHandler(RSVPHandler handler) {
        // Last in chain, no next handler
    }

    @Override
    public void handle(RSVP rsvp) {
        for (int i = 0; i < rsvp.getNumber(); i++) {
            Ticket ticket = new Ticket(rsvp.getParticipant().get(i),rsvp,TicketStatus.Issued);
            rsvp.getParticipant().get(i).addTicket(ticket);
            rsvp.setStatus(RSVPStatus.Confirmed);
        }
    }
}
