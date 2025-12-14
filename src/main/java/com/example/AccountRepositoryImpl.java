package com.example;

public class AccountRepositoryImpl implements AccountRepository {

    private final JdbcDataSource dataSource;

    public AccountRepositoryImpl(JdbcDataSource dataSource) {
        this.dataSource = dataSource;
    }


    @Override
    public boolean login(String username, String password) {
        return false;
    }

    @Override
    public void createAccount(String password, String firstName, String lastName, String ssn) {

    }

    @Override
    public boolean updatePassword(int userID, String newPassword) {
        return false;
    }

    @Override
    public boolean deleteAccount(int userID) {
        return false;
    }
}
