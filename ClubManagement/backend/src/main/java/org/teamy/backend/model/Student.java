package org.teamy.backend.model;

import org.teamy.backend.DataMapper.ClubDataMapper;
import org.teamy.backend.security.model.Role;
import org.teamy.backend.service.ClubService;
import org.teamy.backend.service.StudentService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class Student extends Person{
    private List<Integer>rsvpsId;
    private List<RSVP>rsvps;
    private List<Integer>clubId;
    private List<Integer>ticketsId;

    private List<Ticket>tickets;
    private List<Club>clubs;

    public Student(Long id, String username, String name, String email, Long phoneNumber, String password, boolean isActive, Set<Role> roles, List<RSVP> rsvps, List<Integer> clubId, List<Ticket> tickets) {
        super(id, username, name, email, phoneNumber, password, isActive, roles);
        this.rsvps = rsvps;
        this.clubId = clubId;
        this.tickets = tickets;
    }

    public Student(Long id, String username, String name, String email, Long phoneNumber, String password, boolean isActive) {
        super(id, name,email,  phoneNumber, password, username, isActive);
        this.rsvpsId = new ArrayList<>();
        this.clubId = new ArrayList<>();
        this.ticketsId = new ArrayList<>();
    }

    public Student(String name, String email, Long phoneNumber) {
        super(name, email, phoneNumber);
        this.rsvps = new ArrayList<>();
        this.clubId = new ArrayList<>();
        this.tickets = new ArrayList<>();
    }
    public Student(String name, String email, Long phoneNumber,String password,String username) {
        super(name, email, phoneNumber,password,username);
        this.rsvps = new ArrayList<>();
        this.clubId = new ArrayList<>();
        this.tickets = new ArrayList<>();
    }

    public List<RSVP> getRsvps() {
        return rsvps;
    }

    public void setRsvps(List<RSVP> rsvps) {
        this.rsvps = rsvps;
    }

    public List<Integer> getClubId() {
        return clubId;
    }

    public void setClubId(List<Integer> clubId) {
        this.clubId = clubId;
    }

    public List<Club> getClubs() {
        if (clubs == null) {
            return Collections.emptyList();
        }
        return clubs;
    }

    public void setClubs(List<Club> clubs) {
        this.clubs = clubs;
    }

    public void addClubId(Integer clubId) {
        this.clubId.add(clubId);
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

    public List<Integer> getRsvpsId() {
        return rsvpsId;
    }

    public void setRsvpsId(List<Integer> rsvpsId) {
        this.rsvpsId = rsvpsId;
    }

    public List<Integer> getTicketsId() {
        return ticketsId;
    }

    public void setTicketsId(List<Integer> ticketsId) {
        this.ticketsId = ticketsId;
    }
}
