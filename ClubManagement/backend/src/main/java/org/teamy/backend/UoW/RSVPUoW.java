package org.teamy.backend.UoW;

import org.teamy.backend.DataMapper.EventDataMapper;
import org.teamy.backend.DataMapper.RSVPDataMapper;
import org.teamy.backend.DataMapper.TicketDataMapper;
import org.teamy.backend.model.RSVP;
import org.teamy.backend.model.Ticket;
import org.teamy.backend.service.StudentService;

import java.util.ArrayList;
import java.util.List;
public class RSVPUoW implements UnitOfWork{
    private final RSVPDataMapper rsvpDataMapper;
    private final TicketDataMapper ticketDataMapper;
    private List<RSVP> newRsvps = new ArrayList<>();
    private List<Ticket> newTickets = new ArrayList<>();

    public RSVPUoW(RSVPDataMapper rsvpDataMapper, TicketDataMapper ticketDataMapper) {
        this.rsvpDataMapper = rsvpDataMapper;
        this.ticketDataMapper = ticketDataMapper;
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
        try {
            // 先保存 RSVP 并获取自增 ID
            for (RSVP rsvp : newRsvps) {
                rsvpDataMapper.saveRSVP(rsvp);  // 保存 RSVP，生成自增 ID
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
                ticketDataMapper.saveTicket(ticket);  // 保存 Ticket
            }
        } catch (Exception e) {
            throw new RuntimeException("Error committing UoW: " + e.getMessage());
        } finally {
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
