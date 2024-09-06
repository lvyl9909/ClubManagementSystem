package org.teamy.backend.DataMapper;

import org.teamy.backend.config.DatabaseConnectionManager;
import org.teamy.backend.model.RSVP;

import java.sql.Array;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
                    "INSERT INTO rsvps (student_id, event, number,participates_id) VALUES (?, ?, ?,?)"
            );
            stmt.setInt(1, rsvp.getSubmitterId());
            stmt.setInt(2, rsvp.getEventId());
            stmt.setInt(3, rsvp.getNumber());

            // 将 List<Integer> 转换为 PostgreSQL 的 Array 类型
            List<Integer> participatesIdList = rsvp.getParticipantIds();
            Integer[] participatesIdArray = participatesIdList.toArray(new Integer[0]);
            Array sqlArray = connection.createArrayOf("INTEGER", participatesIdArray);
            stmt.setArray(4,sqlArray);

            stmt.executeUpdate();
        } finally {
            databaseConnectionManager.releaseConnection(connection);
        }
    }
}
