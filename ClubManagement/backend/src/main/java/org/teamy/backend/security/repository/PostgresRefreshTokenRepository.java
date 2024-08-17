package org.teamy.backend.security.repository;

import org.teamy.backend.security.model.RefreshToken;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class PostgresRefreshTokenRepository implements RefreshTokenRepository{
    private final Connection connection;

    public PostgresRefreshTokenRepository(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Optional<RefreshToken> get(String id) {
        String sql = "SELECT id, token_id, username FROM refresh_token WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                RefreshToken token = new RefreshToken();
                token.setId(resultSet.getString("id"));
                token.setTokenId(resultSet.getString("token_id"));
                token.setUsername(resultSet.getString("username"));
                return Optional.of(token);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public void save(RefreshToken token) {
        String sql = "INSERT INTO refresh_token (id, token_id, username) VALUES (?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, token.getId());
            statement.setString(2, token.getTokenId());
            statement.setString(3, token.getUsername());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteAllForUsername(String username) {
        String sql = "DELETE FROM refresh_token WHERE username = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(String id) {
        String sql = "DELETE FROM refresh_token WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
