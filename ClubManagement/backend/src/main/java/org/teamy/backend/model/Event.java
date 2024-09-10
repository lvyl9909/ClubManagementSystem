package org.teamy.backend.model;

import jakarta.persistence.criteria.CriteriaBuilder;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class Event extends DomainObject {
    private String title;
    private String description;
    private String date;
    private String time;
    private String venueName;
    private Integer venueId;
    private BigDecimal cost;
    private Integer clubId;
    private Club club;
    private Venue venue;
    private Integer capacity;
    private Integer currentCapacity;
    private List<RSVP> rsvps;
    private EventStatus status;
    public Integer getClubId() {
        return clubId;
    }

    public void setClubId(Integer clubId) {
        this.clubId = clubId;
    }

    @Override
    public String toString() {
        return "Event{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", date='" + date + '\'' +
                ", time='" + time + '\'' +
                ", venueName='" + venueName + '\'' +
                ", cost=" + cost +
                ", capacity=" + capacity +
                ", rsvps=" + rsvps +
                ", clubId=" + clubId +
                '}';
    }


    public Event(Integer id,String title, String description, Date date, Time time, Integer venueId, BigDecimal cost, Integer clubId,String status,Integer capacity) {
        this.setId(id);
        this.clubId = clubId;
        this.title = title;
        this.description = description;
        this.date = date.toString();
        this.time = time.toString();
        this.venueId = venueId;
        this.cost = cost;
        this.status = EventStatus.valueOf(status);
        this.capacity = capacity;
        this.currentCapacity = capacity;
    }
    public Event(String title, String description, Date date, Time time, String venueName, BigDecimal cost, Integer clubId,Integer capacity) {
        this.clubId = clubId;
        this.title = title;
        this.description = description;
        this.date = date.toString();
        this.time = time.toString();
        this.venueName = venueName;
        this.cost = cost;
        this.status = EventStatus.Ongoing;
        this.capacity =capacity;
        this.currentCapacity = capacity;
    }

    public Event() {
    }

    public EventStatus getStatus() {
        return status;
    }

    public void setStatus(EventStatus status) {
        this.status = status;
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

    public Date getSqlDate() {
        return Date.valueOf(LocalDate.parse(date));
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Time getSqlTime() {
        return Time.valueOf(LocalTime.parse(time));  // 手动转换为 java.sql.Time
    }

    public String getVenueName() {
        return venueName;
    }

    public void setVenueName(String venueName) {
        this.venueName = venueName;
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

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public List<RSVP> getRsvps() {
        return rsvps;
    }

    public void setRsvps(List<RSVP> rsvps) {
        this.rsvps = rsvps;
    }

    public Integer getClub() {
        return clubId;
    }

    public void setClub(Integer clubId) {
        this.clubId = clubId;
    }

    public Integer getCurrentCapacity() {
        return currentCapacity;
    }

    public void setCurrentCapacity(Integer currentCapacity) {
        this.currentCapacity = currentCapacity;
    }

    public void setClub(Club club) {
        this.club = club;
    }

    public Venue getVenue() {
        return venue;
    }

    public void setVenue(Venue venue) {
        this.venue = venue;
    }

    public void addRSVP(RSVP rsvp){
        this.rsvps.add(rsvp);
    }

    public boolean deleteRSVP(RSVP rsvp){
        if(this.rsvps.contains(rsvp)){
            this.rsvps.remove(rsvp);
            return true;
        }
        else {
            return false;
        }
    }
    // 方法来处理与业务相关的逻辑
    public void validateBudget() {
        if (this.cost.compareTo(BigDecimal.valueOf(club.getBudget())) > 0) {
            throw new RuntimeException("Budget not enough");
        }
    }

    public void validateCapacity() {
        if (this.capacity > venue.getCapacity()) {
            throw new RuntimeException("Venue capacity not enough");
        }
    }

}
