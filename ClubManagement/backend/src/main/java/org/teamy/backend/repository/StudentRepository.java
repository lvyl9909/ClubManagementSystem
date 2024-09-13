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
    private static StudentRepository instance;
    private final Cache<Integer, Student> studentCache;

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

        // Initialize cache with max size and expiration time
        this.studentCache = CacheBuilder.newBuilder()
                .maximumSize(100) // Maximum 100 students in the cache
                .expireAfterWrite(30, TimeUnit.MINUTES) // Expire cache entries after 10 minutes
                .build();
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
    // Find student by ID with caching
    public Student findStudentById(int id) throws SQLException {
        // Check the cache first
        Student student = studentCache.getIfPresent(id);
        if (student != null) {
            return student; // Return cached student if available
        }

        // If not in cache, fetch from the database and cache the result
        student = studentDataMapper.findStudentById(id);
        if (student != null) {
            student.setClubId(studentsClubsDataMapper.findClubIdByStudentId(id));
            student.setRsvpsId(rsvpDataMapper.findRSVPIdByStudentId(id));
            student.setTicketsId(ticketDataMapper.getTicketsIdFromStudent(id));

            // Add to cache
            studentCache.put(id, student);
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

                // Add each student to the cache
                studentCache.put(Math.toIntExact(student.getId()), student);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return students;
    }
    public Student findStudentByUsername(String username) throws SQLException {
        Student student= studentDataMapper.findStudentByUsername(username);
        student.setClubId(studentsClubsDataMapper.findClubIdByStudentId(Math.toIntExact(student.getId())));
        student.setRsvpsId(rsvpDataMapper.findRSVPIdByStudentId(Math.toIntExact(student.getId())));
        student.setTicketsId(ticketDataMapper.getTicketsIdFromStudent(Math.toIntExact(student.getId())));
        return student;
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

            // 懒加载之后，将更新后的学生对象写入缓存
            studentCache.put(Math.toIntExact(student.getId()), student);

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

            // 更新缓存中的学生对象
            studentCache.put(Math.toIntExact(student.getId()), student);

        } catch (SQLException e) {
            throw new RuntimeException("Error loading tickets for student", e);
        }
        return student;
    }
    public Student lazyLoadRSVP(Student student) {
        try {
            // 从数据库加载 RSVP 数据
            List<RSVP> rsvps = rsvpDataMapper.findRSVPsByIds(student.getRsvpsId());
            student.setRsvps(rsvps);

            // 更新缓存中的学生对象
            studentCache.put(Math.toIntExact(student.getId()), student);

        } catch (SQLException e) {
            throw new RuntimeException("Error loading RSVPs for student", e);
        }
        return student;
    }

    public void invalidateStudentCache(Integer studentId) {
        studentCache.invalidate(studentId);
    }
    public void invalidateStudentCaches(List<Integer> studentsId) {
        for (Integer studentId:studentsId){
            studentCache.invalidate(studentId);
        }
    }
}
