package org.teamy.backend.model;

public class Ticket {
    private Integer id;
    private Integer studentId;
    private Student student;
    private Integer rsvpId;
    private RSVP rsvp;
    private Event event;
    private Integer EventId;
    private TicketStatus status;

    public Ticket(Integer id,Integer studentId, Integer rsvpId, TicketStatus status,Integer eventId) {
        this.id=id;
        this.studentId = studentId;
        this.rsvpId = rsvpId;
        this.status = status;
        this.EventId = eventId;
    }
    public Ticket(Integer studentId, Integer rsvpId, TicketStatus status) {
        this.studentId = studentId;
        this.rsvpId = rsvpId;
        this.status = status;
    }

    @Override
    public String toString() {
        return "Ticket{" +
                "id=" + id +
                ", studentId=" + studentId +
                ", student=" + student +
                ", rsvpId=" + rsvpId +
                ", rsvp=" + rsvp +
                ", event=" + event +
                ", EventId=" + EventId +
                ", status=" + status +
                '}';
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Integer getId() {
        return id;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public Integer getEventId() {
        return EventId;
    }

    public void setEventId(Integer eventId) {
        EventId = eventId;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getStudentId() {
        return studentId;
    }

    public void setStudentId(Integer studentId) {
        this.studentId = studentId;
    }

    public Integer getRsvpId() {
        return rsvpId;
    }

    public void setRsvpId(Integer rsvpId) {
        this.rsvpId = rsvpId;
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
