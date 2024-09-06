package org.teamy.backend.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.teamy.backend.DataMapper.StudentDataMapper;
import org.teamy.backend.model.Club;
import org.teamy.backend.model.RSVP;
import org.teamy.backend.model.Student;
import org.teamy.backend.model.Ticket;

import java.util.ArrayList;
import java.util.List;

public class StudentService {
    private StudentDataMapper studentDataMapper;
    private final ClubService clubService;
    private final RSVPService rsvpService;
    private final TicketService ticketService;
    public StudentService(StudentDataMapper studentDataMapper, ClubService clubService, RSVPService rsvpService, TicketService ticketService) {
        this.studentDataMapper = studentDataMapper;
        this.clubService = clubService;
        this.rsvpService = rsvpService;
        this.ticketService = ticketService;
    }
    public UserDetails getCurrentUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            return (UserDetails) authentication.getPrincipal();
        }
        return null;
    }
    public Student getCurrentStudent() {
        UserDetails userDetails = getCurrentUserDetails();
        if (userDetails instanceof Student) {
            return (Student) userDetails;
        }
        throw new IllegalStateException("Authenticated user is not a Student");
    }
    public Student getStudentById(int id) throws Exception {
        if (id <= 0) {
            throw new IllegalArgumentException("Club ID must be positive");
        }

        Student student = studentDataMapper.findStudentById(id);
        return student;
    }

    public List<Club> getLazyLoadedClubs(Student student) {
        if (student.getClubs() == null || student.getClubs().isEmpty()) {
            List<Club> clubs = new ArrayList<>();
            for (Integer clubId : student.getClubId()) {
                try {
                    Club club = clubService.getClubById(clubId);
                    clubs.add(club);
                } catch (Exception e) {
                    throw new RuntimeException("Error loading clubs for student", e);
                }
            }
            student.setClubs(clubs);
        }
        return student.getClubs();
    }

    public List<Ticket> getLazyLoadedTickets(Student student) {
        if (student.getTickets() == null || student.getTickets().isEmpty()) {
            List<Ticket> tickets = new ArrayList<>();
            for (Integer ticketsId : student.getTicketsId()) {
                try {
                    Ticket ticket = ticketService.getTicketById(ticketsId);
                    tickets.add(ticket);
                } catch (Exception e) {
                    throw new RuntimeException("Error loading clubs for student", e);
                }
            }
            student.setTickets(tickets);
        }
        return student.getTickets();
    }

    public List<RSVP> getLazyLoadedRSVP(Student student) {
        if (student.getRsvps() == null || student.getRsvps().isEmpty()) {
            List<RSVP> rsvps = new ArrayList<>();
            for (Integer rsvpId : student.getRsvpsId()) {
                try {
                    RSVP rsvp = rsvpService.getRSVPById(rsvpId);
                    rsvps.add(rsvp);
                } catch (Exception e) {
                    throw new RuntimeException("Error loading clubs for student", e);
                }
            }
            student.setRsvps(rsvps);
        }
        return student.getRsvps();
    }

}
