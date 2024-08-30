package org.teamy.backend.model;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class Event {
    private String title;
    private String description;
    private String date;
    private String time;
    private String venueName;
    private BigDecimal cost;
    private Integer clubId;
    private Integer capacity;
    private List<RSVP> rsvps;
    private final List<CapacityObserver> observers = new ArrayList<>();
    private Waitlist waitlist;

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

    public void setWaitlist(Waitlist waitlist) {
        this.waitlist = waitlist;
    }


    public Event(String title, String description, Date date, Time time, String venueName, BigDecimal cost, Integer clubId) {
        this.clubId = clubId;
        this.title = title;
        this.description = description;
        this.date = date.toString();
        this.time = time.toString();
        this.venueName = venueName;
        this.cost = cost;
    }

    public Event() {
    }

    public Event(String title, String description, String venueName, BigDecimal cost, Integer clubId) {
        this.title = title;
        this.description = description;
        this.venueName = venueName;
        this.cost = cost;
        this.clubId = clubId;
    }

    public Event(String title, String description, String date, String time, String venueName, BigDecimal cost, Integer clubId, Integer capacity) {
        this.title = title;
        this.description = description;
        this.date = date;
        this.time = time;
        this.venueName = venueName;
        this.cost = cost;
        this.clubId = clubId;
        this.capacity = capacity;
        this.waitlist = new Waitlist(this);
        this.rsvps = new ArrayList<>();
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
        return Date.valueOf(LocalDate.parse(date));  // 在这里手动转换为 java.sql.Date
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

    public void registerObserver(CapacityObserver observer) {
        observers.add(observer);
    }

    public void notifyObservers() {
        for (CapacityObserver observer : observers) {
            observer.update(this);
        }
    }

    public void updateCapacity(int newCapacity) {
        this.capacity = newCapacity;
        notifyObservers(); // Notify all observers when capacity changes
    }
    public Waitlist getWaitlist() {
        return waitlist;
    }
}
