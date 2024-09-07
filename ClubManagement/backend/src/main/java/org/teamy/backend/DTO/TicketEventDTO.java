package org.teamy.backend.DTO;

import org.teamy.backend.model.Event;
import org.teamy.backend.model.Ticket;

import java.math.BigDecimal;

public class TicketEventDTO {
    private Integer ticketId;
    private String ticketStatus;
    private String title;
    private Integer eventId;
    private String description;
    private String date;
    private String time;
    private String venueName;
    private BigDecimal cost;
    private Integer clubId;

    // 构造函数
    public TicketEventDTO(Ticket ticket, Event event) {
        this.ticketId = ticket.getId();
        this.ticketStatus = ticket.getStatus().name();  // 假设 ticket status 是枚举类型

        this.eventId =event.getId();
        this.title = event.getTitle();
        this.description = event.getDescription();
        this.date = event.getSqlDate().toString();
        this.time = event.getSqlTime().toString();
        this.venueName = event.getVenueName();
        this.cost = event.getCost();
        this.clubId = event.getClub();
    }

    public Integer getTicketId() {
        return ticketId;
    }

    public void setTicketId(Integer ticketId) {
        this.ticketId = ticketId;
    }

    public String getTicketStatus() {
        return ticketStatus;
    }

    public void setTicketStatus(String ticketStatus) {
        this.ticketStatus = ticketStatus;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getVenueName() {
        return venueName;
    }

    public void setVenueName(String venueName) {
        this.venueName = venueName;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    public Integer getClubId() {
        return clubId;
    }

    public void setClubId(Integer clubId) {
        this.clubId = clubId;
    }
}
