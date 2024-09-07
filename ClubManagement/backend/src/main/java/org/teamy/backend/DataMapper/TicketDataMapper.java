package org.teamy.backend.DataMapper;

import org.teamy.backend.config.DatabaseConnectionManager;
import org.teamy.backend.model.Event;
import org.teamy.backend.model.Student;
import org.teamy.backend.model.Ticket;
import org.teamy.backend.model.TicketStatus;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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
                return new Ticket( rs.getInt("ticket_id"), rs.getInt("student_id"),rs.getInt("rsvp"), TicketStatus.valueOf(rs.getString("status")) ,rs.getInt("event_id")
                );

            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            databaseConnectionManager.releaseConnection(connection);
        }
        return null;
    }

    public void saveTicket(Ticket ticket) throws SQLException {
        var connection = databaseConnectionManager.nextConnection();
        try {
            PreparedStatement stmt = connection.prepareStatement(
                    "INSERT INTO tickets (student_id, rsvp,status,event_id) VALUES (?, ?,?,?)"
            );
            stmt.setInt(1, ticket.getStudentId());
            stmt.setInt(2, ticket.getRsvpId());
            stmt.setString(3,ticket.getStatus().name());
            stmt.setInt(4,ticket.getEventId());
            stmt.executeUpdate();
        } finally {
            databaseConnectionManager.releaseConnection(connection);
        }
    }
    public void deleteTicket(Integer ticketId)throws SQLException{
        var connection = databaseConnectionManager.nextConnection();
        try {
            // 更新事件状态为 "Cancelled"
            PreparedStatement stmt = connection.prepareStatement("UPDATE tickets SET status = ? WHERE ticket_id = ?");
            stmt.setString(1, "Cancelled");
            stmt.setInt(2, ticketId);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new RuntimeException("No ticket found with id: " + ticketId);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            databaseConnectionManager.releaseConnection(connection);
        }
    }

    public List<Ticket> getTicketsFromEvent(Integer eventId) throws SQLException {
        var connection = databaseConnectionManager.nextConnection();
        List<Ticket> tickets = new ArrayList<>();
        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM tickets WHERE event_id = ?");
            stmt.setInt(1, eventId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Ticket ticket = new Ticket( rs.getInt("ticket_id"), rs.getInt("student_id"),rs.getInt("rsvp"), TicketStatus.valueOf(rs.getString("status")) ,rs.getInt("event_id")
                );
                tickets.add(ticket);
            }
        } finally {
            databaseConnectionManager.releaseConnection(connection);
        }

        return tickets;
    }

    public List<Ticket> getTicketsFromStudent(Integer studentId) throws SQLException {
        var connection = databaseConnectionManager.nextConnection();
        List<Ticket> tickets = new ArrayList<>();
        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM tickets WHERE student_id = ?");
            stmt.setInt(1, studentId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Ticket ticket = new Ticket( rs.getInt("ticket_id"), rs.getInt("student_id"),rs.getInt("rsvp"), TicketStatus.valueOf(rs.getString("status")) ,rs.getInt("event_id"));
                tickets.add(ticket);
            }
        } finally {
            databaseConnectionManager.releaseConnection(connection);
        }

        return tickets;
    }
}
