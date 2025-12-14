package com.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class JdbcDataSource {

    private final String dbUrl;
    private final String user;
    private final String password;


    public JdbcDataSource(String dbUrl, String user, String password) {
        this.dbUrl = dbUrl;
        this.user = user;
        this.password = password;
    }

    Connection getConnection() throws SQLException {
        return DriverManager.getConnection(dbUrl, user, password);
    }
}




