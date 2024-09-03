package org.teamy.backend.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.teamy.backend.DataMapper.StudentDataMapper;
import org.teamy.backend.model.Club;
import org.teamy.backend.model.Student;

import java.util.ArrayList;
import java.util.List;

public class StudentService {
    private StudentDataMapper studentDataMapper;
    private final ClubService clubService;
    public StudentService(StudentDataMapper studentDataMapper,ClubService clubService) {
        this.studentDataMapper = studentDataMapper;
        this.clubService = clubService;
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

    public boolean updateStudent(Student student) throws Exception {
        // You can add additional business logic here, such as data validation
        if (student.getName() == null || student.getName().isEmpty()) {
            throw new IllegalArgumentException("Student name cannot be empty");
        }
        // Recall methods in DAO layer
        return studentDataMapper.updateStudent(student);
    }

    public Student getStudentByStudentId(String studentId) throws Exception {
        // Here you can add business logic, such as checking whether the studentId is empty
        if (studentId == null || studentId.trim().isEmpty()) {
            throw new IllegalArgumentException("Student ID cannot be null or empty");
        }

        Student student = studentDataMapper.findStudentByStudentId(studentId);

        if (student == null) {
            throw new Exception("Student with ID " + studentId + " not found");
        }

        return student;
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

}
