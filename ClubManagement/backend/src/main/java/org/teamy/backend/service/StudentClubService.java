package org.teamy.backend.service;

import org.teamy.backend.DataMapper.ClubDataMapper;
import org.teamy.backend.DataMapper.StudentClubDataMapper;
import org.teamy.backend.model.Student;
import org.teamy.backend.repository.ClubRepository;
import org.teamy.backend.repository.RSVPRepository;
import org.teamy.backend.repository.StudentClubRepository;

import java.sql.SQLException;
import java.util.List;

public class StudentClubService {
    private final StudentClubRepository studentClubRepository;
    private final ClubRepository clubRepository;
    private static StudentClubService instance;
    public static synchronized StudentClubService getInstance(StudentClubRepository studentClubRepository, ClubRepository clubRepository) {
        if (instance == null) {
            instance = new StudentClubService(studentClubRepository,clubRepository);
        }
        return instance;
    }
    private StudentClubService(StudentClubRepository studentClubRepository, ClubRepository clubRepository) {
        this.studentClubRepository = studentClubRepository;
        this.clubRepository = clubRepository;
    }
    public void addAdmin(Integer clubId,Integer studentId){
        try {
            studentClubRepository.addNewAdmin(clubId,studentId);
            clubRepository.invalidateClubCache(clubId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void deleteAdmin(Integer clubId,Integer studentId){
        try {
            studentClubRepository.deleteAdmin(clubId,studentId);
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
