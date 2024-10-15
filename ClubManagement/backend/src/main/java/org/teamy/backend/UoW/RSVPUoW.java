package org.teamy.backend.UoW;

import org.teamy.backend.DataMapper.EventDataMapper;
import org.teamy.backend.DataMapper.RSVPDataMapper;
import org.teamy.backend.DataMapper.TicketDataMapper;
import org.teamy.backend.config.DatabaseConnectionManager;
import org.teamy.backend.model.RSVP;
import org.teamy.backend.model.Ticket;
import org.teamy.backend.repository.EventRepository;
import org.teamy.backend.repository.RSVPRepository;
import org.teamy.backend.repository.TicketRepository;
import org.teamy.backend.service.StudentService;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
public class RSVPUoW implements UnitOfWork{
    private final RSVPRepository rsvpRepository;
    private final TicketRepository ticketRepository;
    private final DatabaseConnectionManager connectionManager;

    private List<RSVP> newRsvps = new ArrayList<>();
    private List<Ticket> newTickets = new ArrayList<>();

    public RSVPUoW(RSVPRepository rsvpRepository, TicketRepository ticketRepository, DatabaseConnectionManager connectionManager) {
        this.rsvpRepository = rsvpRepository;
        this.ticketRepository = ticketRepository;
        this.connectionManager = connectionManager;
    }
    public void registerNewRSVP(RSVP rsvp) {
        newRsvps.add(rsvp);
        System.out.println("Add rsvp:"+rsvp.toString());
    }

    public void registerNewTicket(Ticket ticket) {
        newTickets.add(ticket);
        System.out.println("Add ticket:"+ticket.toString());
    }
    @Override
    public void commit() throws Exception {
        Connection connection = null;

        try {
            connection = connectionManager.nextConnection();
            connection.setAutoCommit(false); // 关闭自动提交，手动管理事务

            // 先保存 RSVP 并获取自增 ID
            for (RSVP rsvp : newRsvps) {
                rsvpRepository.saveRSVP(connection,rsvp);  // 保存 RSVP，生成自增 ID
                System.out.println("rsvpID:"+rsvp.getId());
            }

            // 使用生成的 RSVP ID 保存 Tickets
            for (Ticket ticket : newTickets) {
                if (ticket.getRsvpId() == null) {
                    // 使用保存后 rsvp 的 ID 更新 ticket 中的 rsvpId
                    RSVP relatedRSVP = findRelatedRSVP(ticket);
                    if (relatedRSVP != null) {
                        ticket.setRsvpId(relatedRSVP.getId());  // 更新 Ticket 的 RSVP ID
                    } else {
                        throw new RuntimeException("No related RSVP found for ticket.");
                    }
                }
                System.out.println(ticket.toString());
                ticketRepository.saveTicket(connection,ticket);  // 保存 Ticket
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
            throw new RuntimeException("Error committing UoW: " + e.getMessage());
        } finally {
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

    private RSVP findRelatedRSVP(Ticket ticket) {
        for (RSVP rsvp : newRsvps) {
            System.out.println(rsvp.getEventId()+"and"+ticket.getEventId());
            if (rsvp.getEventId() == ticket.getEventId()) {
                return rsvp;
            }
        }
        return null;
    }

    @Override
    public void clear() {
        newRsvps.clear();
        newTickets.clear();
    }
}
