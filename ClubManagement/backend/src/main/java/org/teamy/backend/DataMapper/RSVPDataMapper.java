package org.teamy.backend.DataMapper;

import org.teamy.backend.config.DatabaseConnectionManager;
import org.teamy.backend.model.RSVP;

import java.sql.*;
import java.util.Arrays;
import java.util.List;

public class RSVPDataMapper {
    private final DatabaseConnectionManager databaseConnectionManager;

    public RSVPDataMapper(DatabaseConnectionManager databaseConnectionManager) {
        this.databaseConnectionManager = databaseConnectionManager;
    }
    public RSVP findRSVPById(int Id) throws Exception {
        var connection = databaseConnectionManager.nextConnection();
        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM rsvps WHERE rsvp_id = ?");
            stmt.setInt(1, Id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Array numberArray = rs.getArray("participates_id");
                Integer[] numbers = (Integer[]) numberArray.getArray(); // 将 SQL Array 转换为 Integer[] 数组

                // 将 Integer[] 转换为 List<Integer>
                List<Integer> participants = Arrays.asList(numbers);
                return new RSVP( rs.getInt("rsvp_id"), rs.getInt("student_id"),rs.getInt("event"),rs.getInt("number"),participants);

            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            databaseConnectionManager.releaseConnection(connection);
        }
        return null;
    }

    public void saveRSVP(RSVP rsvp) throws SQLException {
        var connection = databaseConnectionManager.nextConnection();
        try {
            PreparedStatement stmt = connection.prepareStatement(
                    "INSERT INTO rsvps (student_id, event, number, participates_id) VALUES (?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS  // 告诉 PreparedStatement 返回自增的主键
            );
            stmt.setInt(1, rsvp.getSubmitterId());
            stmt.setInt(2, rsvp.getEventId());
            stmt.setInt(3, rsvp.getNumber());

// 将 List<Integer> 转换为 PostgreSQL 的 Array 类型
            List<Integer> participatesIdList = rsvp.getParticipantIds();
            Integer[] participatesIdArray = participatesIdList.toArray(new Integer[0]);
            Array sqlArray = connection.createArrayOf("INTEGER", participatesIdArray);
            stmt.setArray(4, sqlArray);

// 执行插入操作
            int affectedRows = stmt.executeUpdate();

// 检查插入是否成功
            if (affectedRows == 0) {
                throw new SQLException("Creating RSVP failed, no rows affected.");
            }

// 获取生成的主键
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    rsvp.setId(generatedKeys.getInt(1));  // 更新到 RSVP 对象中
                } else {
                    throw new SQLException("Creating RSVP failed, no ID obtained.");
                }
            }
        } finally {
            databaseConnectionManager.releaseConnection(connection);
        }
    }
}
