package org.teamy.backend.repository;

import org.teamy.backend.DataMapper.StudentClubDataMapper;

import java.sql.SQLException;
import java.util.List;

public class StudentClubRepository {
    private final StudentClubDataMapper studentClubDataMapper;
    private static StudentClubRepository instance;

    private StudentClubRepository(StudentClubDataMapper studentClubDataMapper) {
        this.studentClubDataMapper = studentClubDataMapper;
    }
    public static synchronized StudentClubRepository getInstance(StudentClubDataMapper studentClubDataMapper){
        if(instance == null){
            instance = new StudentClubRepository(studentClubDataMapper);
        }
        return instance;
    }
    public List<Integer> findStudentIdByClubId(Integer clubId) throws SQLException {
        return studentClubDataMapper.findStudentIdByClubId(clubId);
    }
    public List<Integer> findClubIdByStudentId(Integer studentId) throws SQLException {
        return studentClubDataMapper.findClubIdByStudentId(studentId);
    }
    public void addNewAdmin(Integer clubId,Integer studentId) throws SQLException {
        studentClubDataMapper.addNewAdmin(clubId,studentId);
    }
    public void deleteAdmin(Integer clubId,Integer studentId) throws SQLException {
        studentClubDataMapper.deleteAdmin(clubId,studentId);
    }
}
