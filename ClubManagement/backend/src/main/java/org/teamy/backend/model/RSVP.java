package org.teamy.backend.model;

import org.teamy.backend.service.EventService;
import org.teamy.backend.service.StudentService;

import javax.swing.border.EmptyBorder;
import java.util.ArrayList;
import java.util.List;

public class RSVP implements CapacityObserver{
    private Integer submitterId;
    private Integer eventId;
    private Event event;
    private Student submitter;
    private RSVPStatus status;
    //private List<Ticket> tickets;
    private Integer number;
    private List<Integer> participantIds;

    private List<Student> participants ;

    private final EventService eventService;
    private final StudentService studentService;
    public RSVP(Integer submitterId, Integer eventId, RSVPStatus status, Integer number, EventService eventService, StudentService studentService) {
        this.submitterId = submitterId;
        this.eventId = eventId;
        this.status = status;
        this.number = number;
        this.participantIds = new ArrayList<>(number);

        this.eventService = eventService;
        this.studentService = studentService;
    }

    public Event getEvent() {
        if (event == null) {
            try {
                event = eventService.getEventById(eventId);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public Student getSubmitter() {
        if (submitter == null) {
            try {
                submitter = studentService.getStudentById(submitterId);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return submitter;
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

    public List<Student> getParticipants() {
        if ( participants.isEmpty()) {
            for (int i = 0; i < number; i++) {
                try {
                    Student participant = studentService.getStudentById(participantIds.get(i));
                    participants.add(participant);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return participants;
    }

    public void setParticipants(List<Student> participants) {
        this.participants = participants;
    }

    public EventService getEventService() {
        return eventService;
    }

    public StudentService getStudentService() {
        return studentService;
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
        if (status.equals(RSVPStatus.Waitlisted) && event.getCapacity() > 0) {
            submit(); // Re-submit RSVP if event capacity increases
        }
    }
}
