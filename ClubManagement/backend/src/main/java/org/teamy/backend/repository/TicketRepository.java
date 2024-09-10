package org.teamy.backend.repository;

import org.teamy.backend.DataMapper.TicketDataMapper;
import org.teamy.backend.model.Ticket;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class TicketRepository {
    private final TicketDataMapper ticketDataMapper;
    private static TicketRepository instance;
    private final Cache<Integer, Ticket> ticketCache;

    private TicketRepository(TicketDataMapper ticketDataMapper) {
        this.ticketDataMapper = ticketDataMapper;
        this.ticketCache = CacheBuilder.newBuilder()
                .maximumSize(100)  // Maximum 100 tickets in cache
                .expireAfterWrite(30, TimeUnit.MINUTES)  // Tickets expire 10 minutes after being cached
                .build();
    }

    public static synchronized TicketRepository getInstance(TicketDataMapper ticketDataMapper){
        if(instance == null){
            instance = new TicketRepository(ticketDataMapper);
        }
        return instance;
    }
    public Ticket findTicketById(int Id) {
        Ticket ticket = ticketCache.getIfPresent(Id);
        if (ticket == null) {
            // If not in cache, query from database and put in cache
            ticket = ticketDataMapper.findTicketById(Id);
            if (ticket != null) {
                ticketCache.put(Id, ticket); // Add ticket to cache
            }
        }
        return ticket;
    }
    public void saveTicket(Ticket ticket) {
        ticketDataMapper.saveTicket(ticket);
        ticketCache.put(ticket.getId(), ticket); // Update cache
    }
    public void deleteTicket(Integer ticketId)throws SQLException {
        ticketDataMapper.deleteTicket(ticketId);
        ticketCache.invalidate(ticketId); // Remove from cache
    }
    public List<Ticket> getTicketsFromEvent(Integer eventId) throws SQLException {
        return ticketDataMapper.getTicketsFromEvent(eventId);
    }
}
