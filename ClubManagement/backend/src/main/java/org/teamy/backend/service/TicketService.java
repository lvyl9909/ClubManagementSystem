package org.teamy.backend.service;

import org.teamy.backend.DataMapper.EventDataMapper;
import org.teamy.backend.DataMapper.RSVPDataMapper;
import org.teamy.backend.DataMapper.TicketDataMapper;
import org.teamy.backend.IdentityMap.TicketInfoIdentityMap;
import org.teamy.backend.model.Event;
import org.teamy.backend.model.RSVP;
import org.teamy.backend.model.Ticket;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TicketService {
    private final TicketDataMapper ticketDataMapper;
    private final RSVPDataMapper rsvpDataMapper;
    private final EventDataMapper eventDataMapper;

    public TicketService(TicketDataMapper ticketDataMapper, RSVPDataMapper rsvpDataMapper, EventDataMapper eventDataMapper) {
        this.ticketDataMapper = ticketDataMapper;
        this.rsvpDataMapper = rsvpDataMapper;
        this.eventDataMapper = eventDataMapper;
    }
    public Ticket getTicketById(int id) throws Exception {
        if (id <= 0) {
            throw new IllegalArgumentException("Club ID must be positive");
        }

        Ticket ticket = null;
        try {
            ticket = ticketDataMapper.findTicketById(id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return ticket;
    }
    public Map<Ticket,Event> getTicketInfo(Long studentId) throws Exception {
        // 为当前请求创建一个 IdentityMapManager 实例
        TicketInfoIdentityMap identityMapManager = new TicketInfoIdentityMap();
        Map<Ticket,Event> result = new HashMap<>();
        // 查询 Ticket
        List<Ticket>tickets = ticketDataMapper.getTicketsFromStudent(Math.toIntExact(studentId));
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
}
