package org.teamy.backend.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnectionManager {
    private Connection connection;
    private static final String PROPERTY_JDBC_URI = "jdbc.uri";
    private static final String PROPERTY_JDBC_USERNAME = "jdbc.username";
    private static final String PROPERTY_JDBC_PASSWORD = "jdbc.password";

    // Private constructor to prevent instantiation
    public DatabaseConnectionManager() {
        try {
            // Load the driver
            Class.forName("org.postgresql.Driver");
            // Initialize the connection
            this.connection = DriverManager.getConnection(
                    System.getProperty(PROPERTY_JDBC_URI),
                    System.getProperty(PROPERTY_JDBC_USERNAME),
                    System.getProperty(PROPERTY_JDBC_PASSWORD)
            );
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException("Error initializing the database connection.", e);
        }
    }

    // Method to get the connection object
    public Connection getConnection() {
        return connection;
    }

    // Method to close the connection
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error closing the database connection.", e);
        }
    }
}
