package org.teamy.backend.DataMapper;

import org.teamy.backend.model.Event;
import org.teamy.backend.model.Student;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class EventDataMapper {
    private Connection connection;
    public EventDataMapper(Connection connection) {
        this.connection = connection;
    }
    public Event findEventById(int Id) throws Exception {
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM events WHERE event_id = ?");
        stmt.setInt(1, Id);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            return new Event( rs.getString("title"), rs.getString("description"),rs.getString("venue"),rs.getBigDecimal("cost"),rs.getInt("club_id"));
        }
        return null;
    }

    public Event findEventByTitle(String title) throws Exception {
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM events WHERE title = ?");
        stmt.setString(1, title);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            return new Event( rs.getString("title"), rs.getString("description"),rs.getString("venue"),rs.getBigDecimal("cost"),rs.getInt("club_id"));
        }
        return null;
    }
}
