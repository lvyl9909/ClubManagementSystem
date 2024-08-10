package org.teamy.backend.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Event {
    private String title;
    private String description;
    private LocalDateTime dateTime;
    private Venue venue;
    private BigDecimal cost;
    private Integer capacity;
    private List<RSVP> rsvps;
    private Club club;
    private List<CapacityObserver> observers = new ArrayList<>();
    private Waitlist waitlist;

    public Waitlist getWaitlist() {
        return waitlist;
    }

    public void setWaitlist(Waitlist waitlist) {
        this.waitlist = waitlist;
    }

    public Event(String title, String description, LocalDateTime dateTime, Venue venue, BigDecimal cost, Integer capacity, List<RSVP> rsvps, Club club) {
        this.title = title;
        this.description = description;
        this.dateTime = dateTime;
        this.venue = venue;
        this.cost = cost;
        this.capacity = capacity;
        this.rsvps = rsvps;
        this.club = club;
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

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public Venue getVenue() {
        return venue;
    }

    public void setVenue(Venue venue) {
        this.venue = venue;
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

    public Club getClub() {
        return club;
    }

    public void setClub(Club club) {
        this.club = club;
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
}
