package com.sunilskyros.payanam;

import com.sunilskyros.payanam.features.signin.SignInView;
import com.sunilskyros.payanam.features.signup.SignUpView;
import com.sunilskyros.payanam.util.InputAndValidation;

public class Payanam {

    public static final int VERSION_NUMBER = 2;
    public static final String APP_VERSION = "1.4.0";
    public static final String APP_NAME = "Payanam";

    /**
     * The main entry point for the Payanam Bus Tracking application.
     * Displays a welcome message and launches the initial landing menu.
     * @param args Command-line arguments (not used).
     */
    public static void main(String[] args) {
        System.out.println("\n\tWelcome to " + APP_NAME + " - Version: " + APP_VERSION+
                           "\n--------------------------------------------------------");
        showLandingMenu();
    }

    /**
     * Displays the primary navigation menu.
     * Offers options to Sign Up, Sign In, or Exit the application.
     * Continuously runs until the user chooses to exit.
     */
    private static void showLandingMenu() {
        while (true) {
            System.out.println("""
                               1. Sign Up
                               2. Sign In
                               3. Exit""");
            String choice = InputAndValidation.getStringInput("Choose an option : ");
            switch (choice) {
                case "1":
                    new SignUpView().init();
                    break;
                case "2":
                    new SignInView().init();
                    break;
                case "3":
                    System.out.println("\nThank you for using Payanam");
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }
}
