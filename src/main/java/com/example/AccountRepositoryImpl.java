package com.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AccountRepositoryImpl implements AccountRepository {

    private final JdbcDataSource dataSource;

    public AccountRepositoryImpl(JdbcDataSource dataSource) {
        this.dataSource = dataSource;
    }


    @Override
    public boolean login(String username, String password) {
        String query = "SELECT user_id FROM account WHERE name = ? AND password = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, username);
            statement.setString(2, password);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return true;
                } else return false;

                }
            } catch (SQLException e) {
                throw new RuntimeException(e);

            }
        }


    // Menu Option 4 : Create an account
    @Override
    public String createAccount(String password, String firstName, String lastName, String ssn) {


        // Generate username

        String name1 = firstName.length() < 3 ? firstName : firstName.substring(0, 3);
        String name2 = lastName.length() < 3 ? lastName : lastName.substring(0, 3);
        String name = name1 + name2;

        // Make new account

        String query = "INSERT INTO account (password, first_name, last_name, ssn, name) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)){

        statement.setString(1, password);
        statement.setString(2, firstName);
        statement.setString(3, lastName);
        statement.setString(4, ssn);
        statement.setString(5, name);
        statement.executeUpdate();

        return name;


        } catch (SQLException e) {
            throw new RuntimeException(e);


    }
    }

    @Override
    public boolean updatePassword(int userID, String newPassword) {

        String query = "UPDATE account SET password=? WHERE user_id=?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement update = connection.prepareStatement(query)) {

            update.setString(1, newPassword);
            update.setInt(2, userID);

            int updated = update.executeUpdate();
            return updated > 0;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    // Menu Option 6 : Delete an account
    @Override
    public boolean deleteAccount(int userID) {
        String query = "DELETE FROM account WHERE user_id = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement delete = connection.prepareStatement(query)) {

            delete.setInt(1, userID);
            int deleted = delete.executeUpdate();
            return deleted > 0;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
