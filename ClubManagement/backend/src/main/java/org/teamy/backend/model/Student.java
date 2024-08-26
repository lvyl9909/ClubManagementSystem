package org.teamy.backend.model;

import org.teamy.backend.security.model.Role;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Student extends Person{
    private String studentId;
    private List<RSVP>rsvps;
    private List<ClubMembership>clubMemberships;
    private List<Ticket>tickets;

    public Student(Long id, String username, String name, String email, Long phoneNumber, String password, boolean isActive, Set<Role> roles, String studentId, List<RSVP> rsvps, List<ClubMembership> clubMemberships, List<Ticket> tickets) {
        super(id, username, name, email, phoneNumber, password, isActive, roles);
        this.studentId = studentId;
        this.rsvps = rsvps;
        this.clubMemberships = clubMemberships;
        this.tickets = tickets;
    }

    public Student(String name, String email, Long phoneNumber, String studentId, List<RSVP> rsvps, List<ClubMembership> clubMemberships, List<Ticket> tickets) {
        super(name, email, phoneNumber);
        this.studentId = studentId;
        this.rsvps = rsvps;
        this.clubMemberships = clubMemberships;
        this.tickets = tickets;
    }

    public Student(String name, String email, Long phoneNumber, String studentId) {
        super(name, email, phoneNumber);
        this.studentId = studentId;
        this.rsvps = new ArrayList<>();
        this.clubMemberships = new ArrayList<>();
        this.tickets = new ArrayList<>();
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public List<RSVP> getRsvps() {
        return rsvps;
    }

    public void setRsvps(List<RSVP> rsvps) {
        this.rsvps = rsvps;
    }

    public List<ClubMembership> getClubMemberships() {
        return clubMemberships;
    }

    public void setClubMemberships(List<ClubMembership> clubMemberships) {
        this.clubMemberships = clubMemberships;
    }

    public List<Ticket> getTickets() {
        return tickets;
    }

    public void setTickets(List<Ticket> tickets) {
        this.tickets = tickets;
    }
    public void addTicket(Ticket tickets) {
        this.tickets.add(tickets);
    }

}
