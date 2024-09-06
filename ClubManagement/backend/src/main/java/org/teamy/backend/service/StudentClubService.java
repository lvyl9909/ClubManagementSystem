package org.teamy.backend.service;

import org.teamy.backend.DataMapper.StudentClubDataMapper;

import java.sql.SQLException;

public class StudentClubService {
    private final StudentClubDataMapper studentClubDataMapper;

    public StudentClubService(StudentClubDataMapper studentClubDataMapper) {
        this.studentClubDataMapper = studentClubDataMapper;
    }
    public void addAdmin(Integer clubId,Integer studentId){
        try {
            studentClubDataMapper.addNewAdmin(clubId,studentId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void deleteAdmin(Integer clubId,Integer studentId){
        try {
            studentClubDataMapper.deleteAdmin(clubId,studentId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
