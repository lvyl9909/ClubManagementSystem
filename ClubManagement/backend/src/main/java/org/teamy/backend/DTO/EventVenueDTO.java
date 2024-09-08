package org.teamy.backend.DTO;

import org.teamy.backend.model.Event;
import org.teamy.backend.model.Ticket;
import org.teamy.backend.model.Venue;

import java.math.BigDecimal;

public class EventVenueDTO {
    private String venueName;
    private String venueLocation;
    private Integer eventId;
    private String title;
    private String description;
    private String date;
    private String time;
    private Integer venueId;
    private BigDecimal cost;
    private Integer clubId;

    public EventVenueDTO(Venue venue, Event event) {
        this.venueName = venue.getName();  // 假设 ticket status 是枚举类型
        this.venueLocation =venue.getLocation();

        this.eventId =event.getId();
        this.title = event.getTitle();
        this.description = event.getDescription();
        this.date = event.getSqlDate().toString();
        this.time = event.getSqlTime().toString();
        this.venueId = event.getVenueId();
        this.cost = event.getCost();
        this.clubId = event.getClub();
    }

    public String getVenueName() {
        return venueName;
    }

    public void setVenueName(String venueName) {
        this.venueName = venueName;
    }

    public String getVenueLocation() {
        return venueLocation;
    }

    public void setVenueLocation(String venueLocation) {
        this.venueLocation = venueLocation;
    }

    public Integer getEventId() {
        return eventId;
    }

    public void setEventId(Integer eventId) {
        this.eventId = eventId;
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

    public Integer getVenueId() {
        return venueId;
    }

    public void setVenueId(Integer venueId) {
        this.venueId = venueId;
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
