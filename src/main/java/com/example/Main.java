package com.example;

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
                case 1:
                    System.out.println("List moon missions");
                    break;
                case 2:
                    System.out.println("Get a moon mission by mission_id");
                    break;
                case 3:
                    System.out.println("Count missions for a given year");
                    break;
                case 4:
                    System.out.println("Create an account");
                    break;
                case 5:
                    System.out.println("Update an account password");
                    break;
                case 6:
                    System.out.println("Delete an account");
                    break;
                case 0:
                    System.out.println("Exit");
                    return;

                    default:
                        System.out.println("Invalid choice.");
            }

        }
    }

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
