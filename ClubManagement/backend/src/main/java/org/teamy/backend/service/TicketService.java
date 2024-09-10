package org.teamy.backend.service;

import org.teamy.backend.DataMapper.EventDataMapper;
import org.teamy.backend.DataMapper.RSVPDataMapper;
import org.teamy.backend.DataMapper.TicketDataMapper;
import org.teamy.backend.IdentityMap.TicketInfoIdentityMap;
import org.teamy.backend.model.Event;
import org.teamy.backend.model.RSVP;
import org.teamy.backend.model.Student;
import org.teamy.backend.model.Ticket;
import org.teamy.backend.repository.StudentRepository;
import org.teamy.backend.repository.TicketRepository;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TicketService {
    private final TicketRepository ticketRepository;
    private final StudentRepository studentRepository;
    private final EventDataMapper eventDataMapper;
    private static TicketService instance;

    private TicketService(TicketRepository ticketRepository, StudentRepository studentRepository, EventDataMapper eventDataMapper) {
        this.ticketRepository = ticketRepository;
        this.studentRepository = studentRepository;
        this.eventDataMapper = eventDataMapper;
    }
    public static synchronized TicketService getInstance(TicketRepository ticketRepository, StudentRepository studentRepository, EventDataMapper eventDataMapper){
        if(instance == null){
            instance = new TicketService(ticketRepository,studentRepository,eventDataMapper);
        }
        return instance;
    }
    public Ticket getTicketById(int id) throws Exception {
        if (id <= 0) {
            throw new IllegalArgumentException("Club ID must be positive");
        }

        Ticket ticket = null;
        try {
            ticket = ticketRepository.findTicketById(id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return ticket;
    }
    public Map<Ticket,Event> getTicketInfo(Student student) throws Exception {
        // 为当前请求创建一个 IdentityMapManager 实例
        TicketInfoIdentityMap identityMapManager = new TicketInfoIdentityMap();
        Map<Ticket,Event> result = new HashMap<>();
        // 查询 Ticket
        student = studentRepository.lazyLoadTicket(student);
        List<Ticket>tickets = student.getTickets();
        // 查询 Event
        for(Ticket ticket :tickets){
            System.out.println("ticket:"+ticket);

            Event event = identityMapManager.getEvent(ticket.getEventId());
            System.out.println(event);
            if (event == null) {
                event = eventDataMapper.findEventById(ticket.getEventId());
                identityMapManager.addEvent(event);
            }
            result.put(ticket,event);
        }
        // 将 Ticket 和 Event 信息打包返回
        return result;
    }
    public void deleteTicket(Integer id){
        try {
            ticketRepository.deleteTicket(id);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
