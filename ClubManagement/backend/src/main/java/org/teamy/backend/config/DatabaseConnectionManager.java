package org.teamy.backend.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class DatabaseConnectionManager {
    private static final int MAX_CONNECTIONS = 20;
    private static final Duration ACQUIRE_CONNECTION_TIMEOUT = Duration.ofMillis(100);
    private static final Logger logger = Logger.getLogger(DatabaseConnectionManager.class.getName());

    private final String url;
    private final String username;
    private final String password;
    private final BlockingDeque<TrackedConnection> connectionPool;

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

        logger.info("Initializing connection pool...");
        while (connectionPool.size() < MAX_CONNECTIONS) {
            connectionPool.offer(new TrackedConnection(connect(), UUID.randomUUID().toString()));
        }
        logger.info("Connection pool initialized with " + connectionPool.size() + " connections.");
    }

    private Connection connect() {
        try {
            logger.info("Creating a new database connection...");
            return DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            logger.severe("Failed to create a new database connection: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public Connection nextConnection() {
        try {
            logger.info("Attempting to acquire a connection from the pool...");
            TrackedConnection trackedConnection = connectionPool.poll(ACQUIRE_CONNECTION_TIMEOUT.toMillis(), TimeUnit.MILLISECONDS);

            if (trackedConnection == null) {
                logger.warning("Failed to acquire connection: Pool may be exhausted.");
                throw new RuntimeException("Failed to acquire connection from pool. Pool may be exhausted.");
            }

            Connection connection = trackedConnection.getConnection();
            if (connection.isClosed() || !connection.isValid(2)) {
                logger.warning("Acquired connection is closed or invalid. Creating a new connection...");
                connection = connect();
                trackedConnection = new TrackedConnection(connection, UUID.randomUUID().toString());
            }

            // Log the stack trace where the connection is being used
            logger.info("Connection acquired (ID: " + trackedConnection.getId() + ") from the pool. Acquired by: " + getCurrentStackTrace());

            return connection;
        } catch (InterruptedException | SQLException e) {
            logger.severe("Error while acquiring connection: " + e.getMessage());
            Thread.currentThread().interrupt();
            throw new RuntimeException("Error acquiring connection", e);
        }
    }

    public void releaseConnection(Connection connection) {
        try {
            if (connection.isClosed() || !connection.isValid(2)) {
                logger.warning("Connection is closed or invalid, creating a new one...");
                connection = connect();
            }

            logger.info("Releasing connection back to the pool...");
            connectionPool.offer(new TrackedConnection(connection, UUID.randomUUID().toString()), ACQUIRE_CONNECTION_TIMEOUT.toMillis(), TimeUnit.MILLISECONDS);
            logger.info("Connection released successfully.");
        } catch (InterruptedException | SQLException e) {
            logger.severe("Error while releasing connection: " + e.getMessage());
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }

    // Helper method to log the current stack trace
    private String getCurrentStackTrace() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        StringBuilder sb = new StringBuilder();
        for (int i = 3; i < stackTrace.length; i++) { // Skip the first 3 elements (getStackTrace, this method, and calling method)
            sb.append(stackTrace[i].toString()).append("\n");
        }
        return sb.toString();
    }

    // Inner class to track connection with unique ID
    private static class TrackedConnection {
        private final Connection connection;
        private final String id;

        public TrackedConnection(Connection connection, String id) {
            this.connection = connection;
            this.id = id;
        }

        public Connection getConnection() {
            return connection;
        }

        public String getId() {
            return id;
        }
    }
}