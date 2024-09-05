package org.teamy.backend.DataMapper;

import org.teamy.backend.config.DatabaseConnectionManager;
import org.teamy.backend.model.Event;
import org.teamy.backend.model.Student;
import org.teamy.backend.model.Ticket;
import org.teamy.backend.model.TicketStatus;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class TicketDataMapper {
    private final DatabaseConnectionManager databaseConnectionManager;

    public TicketDataMapper(DatabaseConnectionManager databaseConnectionManager) {
        this.databaseConnectionManager = databaseConnectionManager;
    }
    public Ticket findTicketById(int Id) throws Exception {
        var connection = databaseConnectionManager.nextConnection();
        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM tickets WHERE ticket_id = ?");
            stmt.setInt(1, Id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Ticket( rs.getInt("ticket_id"), rs.getInt("student_id"),rs.getInt("rsvp"),        TicketStatus.valueOf(rs.getString("status")) // 将数据库中的字符串转换为枚举
                );

            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            databaseConnectionManager.releaseConnection(connection);
        }
        return null;
    }

}
