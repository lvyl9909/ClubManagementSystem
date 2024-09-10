package org.teamy.backend.DataMapper;

import org.teamy.backend.config.DatabaseConnectionManager;
import org.teamy.backend.model.Club;
import org.teamy.backend.model.Ticket;
import org.teamy.backend.model.TicketStatus;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StudentClubDataMapper {
    private final DatabaseConnectionManager databaseConnectionManager;
    private static StudentClubDataMapper instance;
    public static synchronized StudentClubDataMapper getInstance(DatabaseConnectionManager dbManager) {
        if (instance == null) {
            instance = new StudentClubDataMapper(dbManager);
        }
        return instance;
    }
    private StudentClubDataMapper(DatabaseConnectionManager databaseConnectionManager) {
        this.databaseConnectionManager = databaseConnectionManager;
    }
    public List<Integer> findClubIdByStudentId(Integer studentId) throws SQLException {
        var connection = databaseConnectionManager.nextConnection();
        List<Integer> clubsId = new ArrayList<>();
        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM students_clubs WHERE student_id = ?");
            stmt.setInt(1, studentId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                clubsId.add(rs.getInt("club_id"));
            }
        } finally {
            databaseConnectionManager.releaseConnection(connection);
        }
        return clubsId;
    }

    public List<Integer> findStudentIdByClubId(Integer clubId) throws SQLException {
        var connection = databaseConnectionManager.nextConnection();
        List<Integer> studentsId = new ArrayList<>();
        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM students_clubs WHERE club_id = ?");
            stmt.setInt(1, clubId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                studentsId.add(rs.getInt("student_id"));
            }
        } finally {
            databaseConnectionManager.releaseConnection(connection);
        }
        return studentsId;
    }

    public void addNewAdmin(Integer clubId,Integer studentId) throws SQLException {
        var connection = databaseConnectionManager.nextConnection();
        try {
            PreparedStatement stmt = connection.prepareStatement(
                    "INSERT INTO students_clubs (student_id, club_id) VALUES (?, ?)"
            );
            stmt.setInt(1, studentId);
            stmt.setInt(2, clubId);

            stmt.executeUpdate();
        } finally {
            databaseConnectionManager.releaseConnection(connection);
        }
    }
    public void deleteAdmin(Integer clubId,Integer studentId) throws SQLException {
        var connection = databaseConnectionManager.nextConnection();
        try {
            // 创建 SQL 语句，删除满足 clubId 和 studentId 的记录
            String sql = "DELETE FROM students_clubs WHERE club_id = ? AND student_id = ?";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, clubId);  // 设置 clubId 参数
            stmt.setInt(2, studentId);  // 设置 studentId 参数
            // 执行删除操作
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new RuntimeException("No record found with clubId: " + clubId + " and studentId: " + studentId);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        } finally {
            databaseConnectionManager.releaseConnection(connection);
        }
    }
}
