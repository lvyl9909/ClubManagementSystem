package org.teamy.backend.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.Duration;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import static javax.management.remote.JMXConnectorFactory.connect;

public class DatabaseConnectionManager {
    private static final int MAX_CONNECTIONS = 10;
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
            return connectionPool.poll(ACQUIRE_CONNECTION_TIMEOUT.toMillis(), TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }

    public void releaseConnection(Connection connection) {
        try {
            connectionPool.offer(connection, ACQUIRE_CONNECTION_TIMEOUT.toMillis(), TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }
}
