package org.teamy.backend.UoW;

import org.teamy.backend.DataMapper.EventDataMapper;
import org.teamy.backend.DataMapper.TicketDataMapper;
import org.teamy.backend.config.DatabaseConnectionManager;
import org.teamy.backend.model.Event;
import org.teamy.backend.model.RSVP;
import org.teamy.backend.model.Ticket;
import org.teamy.backend.repository.EventRepository;
import org.teamy.backend.repository.TicketRepository;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EventDeleteUoW implements UnitOfWork{
    private final EventRepository eventRepository;
    private final TicketRepository ticketRepository;
    private final DatabaseConnectionManager connectionManager;

    private List<Integer> eventsId = new ArrayList<>();

    @Override
    public String toString() {
        return "EventDeleteUoW{" +
                "eventsId=" + eventsId +
                '}';
    }

    public EventDeleteUoW(EventRepository eventRepository, TicketRepository ticketRepository, DatabaseConnectionManager connectionManager) {
        this.eventRepository = eventRepository;
        this.ticketRepository = ticketRepository;
        this.connectionManager = connectionManager;
    }

    public void addDeleteEvents(Integer eventId) {
        eventsId.add(eventId);
    }

    @Override
    public void commit() {
        Connection connection = null;

        try {
            connection = connectionManager.nextConnection();
            connection.setAutoCommit(false); // 关闭自动提交，手动管理事务

            System.out.println(eventsId);
            for (Integer eventId:eventsId){
                eventRepository.deleteEvent(connection,eventId);
                System.out.println("Event Deleted");
                List<Ticket> tickets = ticketRepository.getTicketsFromEvent(connection,eventId);
                for (Ticket ticket : tickets) {
                    ticketRepository.deleteTicket(connection,ticket.getId());
                }
            }
            connection.commit();
        } catch (Exception e) {
            if (connection != null) {
                try {
                    connection.rollback(); // 如果出错，回滚事务
                } catch (SQLException rollbackEx) {
                    rollbackEx.printStackTrace();
                }
            }
            throw new RuntimeException("Error committing UoW: " + e.getMessage());        }
        finally {
            if (connection != null) {
                try {
                    connection.setAutoCommit(true); // 恢复自动提交模式
                    connectionManager.releaseConnection(connection); // 释放连接
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            clear();
        }
    }

    @Override
    public void clear() {
        eventsId.clear();
    }
}
