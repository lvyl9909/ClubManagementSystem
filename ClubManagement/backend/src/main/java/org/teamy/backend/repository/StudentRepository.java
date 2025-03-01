package org.teamy.backend.repository;

import org.teamy.backend.DataMapper.*;
import org.teamy.backend.model.*;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class StudentRepository {
    private final ClubDataMapper clubDataMapper;
    private final RSVPDataMapper rsvpDataMapper;
    private final TicketDataMapper ticketDataMapper;
    private final StudentDataMapper studentDataMapper;
    private final StudentClubDataMapper studentsClubsDataMapper;
    private final FundingApplicationMapper fundingApplicationMapper;
    private static StudentRepository instance;
    private StudentRepository(ClubDataMapper clubDataMapper,
                              RSVPDataMapper rsvpDataMapper,
                              TicketDataMapper ticketDataMapper,
                              StudentDataMapper studentDataMapper,
                              StudentClubDataMapper studentsClubsDataMapper, FundingApplicationMapper fundingApplicationMapper) {
        this.clubDataMapper = clubDataMapper;
        this.rsvpDataMapper = rsvpDataMapper;
        this.ticketDataMapper = ticketDataMapper;
        this.studentDataMapper = studentDataMapper;
        this.studentsClubsDataMapper = studentsClubsDataMapper;
        this.fundingApplicationMapper = fundingApplicationMapper;
    }
    public static synchronized StudentRepository getInstance(ClubDataMapper clubDataMapper,
                                                             RSVPDataMapper rsvpDataMapper,
                                                             TicketDataMapper ticketDataMapper,
                                                             StudentDataMapper studentDataMapper,
                                                             StudentClubDataMapper studentsClubsDataMapper, FundingApplicationMapper fundingApplicationMapper){
        if(instance == null){
            instance = new StudentRepository(clubDataMapper,rsvpDataMapper,ticketDataMapper,studentDataMapper,studentsClubsDataMapper,fundingApplicationMapper);
        }
        return instance;
    }
    // Find student by ID with caching
    public Student findStudentById(int id) throws SQLException {
        // Check the cache first
        Student student;
        // If not in cache, fetch from the database and cache the result
        student = studentDataMapper.findStudentById(id);
        if (student != null) {
            student.setClubId(studentsClubsDataMapper.findClubIdByStudentId(id));
            student.setRsvpsId(rsvpDataMapper.findRSVPIdByStudentId(id));
            student.setTicketsId(ticketDataMapper.getTicketsIdFromStudent(id));
        }

        return student;
    }
    public List<Student> findStudentsById(List<Integer> ids) {
        List<Student> students = null;
        try {
            students = studentDataMapper.findStudentsByIds(ids);
            for (Student student : students) {
                student.setClubId(studentsClubsDataMapper.findClubIdByStudentId(Math.toIntExact(student.getId())));
                student.setRsvpsId(rsvpDataMapper.findRSVPIdByStudentId(Math.toIntExact(student.getId())));
                student.setTicketsId(ticketDataMapper.getTicketsIdFromStudent(Math.toIntExact(student.getId())));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return students;
    }
    public Person findUserByUsername(String username) throws SQLException {
        Person person= studentDataMapper.findUserByUsername(username);
        System.out.println(person.toString());

        if (person instanceof Student) {
            Student student = (Student) person;
            student.setClubId(studentsClubsDataMapper.findClubIdByStudentId(Math.toIntExact(student.getId())));
            student.setRsvpsId(rsvpDataMapper.findRSVPIdByStudentId(Math.toIntExact(student.getId())));
            student.setTicketsId(ticketDataMapper.getTicketsIdFromStudent(Math.toIntExact(student.getId())));
            System.out.println(student.toString());
            return student;
        }else if(person instanceof FacultyAdministrator){
            FacultyAdministrator admin = (FacultyAdministrator) person;
//            admin.setFundingApplicationIds(fundingApplicationMapper.findApplicationIdByReviewerId(Math.toIntExact(admin.getId())));
            //读取application
            System.out.println(admin.toString());
            return admin;
        }
        return null;
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

    public Student lazyLoadClub(Student student) {
        try {
            List<Club> clubs = clubDataMapper.findClubsByIds(student.getClubId());
            System.out.println("club list:" + clubs);
            student.setClubs(clubs);
        } catch (SQLException e) {
            throw new RuntimeException("Error loading students for club", e);
        }
        return student;
    }
    public Student lazyLoadTicket(Student student) {
        try {
            // 从数据库加载 Ticket 数据
            System.out.println("lazyloadticket: getticketid"+student.getTicketsId());
            List<Ticket> tickets = ticketDataMapper.findTicketsByIds(student.getTicketsId());
            student.setTickets(tickets);
        } catch (SQLException e) {
            throw new RuntimeException("Error loading tickets for student", e);
        }
        return student;
    }
    public Student lazyLoadRSVP(Student student) {
        try {
            // Load RSVP data from the database
            List<RSVP> rsvps = rsvpDataMapper.findRSVPsByIds(student.getRsvpsId());
            student.setRsvps(rsvps);
        } catch (SQLException e) {
            throw new RuntimeException("Error loading RSVPs for student", e);
        }
        return student;
    }
}
