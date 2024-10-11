package org.teamy.backend.service;

import org.teamy.backend.UoW.EventDeleteUoW;
import org.teamy.backend.UoW.RSVPUoW;
import org.teamy.backend.config.DatabaseConnectionManager;
import org.teamy.backend.model.*;
import org.teamy.backend.model.exception.NotEnoughTicketsException;
import org.teamy.backend.model.exception.OptimisticLockingFailureException;
import org.teamy.backend.repository.*;

import java.math.BigDecimal;
import java.net.ConnectException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class EventService {
    EventRepository eventRepository;
    RSVPRepository rsvpRepository;
    TicketRepository ticketRepository;
    VenueRepository venueRepository;
    ClubRepository clubRepository;
    private final DatabaseConnectionManager databaseConnectionManager;
    private static EventService instance;

    private final ConcurrentHashMap<Integer, Lock> eventLocks = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Integer, Lock> clubLocks = new ConcurrentHashMap<>();

    private Lock getEventLock(int eventId) {
        return eventLocks.computeIfAbsent(eventId, id -> new ReentrantLock(true));
    }
    public static synchronized EventService getInstance(EventRepository eventRepository, RSVPRepository rsvpRepository,TicketRepository ticketRepository,VenueRepository venueRepository,ClubRepository clubRepository,DatabaseConnectionManager databaseConnectionManager) {
        if (instance == null) {
            instance = new EventService(eventRepository,rsvpRepository,ticketRepository, venueRepository, clubRepository, databaseConnectionManager);
        }
        return instance;
    }
    // 添加一个方法来重置单例
    public static synchronized void resetInstance() {
        instance = null;
    }
    private EventService(EventRepository eventRepository, RSVPRepository rsvpRepository, TicketRepository ticketRepository, VenueRepository venueRepository, ClubRepository clubRepository, DatabaseConnectionManager databaseConnectionManager) {
        this.eventRepository = eventRepository;
        this.rsvpRepository = rsvpRepository;
        this.ticketRepository = ticketRepository;
        this.clubRepository=clubRepository;
        this.venueRepository = venueRepository;
        this.databaseConnectionManager = databaseConnectionManager;
    }
    public Event getEventById(Integer id) throws Exception {
        Connection connection =  databaseConnectionManager.nextConnection();
        if (id <= 0) {
            throw new IllegalArgumentException("Club ID must be positive");
        }

        Event event = eventRepository.findEventById(id,connection);
        if (event == null) {
            throw new RuntimeException("event with id '" + id + "' not found");
        }
        return event;
    }
    public List<Event> getEventByTitle(String title) throws Exception {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be null or empty.");
        }
        List<Event>events=  eventRepository.findEventsByTitle(title);
        for(Event event:events){
            Integer currentCapacity = getCurrentCapacity(event);
            event.setCurrentCapacity(currentCapacity);
        }
        return events;
    }

    public boolean saveEvent(Event event) throws Exception {
        // You can add additional business logic here, such as data validation
        if (event ==null||event.getTitle() == null || event.getTitle().isEmpty()) {
            throw new IllegalArgumentException("Club cannot be empty");
        }

        event = eventRepository.lazyLoadClub(event);
        System.out.println("load club");

        Venue venue = venueRepository.getVenueById(event.getVenueId());
        event.setVenue(venue);
        System.out.println("set venue");
        try {
            event.validateBudget();
            event.validateCapacity();
            System.out.println("pass test");
            boolean isSuccess =  eventRepository.saveEvent(event);
            System.out.println("save");
            clubRepository.invalidateClubCache(event.getClubId());
            return isSuccess;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
        }
    }

    public List<Event> getAllEvents() {
        try {
            List<Event>events =  eventRepository.getAllEvent();
            for(Event event:events){
                Integer currentCapacity = getCurrentCapacity(event);
                event.setCurrentCapacity(currentCapacity);
            }
            return events;
        } catch (Exception e) {
            // Exceptions are handled here, such as logging or throwing custom exceptions
            System.err.println("Error occurred while fetching clubs: " + e.getMessage());
            e.printStackTrace();
            return Collections.emptyList(); // Returns an empty list to prevent the upper code from crashing
        }
    }
    public boolean updateEvent(Event event) throws Exception {
        Connection connection = databaseConnectionManager.nextConnection();
            System.out.println("你好"+event);
            // 检查事件是否存在
            Event existingEvent = eventRepository.findEventById(event.getId(),connection);
            if (existingEvent == null) {
                throw new Exception("Event not found with ID: " + event.getId());
            }
            Venue venue = venueRepository.getVenueById(event.getVenueId());
            Club club = clubRepository.findClubById(event.getClubId());
            try {
                if(event.getCost().compareTo(BigDecimal.valueOf(club.getBudget()))>0){
                    throw new RuntimeException("budget not enough");
                }
                if(event.getCapacity()>venue.getCapacity()){
                    throw new RuntimeException("venue capacity not enough");
                }
                // 调用 DataMapper 更新事件
                boolean isSuccess =  eventRepository.updateEvent(event);
                List<Ticket> tickets = ticketRepository.getTicketsFromEvent(event.getId());

                ticketRepository.invalidateTicketCaches(tickets);
                clubRepository.invalidateClubCache(event.getClubId());
                return isSuccess;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }finally {
            }
    }
    public void applyForRSVP(int eventId, int studentId, int numTickets,List<Integer> participates_id) throws Exception {
        Connection connection = databaseConnectionManager.nextConnection();
        try {
            connection.setAutoCommit(false);  // 关闭自动提交，手动管理事务

            // 1. 查找 Event 并更新其容量（使用乐观锁控制）
            Event event = eventRepository.findEventById(eventId,connection);
            event.setCapacity(event.getCapacity() - numTickets);

            // 更新 Event 的容量（乐观锁控制）
            boolean isUpdated = eventRepository.updateCapacity(event, connection);
            if (!isUpdated) {
                throw new OptimisticLockingFailureException("Failed to update event capacity due to version mismatch.");
            }

            // 2. 保存 RSVP
            RSVP rsvp = new RSVP(studentId, eventId, numTickets, participates_id);
            rsvp.setEvent(event);
            rsvpRepository.saveRSVP(connection, rsvp);  // 保存 RSVP，生成自增 ID

            // 3. 为每个参与者保存 Tickets
            for (Integer participantId : participates_id) {
                Ticket ticket = new Ticket(participantId, rsvp.getId(), eventId, TicketStatus.Issued);
                ticketRepository.saveTicket(connection, ticket);  // 保存 Ticket
            }

            // 提交事务
            connection.commit();
        } catch (Exception e) {
            try {
                connection.rollback();  // 事务回滚
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            throw new RuntimeException("Error in RSVP and Ticket processing: " + e.getMessage());
        } finally {
            try {
                connection.setAutoCommit(true);  // 恢复自动提交模式
                databaseConnectionManager.releaseConnection(connection);  // 释放数据库连接
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized void deleteEvent(List<Integer> eventsId)throws Exception{
        EventDeleteUoW eventDeleteUoW = new EventDeleteUoW(eventRepository,ticketRepository,databaseConnectionManager);
        for (Integer eventId : eventsId){
            eventDeleteUoW.addDeleteEvents(eventId);
        }
        System.out.println(eventDeleteUoW.toString());
        eventDeleteUoW.commit();
    }

    public Integer getCurrentCapacity(Event event)throws Exception{
        List<Ticket> tickets = ticketRepository.getTicketsFromEvent(event.getId());
        int count=0;
        for(Ticket ticket:tickets){
            if(ticket.getStatus().equals(TicketStatus.Issued))count++;
        }
        return event.getCapacity()-count;
    }
}
