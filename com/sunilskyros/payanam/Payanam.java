package com.sunilskyros.payanam;

import com.sunilskyros.payanam.features.signin.SignInView;
import com.sunilskyros.payanam.features.signup.SignUpView;
import com.sunilskyros.payanam.util.ConsoleInput;

import java.util.Scanner;

public class Payanam {

    public static final int VERSION_NUMBER = 1;
    public static final String APP_VERSION = "1.0.0";
    public static final String APP_NAME = "Payanam";

    public static void main(String[] args) {
        System.out.println("\n\tWelcome to " + APP_NAME + " - Version: " + APP_VERSION);
        System.out.println("--------------------------------------------");
        showLandingMenu();
    }
    private static void showLandingMenu(){
        Scanner scanner= ConsoleInput.getScanner();
        while (true) {
            System.out.println();
            System.out.println("1. Sign Up");
            System.out.println("2. Sign In");
            System.out.println("3. Exit");
            System.out.print("Choose an option: ");
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    new SignUpView().init();
                    break;
                case "2":
                    new SignInView().init();
                    break;
                case "3":
                    System.out.println("Thank you for using Payanam");
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }
}
