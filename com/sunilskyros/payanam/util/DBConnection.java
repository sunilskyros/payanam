package com.sunilskyros.payanam.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/payanam";
    private static final String USER = "root";
    private static final String PASSWORD = "Sunil@123";

    private static Connection connection = null;

    private DBConnection() {}

    public static Connection getConnection() {
        try{
            if(connection == null || connection.isClosed()){
                connection=DriverManager.getConnection(URL,USER,PASSWORD);
            }
            else{
                return connection;
            }

        }catch (SQLException e){
            System.err.println("Database connection failed...."+ e.getMessage());
        }
        return null;
    }
}
