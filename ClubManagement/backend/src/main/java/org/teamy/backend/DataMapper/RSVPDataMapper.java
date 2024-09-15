package org.teamy.backend.DataMapper;

import org.teamy.backend.config.DatabaseConnectionManager;
import org.teamy.backend.model.RSVP;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class RSVPDataMapper {
    private final DatabaseConnectionManager databaseConnectionManager;
    private static RSVPDataMapper instance;
    public static synchronized RSVPDataMapper getInstance(DatabaseConnectionManager dbManager) {
        if (instance == null) {
            instance = new RSVPDataMapper(dbManager);
        }
        return instance;
    }
    private RSVPDataMapper(DatabaseConnectionManager databaseConnectionManager) {
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
    public List<RSVP> findRSVPsByIds(List<Integer> rsvpIds) throws SQLException {
        // 如果 rsvpIds 列表为空，则返回空列表
        if (rsvpIds.isEmpty()) {
            return new ArrayList<>();
        }

        // 构建 SQL 查询，使用 IN 子句来查询多个 rsvp_id
        String query = "SELECT * FROM rsvps WHERE rsvp_id IN (" +
                rsvpIds.stream().map(String::valueOf).collect(Collectors.joining(",")) + ")";

        var connection = databaseConnectionManager.nextConnection();
        List<RSVP> rsvps = new ArrayList<>();

        try {
            PreparedStatement stmt = connection.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            // 遍历结果集，将每个 RSVP 实例化并加入列表
            while (rs.next()) {
                Array numberArray = rs.getArray("participates_id");
                Integer[] numbers = (Integer[]) numberArray.getArray(); // 将 SQL Array 转换为 Integer[] 数组

                // 将 Integer[] 转换为 List<Integer>
                List<Integer> participants = Arrays.asList(numbers);

                RSVP rsvp = new RSVP(
                        rs.getInt("rsvp_id"),
                        rs.getInt("student_id"),
                        rs.getInt("event"),
                        rs.getInt("number"),
                        participants
                );
                rsvps.add(rsvp);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching RSVPs by IDs", e);
        } finally {
            databaseConnectionManager.releaseConnection(connection);
        }

        return rsvps;
    }
    public void saveRSVP(Connection connection,RSVP rsvp) throws SQLException {
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
        }
    }
    public List<Integer> findRSVPIdByStudentId(Integer studentId){
        var connection = databaseConnectionManager.nextConnection();
        List<Integer> eventsId= new ArrayList<>();
        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT rsvp_id FROM rsvps WHERE student_id = ?");
            stmt.setInt(1, studentId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                eventsId.add(rs.getInt("rsvp_id"));
            }
            return eventsId;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            databaseConnectionManager.releaseConnection(connection);

        }
    }
}
