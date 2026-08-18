package com.parking.smart_parking.core.db;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {
    private static final String URL = "jdbc:Your-SupaBase-URI";
    private static final String USER = "USER";
    private static final String PASSWORD = "Your-password";

    public static Connection getConnection() throws Exception {
        Class.forName("org.postgresql.Driver");
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
