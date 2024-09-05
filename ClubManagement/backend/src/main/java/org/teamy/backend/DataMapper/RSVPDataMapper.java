package org.teamy.backend.DataMapper;

import org.teamy.backend.config.DatabaseConnectionManager;
import org.teamy.backend.model.RSVP;
import org.teamy.backend.model.RSVPStatus;
import org.teamy.backend.model.Ticket;
import org.teamy.backend.model.TicketStatus;

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
                return new RSVP( rs.getInt("rsvp_id"), rs.getInt("student_id"),rs.getInt("event"),RSVPStatus.valueOf(rs.getString("status")),rs.getInt("number"),participants);

            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            databaseConnectionManager.releaseConnection(connection);
        }
        return null;
    }
}
