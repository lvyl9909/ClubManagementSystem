package org.teamy.backend.model;

import jakarta.persistence.criteria.CriteriaBuilder;
import org.teamy.backend.service.EventService;
import org.teamy.backend.service.StudentService;

import javax.swing.border.EmptyBorder;
import java.util.ArrayList;
import java.util.List;

public class RSVP {
    private Integer id;
    private Integer submitterId;
    private Integer eventId;
    private Event event;
    private Student submitter;
    //private List<Ticket> tickets;
    private Integer number;
    private List<Integer> participantIds;

    private List<Student> participants ;

    public RSVP(Integer id, Integer submitterId, Integer eventId, Integer number, List<Integer>participantIds) {
        this.id=id;
        this.submitterId = submitterId;
        this.eventId = eventId;
        this.number = number;
        this.participantIds = participantIds;

    }
    public RSVP( Integer submitterId, Integer eventId, Integer number, List<Integer>participantIds) {
        this.submitterId = submitterId;
        this.eventId = eventId;
        this.number = number;
        this.participantIds = participantIds;
    }
    public RSVP( Integer submitterId, Integer eventId, Integer number) {
        this.submitterId = submitterId;
        this.eventId = eventId;
        this.number = number;
        this.participantIds = new ArrayList<>(number);
    }


    @Override
    public String toString() {
        return "RSVP{" +
                "id=" + id +
                ", submitterId=" + submitterId +
                ", eventId=" + eventId +
                ", event=" + event +
                ", submitter=" + submitter +
                ", number=" + number +
                ", participantIds=" + participantIds +
                ", participants=" + participants +
                '}';
    }

    public void setEvent(Event event) {
        this.event = event;
    }


    public void setSubmitter(Student submitter) {
        this.submitter = submitter;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Event getEvent() {
        return event;
    }

    public Student getSubmitter() {
        return submitter;
    }

    public List<Student> getParticipants() {
        return participants;
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

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }
}
