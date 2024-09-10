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
import java.util.stream.Collectors;


public class ClubDataMapper {
    private final DatabaseConnectionManager databaseConnectionManager;

    public ClubDataMapper(DatabaseConnectionManager databaseConnectionManager) {
        this.databaseConnectionManager = databaseConnectionManager;
    }
    public Club findClubById(int Id){
        var connection = databaseConnectionManager.nextConnection();

        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM clubs WHERE clubs.club_id = ?");
            stmt.setInt(1, Id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Club(rs.getInt("club_id"),rs.getString("name"),rs.getString("description"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            databaseConnectionManager.releaseConnection(connection);

        }
        return null;
    }
    public List<Club> findClubsByIds(List<Integer> clubIds) throws SQLException {
        // 如果 clubIds 列表为空，则返回空列表
        if (clubIds.isEmpty()) {
            return new ArrayList<>();
        }

        // 构建 SQL 查询，使用 IN 子句来查询多个 club_id
        String query = "SELECT * FROM clubs WHERE club_id IN (" +
                clubIds.stream().map(String::valueOf).collect(Collectors.joining(",")) + ")";

        var connection = databaseConnectionManager.nextConnection();
        List<Club> clubs = new ArrayList<>();

        try {
            PreparedStatement stmt = connection.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            // 遍历结果集，将每个 Club 实例化并加入列表
            while (rs.next()) {
                Club club = new Club(
                        rs.getInt("club_id"),
                        rs.getString("name"),
                        rs.getString("description")
                );
                clubs.add(club);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching clubs by IDs", e);
        } finally {
            databaseConnectionManager.releaseConnection(connection);
        }

        return clubs;
    }
    public Club findClubByName(String name) throws Exception {

        var connection = databaseConnectionManager.nextConnection();

        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM clubs WHERE name = ?");
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Club(rs.getInt("club_id"),rs.getString("name"),rs.getString("description"));
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

    public List<Club> getAllClub() {
        var connection = databaseConnectionManager.nextConnection();

        List<Club> clubs = new ArrayList<>();

        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM clubs ");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Club club = new Club(
                        rs.getInt("club_id"),
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
