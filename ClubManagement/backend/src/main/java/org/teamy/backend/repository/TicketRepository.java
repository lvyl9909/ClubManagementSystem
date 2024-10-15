package org.teamy.backend.repository;

import org.teamy.backend.DataMapper.TicketDataMapper;
import org.teamy.backend.model.Ticket;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class TicketRepository {
    private final TicketDataMapper ticketDataMapper;
    private static TicketRepository instance;
    private TicketRepository(TicketDataMapper ticketDataMapper) {
        this.ticketDataMapper = ticketDataMapper;
    }

    public static synchronized TicketRepository getInstance(TicketDataMapper ticketDataMapper){
        if(instance == null){
            instance = new TicketRepository(ticketDataMapper);
        }
        return instance;
    }
    public Ticket findTicketById(int Id) {
        Ticket ticket = ticketDataMapper.findTicketById(Id);

        return ticket;
    }
    public void saveTicket(Connection connection,Ticket ticket) {
        try {
            ticketDataMapper.saveTicket(connection,ticket);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public void saveTickets(List<Ticket> tickets) {
        Map<Integer, Ticket> cacheUpdates = new HashMap<>();
        for (Ticket ticket : tickets) {
            cacheUpdates.put(ticket.getId(), ticket);
        }
        try {
            ticketDataMapper.saveTickets(tickets);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void deleteTicket(Integer ticketId)throws SQLException {
        ticketDataMapper.deleteTicket(ticketId);
    }
    public void deleteTicket(Connection connection,Integer ticketId)throws SQLException {
        ticketDataMapper.deleteTicket(connection,ticketId);
    }

    public List<Ticket> getTicketsFromEvent(Integer eventId,Connection connection) throws SQLException {
        return ticketDataMapper.getTicketsFromEvent(eventId,connection);
    }

    public List<Ticket> getTicketsFromEvent(Connection connection,Integer eventId) throws SQLException {
        return ticketDataMapper.getTicketsFromEvent(connection,eventId);
    }


}
