package org.teamy.backend.model;

import javax.swing.border.EmptyBorder;
import java.util.List;

public class RSVP {
    private Student submitter;
    private Event event;
    private RSVPStatus status;
    private List<Ticket> tickets;

    public RSVP(Student submitter, Event event, RSVPStatus status, List<Ticket> tickets) {
        this.submitter = submitter;
        this.event = event;
        this.status = status;
        this.tickets = tickets;
    }

    public Student getSubmitter() {
        return submitter;
    }

    public void setSubmitter(Student submitter) {
        this.submitter = submitter;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public RSVPStatus getStatus() {
        return status;
    }

    public void setStatus(RSVPStatus status) {
        this.status = status;
    }

    public List<Ticket> getTickets() {
        return tickets;
    }

    public void setTickets(List<Ticket> tickets) {
        this.tickets = tickets;
    }
}
