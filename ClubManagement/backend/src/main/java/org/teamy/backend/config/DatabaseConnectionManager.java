package org.teamy.backend.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.Duration;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

public class DatabaseConnectionManager {
    private static final int MAX_CONNECTIONS = 20;
    private static final Duration ACQUIRE_CONNECTION_TIMEOUT = Duration.ofMillis(100);
    private final String url;
    private final String username;
    private final String password;
    private final BlockingDeque<Connection> connectionPool;
    // Private constructor to prevent instantiation
    public DatabaseConnectionManager(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.connectionPool = new LinkedBlockingDeque<>();
    }

    public void init() {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        while (connectionPool.size() < MAX_CONNECTIONS) {
            connectionPool.offer(connect());
        }
    }

    private Connection connect() {
        try {
            return DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public Connection nextConnection() {
        try {
            Connection connection = connectionPool.poll(ACQUIRE_CONNECTION_TIMEOUT.toMillis(), TimeUnit.MILLISECONDS);
            if (connection == null) {
                throw new RuntimeException("Failed to acquire connection from pool. Pool may be exhausted.");
            }
            if (connection.isClosed() || !connection.isValid(2)) {
                connection = connect();  // 创建新连接替换无效连接
            }
            return connection;
        } catch (InterruptedException | SQLException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Error acquiring connection", e);
        }
    }

    public void releaseConnection(Connection connection) {
        try {
            if (connection.isClosed() || !connection.isValid(2)) {  // 2 秒的超时检测
                connection = connect();  // 如果连接无效，重新创建连接
            }
            connectionPool.offer(connection, ACQUIRE_CONNECTION_TIMEOUT.toMillis(), TimeUnit.MILLISECONDS);
        } catch (InterruptedException | SQLException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }
}
