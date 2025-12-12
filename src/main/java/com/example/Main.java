package com.example;

import org.testcontainers.shaded.com.fasterxml.jackson.databind.util.ISO8601Utils;

import javax.management.Query;
import java.sql.*;
import java.util.Arrays;
import java.util.Scanner;

public class Main {


    public static void main(String[] args) {
        if (isDevMode(args)) {
            DevDatabaseInitializer.start();
        }
        new Main().run();
    }

    public void run() {
        // Resolve DB settings with precedence: System properties -> Environment variables
        String jdbcUrl = resolveConfig("APP_JDBC_URL", "APP_JDBC_URL");
        String dbUser = resolveConfig("APP_DB_USER", "APP_DB_USER");
        String dbPass = resolveConfig("APP_DB_PASS", "APP_DB_PASS");

        if (jdbcUrl == null || dbUser == null || dbPass == null) {
            throw new IllegalStateException(
                    "Missing DB configuration. Provide APP_JDBC_URL, APP_DB_USER, APP_DB_PASS " +
                            "as system properties (-Dkey=value) or environment variables.");
        }

        try (Connection connection = DriverManager.getConnection(jdbcUrl, dbUser, dbPass)) {
            Scanner sc = new Scanner(System.in);
            login(sc, connection);
            menu(sc, connection);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        //Todo: Starting point for your code


    }

    //Main Menu
    private void menu(Scanner sc, Connection connection) {
        while (true) {

            System.out.println("1) List moon missions");
            System.out.println("2) Get a moon mission by mission_id");
            System.out.println("3) Count missions for a given year");
            System.out.println("4) Create an account");
            System.out.println("5) Update an account password");
            System.out.println("6) Delete an account");
            System.out.println("0) Exit");

            int choice;

            try {
                choice = Integer.parseInt(sc.nextLine());

            } catch (Exception e) {
                System.out.println("Invalid choice.");
                continue;
            }

            switch (choice) {
                case 1: // List moon missions
                    try {
                        moonMission(connection);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    break;
                case 2: // Get a moon mission by mission_id
                    try {
                        missionID(sc, connection);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    break;
                case 3: // Count missions for a given year
                    try {
                        countYears(sc, connection);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    break;
                case 4: //Create an account
                    try {
                        createNewAccount(sc, connection);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    break;
                case 5: // Update an account password
                    try {
                        updatePassword(sc, connection);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    break;
                case 6: // Delete an account
                    try {
                        deleteAccount(sc, connection);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    break;
                case 0:
                    System.out.println("Exit");
                    return;

                default:
                    System.out.println("Invalid choice.");
            }

        }
    }

    // Menu Option 1 : List Moon Missions
    private void moonMission(Connection connection) throws SQLException {
        String query = "SELECT spacecraft FROM moon_mission";
        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet moonMission = statement.executeQuery()) {
            while (moonMission.next()) {
                String spacecraft = moonMission.getString("spacecraft");
                System.out.println(spacecraft);
            }
        }
    }

    //Menu Option 2 : Get a moon mission by mission_id
    private static void missionID(Scanner sc, Connection connection) throws SQLException {
        System.out.println("Mission id:");
        int missionID = Integer.parseInt(sc.nextLine());

        String query = "select * from moon_mission WHERE mission_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, missionID);

            try (ResultSet missionIdResult = statement.executeQuery()) {

                if (missionIdResult.next()) {
                    String mission = missionIdResult.getString("mission_type");
                    String spacecraft = missionIdResult.getString("spacecraft");
                    String launchDate = missionIdResult.getString("launch_date");
                    String outcome = missionIdResult.getString("outcome");

                    System.out.println("Mission type: " + mission);
                    System.out.println("Launch date: " + launchDate);
                    System.out.println("Outcome: " + outcome);
                    System.out.println("Spacecraft: " + spacecraft);
                } else {
                    System.out.println("No mission found");
                }
            }
        }
    }

    //Menu Option 3 : Count missions for a given year
    private static void countYears(Scanner sc, Connection connection) throws SQLException {
        System.out.println("What year would you like to see?");
        System.out.println("Write year YYYY");

        int year = Integer.parseInt(sc.nextLine());

        String query = "SELECT count(*) FROM moon_mission WHERE YEAR(launch_date) = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)){
        preparedStatement.setInt(1, year);

        try (ResultSet countMissions = preparedStatement.executeQuery()){
        countMissions.next();
        int totalYears = countMissions.getInt(1);
        System.out.println("In " + year + " there were " + totalYears + " mission/missions.");
    }
    }

    }

    // Menu Option 4 : Create an account
    private static void createNewAccount(Scanner sc, Connection connection) throws SQLException {
        System.out.println("Create new account");

        System.out.println("Please enter new password:");
        String password = sc.nextLine();
        System.out.println("Please enter your first name:");
        String firstName = sc.nextLine();
        System.out.println("Please enter your last name:");
        String lastName = sc.nextLine();
        System.out.println("Please enter your ssn:");
        String ssn = sc.nextLine();

        //Generate username
        String name = firstName.substring(0, 3) + lastName.substring(0, 3);

        String query = "INSERT INTO account (password, first_name, last_name, ssn, name) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)){
        preparedStatement.setString(1, password);
        preparedStatement.setString(2, firstName);
        preparedStatement.setString(3, lastName);
        preparedStatement.setString(4, ssn);
        preparedStatement.setString(5, name);
        preparedStatement.executeUpdate();

            System.out.println("Account created successfully");
            System.out.println("Your username: " + name);
        }

    }

    // Menu Option 5 : Update an account password
    private static void updatePassword(Scanner sc, Connection connection) throws SQLException {

        System.out.println("Please enter your userID:");
        int userID = Integer.parseInt(sc.nextLine());
        System.out.println("Please enter your new password:");
        String newPassword = sc.nextLine();
        String query2 = "UPDATE account SET password=? WHERE user_id=?";
        try (PreparedStatement update = connection.prepareStatement(query2)){
        update.setString(1, newPassword);
        update.setInt(2, userID);
        update.executeUpdate();

            System.out.println("Your password has been updated");
            System.out.println("updated");
        }

    }

    // Menu Option 6 : Delete an account
    private static void deleteAccount(Scanner sc, Connection connection) throws SQLException {
        System.out.println("To delete user, enter userID:");
        int userID = Integer.parseInt(sc.nextLine());

        String query = "DELETE FROM account WHERE user_id = ?";
        try (PreparedStatement delete = connection.prepareStatement(query)) {
            delete.setInt(1, userID);
            delete.executeUpdate();

            System.out.println("Account deleted successfully");
        }
    }

    //LogIn with username and password
    private static void login(Scanner sc , Connection connection) {
        while (true) {
            System.out.println("Enter username:");
            String username = sc.nextLine();
            System.out.println("Enter password:");
            String password = sc.nextLine();


            String query = "SELECT user_id FROM account WHERE name = ? AND password = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)){
                statement.setString(1, username);
                statement.setString(2, password);

                ResultSet rs = statement.executeQuery();

                if (rs.next()) {
                    System.out.println("You logged in as " + username);
                    break;
                } else  {
                    System.out.println("Invalid username or password");
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }


        }
    }

    /**
     * Determines if the application is running in development mode based on system properties,
     * environment variables, or command-line arguments.
     *
     * @param args an array of command-line arguments
     * @return {@code true} if the application is in development mode; {@code false} otherwise
     */
    private static boolean isDevMode(String[] args) {
        if (Boolean.getBoolean("devMode"))  //Add VM option -DdevMode=true
            return true;
        if ("true".equalsIgnoreCase(System.getenv("DEV_MODE")))  //Environment variable DEV_MODE=true
            return true;
        return Arrays.asList(args).contains("--dev"); //Argument --dev
    }

    /**
     * Reads configuration with precedence: Java system property first, then environment variable.
     * Returns trimmed value or null if neither source provides a non-empty value.
     */
    private static String resolveConfig(String propertyKey, String envKey) {
        String v = System.getProperty(propertyKey);
        if (v == null || v.trim().isEmpty()) {
            v = System.getenv(envKey);
        }
        return (v == null || v.trim().isEmpty()) ? null : v.trim();
    }
}
