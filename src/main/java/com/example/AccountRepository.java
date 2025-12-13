package com.example;

public interface AccountRepository {

    boolean login(String username, String password);
    void createAccount(String password, String firstName, String lastName, String ssn);
    boolean updatePassword(int userID, String newPassword);
    boolean deleteAccount(int userID);
}
