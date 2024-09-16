package org.teamy.backend.service;

import org.teamy.backend.DataMapper.ClubDataMapper;
import org.teamy.backend.DataMapper.StudentClubDataMapper;
import org.teamy.backend.model.Student;
import org.teamy.backend.repository.ClubRepository;
import org.teamy.backend.repository.RSVPRepository;
import org.teamy.backend.repository.StudentClubRepository;
import org.teamy.backend.repository.StudentRepository;

import java.sql.SQLException;
import java.util.List;

public class StudentClubService {
    private final StudentClubRepository studentClubRepository;
    private final ClubRepository clubRepository;
    private final StudentRepository studentRepository;
    private static StudentClubService instance;
    public static synchronized StudentClubService getInstance(StudentClubRepository studentClubRepository, ClubRepository clubRepository, StudentRepository studentRepository) {
        if (instance == null) {
            instance = new StudentClubService(studentClubRepository,clubRepository,studentRepository);
        }
        return instance;
    }
    private StudentClubService(StudentClubRepository studentClubRepository, ClubRepository clubRepository, StudentRepository studentRepository) {
        this.studentClubRepository = studentClubRepository;
        this.clubRepository = clubRepository;
        this.studentRepository = studentRepository;
    }
    public void addAdmin(Integer clubId,Integer studentId){
        try {
            studentClubRepository.addNewAdmin(clubId,studentId);
            clubRepository.invalidateClubCache(clubId);
            studentRepository.invalidateStudentCache(studentId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void deleteAdmin(Integer clubId,Integer studentId){
        try {
            studentClubRepository.deleteAdmin(clubId,studentId);
            clubRepository.invalidateClubCache(clubId);
            studentRepository.invalidateStudentCache(studentId);
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
