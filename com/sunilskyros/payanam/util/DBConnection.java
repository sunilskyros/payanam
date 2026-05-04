package com.sunilskyros.payanam.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    // Update these credentials to match your MySQL database configuration
    private static final String URL = "jdbc:mysql://localhost:3306/payanam";
    private static final String USER = "root";
    private static final String PASSWORD = "Sunil@123";

    private static Connection connection = null;

    // Private constructor to prevent instantiation
    private DBConnection() {
    }

    public static Connection getConnection() {
        if (connection == null) {
            try {
                // The new MySQL driver class
                Class.forName("com.mysql.cj.jdbc.Driver");
                
                // Establish the connection
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("Database connection established successfully.");
            } catch (ClassNotFoundException e) {
                System.err.println("MySQL JDBC Driver not found. Ensure the connector jar is added to your project structure. " + e.getMessage());
            } catch (SQLException e) {
                System.err.println("Database connection failed. " + e.getMessage());
            }
        }
        return connection;
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                connection = null;
                System.out.println("Database connection closed.");
            } catch (SQLException e) {
                System.err.println("Failed to close database connection. " + e.getMessage());
            }
        }
    }
}
