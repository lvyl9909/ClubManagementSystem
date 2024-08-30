package org.teamy.backend.service;

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
//        if (club == null) {
//            throw new Exception("Club with ID " + id + " not found");
//        }
        return student;
    }

    public List<Club> getClubStudentJoin(int id)  {
        if (id <= 0) {
            throw new IllegalArgumentException("Club ID must be positive");
        }

        Student student;
        try {
            student = studentDataMapper.findStudentById(id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        List<Club> clubs = new ArrayList<>();
        for (Integer clubId : student.getClubId()) {
            try {
                Club club = clubService.getClubById(clubId);
                clubs.add(club);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        // 设置到Student对象中（如果需要存储）
        student.setClubs(clubs);

        return clubs;
    }

}
