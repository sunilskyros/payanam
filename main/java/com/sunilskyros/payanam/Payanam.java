package com.sunilskyros.payanam;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.sunilskyros.payanam.features.signin.SignInView;
import com.sunilskyros.payanam.features.signup.SignUpView;
import com.sunilskyros.payanam.util.InputAndValidation;

@SpringBootApplication
public class Payanam {

    public static final int VERSION_NUMBER = 2;
    public static final String APP_VERSION = "2.0.1";
    public static final String APP_NAME = "Payanam";

    /**
     * The main entry point for the Payanam Bus Tracking application.
     * @param args Command-line arguments (not used).
     */
    public static void main(String[] args) {
        // Run Spring Boot on a separate thread or just launch it
        // The embedded Tomcat will run on its own daemon threads.
        SpringApplication.run(Payanam.class, args);
        
        // System.out.println("\n\tWelcome to " + APP_NAME + " - Version: " + APP_VERSION+
        //                    "\n--------------------------------------------------------");
        
        // Restore the CLI so it continues to function as before
       showLandingMenu();
    }

    /**
     * Displays the primary navigation menu.
     * Offers options to Sign Up, Sign In, or Exit the application.
     * Continuously runs until the user chooses to exit.
     */
    private static void showLandingMenu() {
        try {
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
                        System.exit(0); // Exit the entire application including Spring
                        return;
                    default:
                        System.out.println("Invalid option. Please try again.");
                }
            }
        } catch (java.util.NoSuchElementException | IllegalStateException e) {
            // This happens when running the app in an IDE without an interactive console.
            System.out.println("\n[Payanam Console Menu Disabled]");
            System.out.println("Running in non-interactive mode. Please use the Web Interface at http://localhost:8080/");
        }
    }
}
