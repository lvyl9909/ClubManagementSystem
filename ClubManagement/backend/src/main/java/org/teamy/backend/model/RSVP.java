package org.teamy.backend.model;

import javax.swing.border.EmptyBorder;
import java.util.List;

public class RSVP implements CapacityObserver{
    private Student submitter;
    private Event event;
    private RSVPStatus status;
    //private List<Ticket> tickets;
    private Integer number;
    private List<Student> participant;

    public List<Student> getParticipant() {
        return participant;
    }

    public void setParticipant(List<Student> participant) {
        this.participant = participant;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public RSVP(Student submitter, Event event, RSVPStatus status, List<Ticket> tickets) {
        this.submitter = submitter;
        this.event = event;
        this.status = status;
        //this.tickets = tickets;
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

//    public List<Ticket> getTickets() {
//        return tickets;
//    }
//
//    public void setTickets(List<Ticket> tickets) {
//        this.tickets = tickets;
//    }

    @Override
    public void update(Event event) {
        if (status.equals(RSVPStatus.Waitlisted) && event.getCapacity() > 0) {
            //submit(); // Re-submit RSVP if event capacity increases
        }
    }
}
