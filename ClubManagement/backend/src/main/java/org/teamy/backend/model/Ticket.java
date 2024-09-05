package org.teamy.backend.model;

public class Ticket {
    private Integer id;
    private Integer studentId;
    private Student student;
    private Integer rsvpId;
    private RSVP rsvp;
    private TicketStatus status;

    public Ticket(Integer id,Integer studentId, Integer rsvpId, TicketStatus status) {
        this.id=id;
        this.studentId = studentId;
        this.rsvpId = rsvpId;
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
