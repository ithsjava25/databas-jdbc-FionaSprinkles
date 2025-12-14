package com.example;

import java.util.Scanner;

public final class UtilsInput {

    private UtilsInput () {

    }


    // Help method to validate numeric inputs
    public static Integer readInt(Scanner sc, String prompt) {
        System.out.println(prompt);
        String s = sc.nextLine().trim();

        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number.");
            return null;
        }
    }
}
