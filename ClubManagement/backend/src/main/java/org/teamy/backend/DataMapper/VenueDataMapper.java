package org.teamy.backend.DataMapper;

import org.teamy.backend.config.DatabaseConnectionManager;
import org.teamy.backend.model.Ticket;
import org.teamy.backend.model.TicketStatus;
import org.teamy.backend.model.Venue;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class VenueDataMapper {
    private final DatabaseConnectionManager databaseConnectionManager;

    public VenueDataMapper(DatabaseConnectionManager databaseConnectionManager) {
        this.databaseConnectionManager = databaseConnectionManager;
    }
    public Venue findVenueById(Integer Id){
        var connection = databaseConnectionManager.nextConnection();
        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM venues WHERE id = ?");
            stmt.setInt(1, Id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Venue( rs.getInt("id"),rs.getString("name"), rs.getString("description"),rs.getString("location"),  rs.getInt("capacity"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            databaseConnectionManager.releaseConnection(connection);
        }
        return null;
    }
    public List<Venue> getAllVenue(){
        var connection = databaseConnectionManager.nextConnection();
        List<Venue> venues =new ArrayList<>();
        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM venues");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Venue venue = new Venue( rs.getInt("id"),rs.getString("name"), rs.getString("description"),rs.getString("location"),  rs.getInt("capacity"));
                venues.add(venue);
            }
            return venues;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            databaseConnectionManager.releaseConnection(connection);
        }
    }
}
