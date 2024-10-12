package org.teamy.backend.repository;

import org.teamy.backend.DataMapper.StudentClubDataMapper;
import org.teamy.backend.config.DatabaseConnectionManager;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class StudentClubRepository {
    private final StudentClubDataMapper studentClubDataMapper;
    private final DatabaseConnectionManager databaseConnectionManager;
    private static StudentClubRepository instance;

    private StudentClubRepository(StudentClubDataMapper studentClubDataMapper,DatabaseConnectionManager databaseConnectionManager) {
        this.studentClubDataMapper = studentClubDataMapper;
        this.databaseConnectionManager =databaseConnectionManager;
    }
    public static synchronized StudentClubRepository getInstance(StudentClubDataMapper studentClubDataMapper,DatabaseConnectionManager databaseConnectionManager){
        if(instance == null){
            instance = new StudentClubRepository(studentClubDataMapper,databaseConnectionManager);
        }
        return instance;
    }
    public List<Integer> findStudentIdByClubId(Integer clubId) throws SQLException {
        Connection connection = databaseConnectionManager.nextConnection();
        return studentClubDataMapper.findStudentIdByClubId(clubId,connection);
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
