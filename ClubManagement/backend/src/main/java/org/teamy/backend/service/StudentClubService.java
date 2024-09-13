package org.teamy.backend.service;

import org.teamy.backend.repository.ClubRepository;
import org.teamy.backend.repository.StudentClubRepository;
import org.teamy.backend.repository.StudentRepository;

import java.sql.SQLException;
import java.util.List;

public class StudentClubService {
    private final StudentClubRepository studentClubRepository;
    private final StudentRepository studentRepository;
    private final ClubRepository clubRepository;
    private static StudentClubService instance;
    public static synchronized StudentClubService getInstance(StudentClubRepository studentClubRepository,StudentRepository studentRepository, ClubRepository clubRepository) {
        if (instance == null) {
            instance = new StudentClubService(studentClubRepository, studentRepository,clubRepository );
        }
        return instance;
    }
    private StudentClubService(StudentClubRepository studentClubRepository, StudentRepository studentRepository, ClubRepository clubRepository) {
        this.studentClubRepository = studentClubRepository;
        this.studentRepository = studentRepository;
        this.clubRepository = clubRepository;
    }
    public void addAdmin(Integer clubId,Integer studentId){
        try {
            studentClubRepository.addNewAdmin(clubId,studentId);
            studentRepository.invalidateStudentCache(studentId);
            clubRepository.invalidateClubCache(clubId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void deleteAdmin(Integer clubId,Integer studentId){
        try {
            studentClubRepository.deleteAdmin(clubId,studentId);
            studentRepository.invalidateStudentCache(studentId);
            clubRepository.invalidateClubCache(clubId);
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
