package org.teamy.backend.DataMapper;

import org.teamy.backend.config.DatabaseConnectionManager;
import org.teamy.backend.model.Club;
import org.teamy.backend.model.Student;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class ClubDataMapper {
    private final DatabaseConnectionManager databaseConnectionManager;

    public ClubDataMapper(DatabaseConnectionManager databaseConnectionManager) {
        this.databaseConnectionManager = databaseConnectionManager;
    }
    public Club findClubById(int Id) throws Exception {
        var connection = databaseConnectionManager.nextConnection();

        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM clubs WHERE clubs.club_id = ?");
            stmt.setInt(1, Id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Club(rs.getString("name"),rs.getString("description"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            databaseConnectionManager.releaseConnection(connection);

        }
        return null;
    }
    public Club findClubByName(String name) throws Exception {

        var connection = databaseConnectionManager.nextConnection();

        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM clubs WHERE name = ?");
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Club(rs.getString("name"),rs.getString("description"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            databaseConnectionManager.releaseConnection(connection);
        }
        return null;
    }

    public boolean saveClub(Club club) throws Exception {
        var connection = databaseConnectionManager.nextConnection();

        String query = "INSERT INTO clubs (name, description) VALUES (?,?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, club.getName());
            stmt.setString(2, club.getDescription());
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;  // If insert successful, return true
        } catch (SQLException e) {
            e.printStackTrace();
            throw new Exception("Error saving club: " + e.getMessage());
        }finally {
            databaseConnectionManager.releaseConnection(connection);
        }
    }

    public List<Club> getAllClub() throws Exception {
        var connection = databaseConnectionManager.nextConnection();

        List<Club> clubs = new ArrayList<>();

        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM clubs ");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Club club = new Club(
                        rs.getString("name"),
                        rs.getString("description")
                );
                clubs.add(club);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            databaseConnectionManager.releaseConnection(connection);
        }

        return clubs;
    }
}
