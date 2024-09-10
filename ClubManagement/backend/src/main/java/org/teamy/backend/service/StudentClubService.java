package org.teamy.backend.service;

import org.teamy.backend.DataMapper.StudentClubDataMapper;
import org.teamy.backend.model.Student;
import org.teamy.backend.repository.RSVPRepository;
import org.teamy.backend.repository.StudentClubRepository;

import java.sql.SQLException;
import java.util.List;

public class StudentClubService {
    private final StudentClubRepository studentClubRepository;
    private static StudentClubService instance;
    public static synchronized StudentClubService getInstance(StudentClubRepository studentClubRepository) {
        if (instance == null) {
            instance = new StudentClubService(studentClubRepository);
        }
        return instance;
    }
    private StudentClubService(StudentClubRepository studentClubRepository) {
        this.studentClubRepository = studentClubRepository;
    }
    public void addAdmin(Integer clubId,Integer studentId){
        try {
            studentClubRepository.addNewAdmin(clubId,studentId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void deleteAdmin(Integer clubId,Integer studentId){
        try {
            studentClubRepository.deleteAdmin(clubId,studentId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public List<Integer> findStudentIdByClubId(Integer clubId){
        try {
            return studentClubRepository.findStudentIdByClubId(clubId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
