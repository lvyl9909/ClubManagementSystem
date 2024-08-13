package org.teamy.backend.model;

import java.util.List;

public class Club {
    private String name;
    private String description;
    private List<ClubMembership> clubMemberships;
    private List<Event> events;
    private List<fundingApplication> fundingApplications;

    public Club(String name, String description, List<ClubMembership> clubMemberships, List<Event> events, List<fundingApplication> fundingApplications) {
        this.name = name;
        this.description = description;
        this.clubMemberships = clubMemberships;
        this.events = events;
        this.fundingApplications = fundingApplications;
    }

    public Club(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Club() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<ClubMembership> getClubMemberships() {
        return clubMemberships;
    }

    public void setClubMemberships(List<ClubMembership> clubMemberships) {
        this.clubMemberships = clubMemberships;
    }

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }

    public List<fundingApplication> getFundingApplications() {
        return fundingApplications;
    }

    public void setFundingApplications(List<fundingApplication> fundingApplications) {
        this.fundingApplications = fundingApplications;
    }
}
