package org.teamy.backend.DataMapper;

import org.teamy.backend.model.Club;
import org.teamy.backend.model.Student;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ClubDataMapper {
    private Connection connection;

    public ClubDataMapper(Connection connection) {
        this.connection = connection;
    }
    public Club findClubById(int Id) throws Exception {
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM clubs WHERE clubs.club_id = ?");
        stmt.setInt(1, Id);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            return new Club(rs.getString("name"),rs.getString("description"));
        }
        return null;
    }
    public Club findClubByName(String name) throws Exception {
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM clubs WHERE name = ?");
        stmt.setString(1, name);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            return new Club(rs.getString("name"),rs.getString("description"));
        }
        return null;
    }

    public boolean saveClub(Club club) throws Exception {
        String query = "INSERT INTO clubs (name, description) VALUES (?,?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, club.getName());
            stmt.setString(2, club.getDescription());
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;  // If insert successful, return true
        } catch (SQLException e) {
            e.printStackTrace();
            throw new Exception("Error saving club: " + e.getMessage());
        }
    }

    public List<Club> getAllClub() throws Exception {
        List<Club> clubs = new ArrayList<>();

        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM clubs ");
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            Club club = new Club(
                    rs.getString("name"),
                    rs.getString("description")
            );
            clubs.add(club);
        }

        return clubs;
    }
}
