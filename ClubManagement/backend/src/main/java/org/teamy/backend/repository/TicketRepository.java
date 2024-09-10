package org.teamy.backend.repository;

import org.teamy.backend.DataMapper.TicketDataMapper;
import org.teamy.backend.model.Ticket;

import java.sql.SQLException;
import java.util.List;

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
        return ticketDataMapper.findTicketById(Id);
    }
    public void saveTicket(Ticket ticket) {
        ticketDataMapper.saveTicket(ticket);
    }
    public void deleteTicket(Integer ticketId)throws SQLException {
        ticketDataMapper.deleteTicket(ticketId);
    }
    public List<Ticket> getTicketsFromEvent(Integer eventId) throws SQLException {
        return ticketDataMapper.getTicketsFromEvent(eventId);
    }
}
