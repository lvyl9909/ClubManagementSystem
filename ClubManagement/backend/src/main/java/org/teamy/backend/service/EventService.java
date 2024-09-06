package org.teamy.backend.service;

import org.teamy.backend.DataMapper.EventDataMapper;
import org.teamy.backend.DataMapper.RSVPDataMapper;
import org.teamy.backend.DataMapper.TicketDataMapper;
import org.teamy.backend.UoW.EventDeleteUoW;
import org.teamy.backend.UoW.RSVPUoW;
import org.teamy.backend.model.*;
import org.teamy.backend.model.exception.NotFoundException;

import java.util.Collections;
import java.util.List;

public class EventService {
    EventDataMapper eventDataMapper;
    RSVPDataMapper rsvpDataMapper;
    TicketDataMapper ticketDataMapper;

    public EventService(EventDataMapper eventDataMapper) {
        this.eventDataMapper = eventDataMapper;
    }
    public Event getEventById(Integer id) throws Exception {
        if (id <= 0) {
            throw new IllegalArgumentException("Club ID must be positive");
        }

        Event event = eventDataMapper.findEventById(id);
        if (event == null) {
            throw new RuntimeException("event with id '" + id + "' not found");
        }
        return event;
    }
    public List<Event> getEventByTitle(String title) throws Exception {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be null or empty.");
        }
        return  eventDataMapper.findEventsByTitle(title);
    }

    public boolean saveEvent(Event event) throws Exception {
        // You can add additional business logic here, such as data validation
        if (event ==null||event.getTitle() == null || event.getTitle().isEmpty()) {
            throw new IllegalArgumentException("Club cannot be empty");
        }

        // Recall methods in DAO layer
        return eventDataMapper.saveEvent(event);
    }

    public List<Event> getAllEvents() {
        try {
            return eventDataMapper.getAllEvent();
        } catch (Exception e) {
            // Exceptions are handled here, such as logging or throwing custom exceptions
            System.err.println("Error occurred while fetching clubs: " + e.getMessage());
            e.printStackTrace();
            return Collections.emptyList(); // Returns an empty list to prevent the upper code from crashing
        }
    }
    public boolean updateEvent(Event event) throws Exception {
        try {
            // 检查事件是否存在
            Event existingEvent = eventDataMapper.findEventById(event.getId());
            if (existingEvent == null) {
                throw new Exception("Event not found with ID: " + event.getId());
            }
            // 调用 DataMapper 更新事件
            return eventDataMapper.updateEvent(event);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Error updating event: " + e.getMessage());
        }
    }
    public void applyForRSVP(int eventId, int studentId, int numTickets,List<Integer> participates_id) throws Exception {
        RSVPUoW unitOfWork = new RSVPUoW(rsvpDataMapper, ticketDataMapper);

        // 创建 RSVP 记录
        RSVP rsvp = new RSVP( studentId,eventId,numTickets,participates_id);
        unitOfWork.registerNewRSVP(rsvp);

        // 创建多个 Ticket 记录
        for (int i = 0; i < numTickets; i++) {
            Ticket ticket = new Ticket(participates_id.get(i),rsvp.getId(), TicketStatus.Issued);
            unitOfWork.registerNewTicket(ticket);
        }

        // 提交事务
        unitOfWork.commit();
    }
    public void deleteEvent(List<Integer> eventsId)throws Exception{
        EventDeleteUoW eventDeleteUoW = new EventDeleteUoW(eventDataMapper,ticketDataMapper);
        for (Integer eventId : eventsId){
            eventDeleteUoW.addDeleteEvents(eventId);
        }
        eventDeleteUoW.commit();
    }
}
