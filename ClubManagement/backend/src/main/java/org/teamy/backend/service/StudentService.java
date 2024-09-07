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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public List<Student> getAllStudent(){
        try {
            return studentDataMapper.getAllStudent();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public List<Student> searchStudent(String parameters) {
        Map<Long, Student> studentsMap = new HashMap<>();  // 使用学生 ID 去重
        List<Student> studentsByName;
        List<Student> studentsByEmail;

        try {
            // 模糊搜索按名字
            studentsByName = studentDataMapper.findStudentByName(parameters);
            for (Student student : studentsByName) {
                studentsMap.put(student.getId(), student);  // 以学生 ID 为键，去重
            }

            // 模糊搜索按邮箱
            studentsByEmail = studentDataMapper.findStudentByEmail(parameters);
            for (Student student : studentsByEmail) {
                studentsMap.put(student.getId(), student);  // 再次按学生 ID 存入 Map，若有重复会自动覆盖
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // 返回去重后的学生列表
        return new ArrayList<>(studentsMap.values());
    }

}
