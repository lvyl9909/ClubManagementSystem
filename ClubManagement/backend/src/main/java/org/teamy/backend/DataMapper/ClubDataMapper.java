package org.teamy.backend.DataMapper;

import org.teamy.backend.model.Club;
import org.teamy.backend.model.Student;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ClubDataMapper {
    private Connection connection;

    public ClubDataMapper(Connection connection) {
        this.connection = connection;
    }
    public Club findStudentById(int Id) throws Exception {
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM clubs WHERE clubs.club_id = ?");
        stmt.setInt(1, Id);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            return new Club(rs.getString("name"),rs.getString("description"));
        }
        return null;
    }
    public Club findStudentByName(String name) throws Exception {
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM clubs WHERE name = ?");
        stmt.setString(1, name);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            return new Club(rs.getString("name"),rs.getString("description"));
        }
        return null;
    }
}
