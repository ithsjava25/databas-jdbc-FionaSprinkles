package com.example;

import java.sql.*;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import static com.example.UtilsInput.readInt;

public class Main {

    public static void main(String[] args) {
        if (isDevMode(args)) {
            DevDatabaseInitializer.start();
        }
        new Main().run();
    }

    public void run() {
        // Resolve DB settings with precedence: System properties -> Environment variables
        String dbUrl = resolveConfig("APP_JDBC_URL", "APP_JDBC_URL");
        String dbUser = resolveConfig("APP_DB_USER", "APP_DB_USER");
        String dbPass = resolveConfig("APP_DB_PASS", "APP_DB_PASS");

        if (dbUrl == null || dbUser == null || dbPass == null) {
            throw new IllegalStateException(
                    "Missing DB configuration. Provide APP_JDBC_URL, APP_DB_USER, APP_DB_PASS " +
                            "as system properties (-Dkey=value) or environment variables.");
        }

        JdbcDataSource dS = new JdbcDataSource(dbUrl, dbUser, dbPass);
        MoonMissionRepository moonMissionRepository = new MoonMissionRepositoryImpl(dS);
        AccountRepository accountRepository = new AccountRepositoryImpl(dS);

            Scanner sc = new Scanner(System.in);
            login(sc, accountRepository);
            menu(sc, moonMissionRepository, accountRepository);


        //Todo: Starting point for your code


    }

    // Main Menu
    private void menu(Scanner sc, MoonMissionRepository moonMissionRepository, AccountRepository accountRepository) {
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
                        List<String> missions = moonMissionRepository.listAllMoonMissions();
                        for (String m : missions) {
                            System.out.println(m);
                        }

                        break;
                    case 2: // Get a moon mission by mission_id
                        Integer moonMissionId = readInt(sc, "Mission id");
                        if (moonMissionId == null) {
                            break;
                        }

                        String getMission = moonMissionRepository.getMoonMissionByID(moonMissionId);
                        if (getMission == null) {
                            System.out.println("Mission not found.");
                        } else {
                            System.out.println(getMission);
                        }

                        break;
                    case 3: // Count missions for a given year
                            Integer year = readInt(sc, "What year would you like to see? : \n Write year YYYY");
                            if (year == null) {
                                break;
                            }
                            int countMissions = moonMissionRepository.countMissionsByYear(year);
                            if (countMissions == 0) {
                                System.out.println("Mission not found.");
                            } else {

                                System.out.println("In " + year + " there were " + countMissions + " mission/missions.");
                            }

                        break;
                    case 4: //Create an account

                        System.out.println("Create new account");

                        System.out.println("Please enter new password:");
                        String password = sc.nextLine();
                        System.out.println("Please enter your first name:");
                        String firstName = sc.nextLine();
                        System.out.println("Please enter your last name:");
                        String lastName = sc.nextLine();
                        System.out.println("Please enter your ssn:");
                        String ssn = sc.nextLine();

                        String username = accountRepository.createAccount(password,firstName,lastName,ssn);


                        System.out.println("Account created successfully");
                        System.out.println("Your username: " + username);

                        break;
                    case 5: // Update an account password
                        Integer userID =  readInt(sc, "Please enter your userID:");
                        if (userID == null) {
                            return;
                        }
                        System.out.println("Please enter your new password:");
                        String newPassword = sc.nextLine();

                        boolean updated = accountRepository.updatePassword(userID, newPassword);

                        if (updated) {
                            System.out.println("Your password has been updated");
                        } else
                            System.out.println("No account found with userID: " + userID);

                        break;
                    case 6: // Delete an account
                        userID = readInt(sc, "To delete user, please enter userID:");
                        if (userID == null) {
                            return;
                        }

                        boolean deleted = accountRepository.deleteAccount(userID);
                        if (deleted) {
                            System.out.println("Account deleted successfully");
                        } else {
                            System.out.println("No account found with userID: " + userID);
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

    // LogIn with username and password
    private static void login(Scanner sc , AccountRepository accountRepository) {
        while (true) {
            System.out.println("Enter username:");
            String username = sc.nextLine();
            System.out.println("Enter password:");
            String password = sc.nextLine();

            boolean login = accountRepository.login(username, password);

            if (login == true) {
                System.out.println("You logged in as " + username);
                break;
            } else {
                System.out.println("Invalid username or password");
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
