package org.teamy.backend.repository;

import org.teamy.backend.DataMapper.*;
import org.teamy.backend.model.*;

import java.sql.SQLException;
import java.util.List;

public class StudentRepository {
    private final ClubDataMapper clubDataMapper;
    private final RSVPDataMapper rsvpDataMapper;
    private final TicketDataMapper ticketDataMapper;
    private final StudentDataMapper studentDataMapper;
    private final StudentClubDataMapper studentsClubsDataMapper;
    private static StudentRepository instance;

    private StudentRepository(ClubDataMapper clubDataMapper,
                             RSVPDataMapper rsvpDataMapper,
                             TicketDataMapper ticketDataMapper,
                             StudentDataMapper studentDataMapper,
                             StudentClubDataMapper studentsClubsDataMapper) {
        this.clubDataMapper = clubDataMapper;
        this.rsvpDataMapper = rsvpDataMapper;
        this.ticketDataMapper = ticketDataMapper;
        this.studentDataMapper = studentDataMapper;
        this.studentsClubsDataMapper = studentsClubsDataMapper;
    }
    public static synchronized StudentRepository getInstance(ClubDataMapper clubDataMapper,
                                                             RSVPDataMapper rsvpDataMapper,
                                                             TicketDataMapper ticketDataMapper,
                                                             StudentDataMapper studentDataMapper,
                                                             StudentClubDataMapper studentsClubsDataMapper){
        if(instance == null){
            instance = new StudentRepository(clubDataMapper,rsvpDataMapper,ticketDataMapper,studentDataMapper,studentsClubsDataMapper);
        }
        return instance;
    }
    public Student findStudentById(int id) throws SQLException {
        Student student = studentDataMapper.findStudentById(id);
        student.setClubId(studentsClubsDataMapper.findClubIdByStudentId(id));
        student.setRsvpsId(rsvpDataMapper.findRSVPIdByStudentId(id));
        student.setTicketsId(ticketDataMapper.getTicketsIdFromStudent(id));
        return student;
    }
    public List<Student> findStudentsById(List<Integer> ids) {
        List<Student> students = null;
        try {
            students = studentDataMapper.findStudentsByIds(ids);
            for (Student student:students){
                student.setClubId(studentsClubsDataMapper.findClubIdByStudentId(Math.toIntExact(student.getId())));
                student.setRsvpsId(rsvpDataMapper.findRSVPIdByStudentId(Math.toIntExact(student.getId())));
                student.setTicketsId(ticketDataMapper.getTicketsIdFromStudent(Math.toIntExact(student.getId())));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return students;
    }
    public Student findStudentByUsername(String username) throws SQLException {
        return studentDataMapper.findStudentByUsername(username);
    }
    public List<Student> getAllStudent() {
        return studentDataMapper.getAllStudent();
    }

    public List<Student> findStudentByName(String name) {
        return studentDataMapper.findStudentByName(name);
    }
    public List<Student> findStudentByEmail(String email) {
        return studentDataMapper.findStudentByEmail(email);
    }

    public Student lazyLoadClub(Student student){
        try {
            List<Club> clubs = clubDataMapper.findClubsByIds(student.getClubId());
            student.setClubs(clubs);
        } catch (SQLException e) {
            throw new RuntimeException("Error loading students for club", e);
        }
        return student;
    }
    public Student lazyLoadTicket(Student student){
        try {
            List<Ticket> tickets = ticketDataMapper.findTicketsByIds(student.getTicketsId());
            student.setTickets(tickets);
        } catch (SQLException e) {
            throw new RuntimeException("Error loading students for club", e);
        }
        return student;
    }
    public Student lazyLoadRSVP(Student student){
        try {
            List<RSVP> rsvps = rsvpDataMapper.findRSVPsByIds(student.getRsvpsId());
            student.setRsvps(rsvps);
        } catch (SQLException e) {
            throw new RuntimeException("Error loading students for club", e);
        }
        return student;
    }
}
