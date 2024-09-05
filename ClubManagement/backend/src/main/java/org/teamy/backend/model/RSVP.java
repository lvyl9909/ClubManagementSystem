package org.teamy.backend.model;

import jakarta.persistence.criteria.CriteriaBuilder;
import org.teamy.backend.service.EventService;
import org.teamy.backend.service.StudentService;

import javax.swing.border.EmptyBorder;
import java.util.ArrayList;
import java.util.List;

public class RSVP implements CapacityObserver{
    private Integer id;
    private Integer submitterId;
    private Integer eventId;
    private Event event;
    private Student submitter;
    private RSVPStatus status;
    //private List<Ticket> tickets;
    private Integer number;
    private List<Integer> participantIds;

    private List<Student> participants ;

    public RSVP(Integer id, Integer submitterId, Integer eventId, RSVPStatus status, Integer number, List<Integer>participantIds) {
        this.id=id;
        this.submitterId = submitterId;
        this.eventId = eventId;
        this.status = status;
        this.number = number;
        this.participantIds = participantIds;

    }



    public void setEvent(Event event) {
        this.event = event;
    }


    public void setSubmitter(Student submitter) {
        this.submitter = submitter;
    }

    public Integer getSubmitterId() {
        return submitterId;
    }

    public void setSubmitterId(Integer submitterId) {
        this.submitterId = submitterId;
    }

    public Integer getEventId() {
        return eventId;
    }

    public void setEventId(Integer eventId) {
        this.eventId = eventId;
    }

    public List<Integer> getParticipantIds() {
        return participantIds;
    }

    public void setParticipantIds(List<Integer> participantIds) {
        this.participantIds = participantIds;
    }



    public void setParticipants(List<Student> participants) {
        this.participants = participants;
    }

    public RSVPStatus getStatus() {
        return status;
    }

    public void setStatus(RSVPStatus status) {
        this.status = status;
    }

    public void submit() {
        CapacityCheckHandler capacityHandler = new CapacityCheckHandler();
        TicketGenerationHandler ticketHandler = new TicketGenerationHandler();
        capacityHandler.setNextHandler(ticketHandler);
        capacityHandler.handle(this); // Start chain of responsibility
    }
    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }
    @Override
    public void update(Event event) {
        if (status.equals(RSVPStatus.Waitlist) && event.getCapacity() > 0) {
            submit(); // Re-submit RSVP if event capacity increases
        }
    }
}
