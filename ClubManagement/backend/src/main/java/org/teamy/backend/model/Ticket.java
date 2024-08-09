package org.teamy.backend.model;

public class Ticket {
    private Student student;
    private RSVP rsvp;
    private TicketStatus status;

    public Ticket(Student student, RSVP rsvp, TicketStatus status) {
        this.student = student;
        this.rsvp = rsvp;
        this.status = status;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public RSVP getRsvp() {
        return rsvp;
    }

    public void setRsvp(RSVP rsvp) {
        this.rsvp = rsvp;
    }

    public TicketStatus getStatus() {
        return status;
    }

    public void setStatus(TicketStatus status) {
        this.status = status;
    }
}
