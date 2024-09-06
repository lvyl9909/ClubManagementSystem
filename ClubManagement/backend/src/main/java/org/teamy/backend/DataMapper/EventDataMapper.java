package org.teamy.backend.DataMapper;

import org.teamy.backend.config.DatabaseConnectionManager;
import org.teamy.backend.model.Club;
import org.teamy.backend.model.Event;
import org.teamy.backend.model.EventStatus;
import org.teamy.backend.model.Student;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EventDataMapper {
    private final DatabaseConnectionManager databaseConnectionManager;
    public EventDataMapper(DatabaseConnectionManager databaseConnectionManager) {
        this.databaseConnectionManager = databaseConnectionManager;
    }
    public Event findEventById(int Id) throws Exception {
        var connection = databaseConnectionManager.nextConnection();

        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM events WHERE event_id = ?");
            stmt.setInt(1, Id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Event( rs.getString("title"), rs.getString("description"),rs.getString("venue"),rs.getBigDecimal("cost"),rs.getInt("club_id"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            databaseConnectionManager.releaseConnection(connection);
        }
        return null;
    }
    public void deleteEvent(int eventId) throws Exception {
        var connection = databaseConnectionManager.nextConnection();

        try {
            // 更新事件状态为 "Cancelled"
            PreparedStatement stmt = connection.prepareStatement("UPDATE events SET status = ? WHERE event_id = ?");
            stmt.setString(1, "Cancelled");
            stmt.setInt(2, eventId);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new RuntimeException("No event found with id: " + eventId);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            databaseConnectionManager.releaseConnection(connection);
        }
    }

    public Event findEventByTitle(String title) throws Exception {
        var connection = databaseConnectionManager.nextConnection();

        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM events WHERE title = ?");
            stmt.setString(1, title);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Event( rs.getString("title"), rs.getString("description"),rs.getString("venue"),rs.getBigDecimal("cost"),rs.getInt("club_id"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            databaseConnectionManager.releaseConnection(connection);
        }
        return null;
    }

    public boolean saveEvent(Event event) throws Exception {
        var connection = databaseConnectionManager.nextConnection();

        String query = "INSERT INTO events (title, description,date,time,venue,cost,club_id,status) VALUES (?,?,?,?,?,?,?,?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, event.getTitle());
            stmt.setString(2, event.getDescription());
            stmt.setDate(3,event.getSqlDate());
            stmt.setTime(4,event.getSqlTime());
            stmt.setInt(5,event.getVenueId());
            stmt.setBigDecimal(6,event.getCost());
            stmt.setInt(7,event.getClub());
            stmt.setString(8, EventStatus.Ongoing.name());
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new Exception("Error saving club: " + e.getMessage());
        }finally {
            databaseConnectionManager.releaseConnection(connection);
        }
    }

    public List<Event> getAllEvent() throws Exception {
        var connection = databaseConnectionManager.nextConnection();

        List<Event> events = new ArrayList<>();

        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM events ");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Event event = new Event(
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getDate("date"),
                        rs.getTime("time"),
                        rs.getString("venue"),
                        rs.getBigDecimal("cost"),
                        rs.getInt("club_id")
                );
                events.add(event);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            databaseConnectionManager.releaseConnection(connection);

        }

        return events;
    }
}
