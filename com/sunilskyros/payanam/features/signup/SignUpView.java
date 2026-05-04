package com.sunilskyros.payanam.features.signup;

import com.sunilskyros.payanam.data.dto.Passenger;
import com.sunilskyros.payanam.features.signin.SignInView;
import com.sunilskyros.payanam.util.ConsoleInput;

import java.util.Scanner;

public class SignUpView {
    private final SignUpPresenter signUpPresenter;
    private final Scanner scanner;

    public SignUpView(){
        this.signUpPresenter = new SignUpPresenter(this);
        this.scanner=ConsoleInput.getScanner();
    }

    public void init(){
        signup();
    }

    private void signup(){
        System.out.println();
        System.out.println("\n\tYour PAYANAM starts here...");
        System.out.println("----------------------------------");
        String name=promptName();
        String phoneNumber=promptPhoneNumber();
        Passenger.Role role=promptRole();
        String password=promptPassword();
        signUpPresenter.registerPassenger(name,phoneNumber,role,password);
    }

    private String promptPassword() {
        while (true) {
            System.out.print("Enter password (minimum 8 characters with letters and numbers): ");
            String input = scanner.nextLine();
            String error = signUpPresenter.validatePassword(input);
            if (error != null) {
                showErrorMessage(error);
                continue;
            }
            System.out.print("Confirm password: ");
            String confirm = scanner.nextLine();
            String confirmError = signUpPresenter.validateConfirmPassword(input, confirm);
            if (confirmError != null) {
                showErrorMessage(confirmError);
                continue;
            }
            return input;
        }
    }

    private String promptName(){
        while (true) {
            System.out.print("Enter your full name: ");
            String input = scanner.nextLine();
            String error = signUpPresenter.validateName(input);
            if (error == null) return input.trim();
            showErrorMessage(error);
        }
    }
    private Passenger.Role promptRole(){
        while(true){
            System.out.println("Select Your Role :");
            System.out.println("1.Passenger");
            System.out.println("2.Ticket Collector");
            System.out.println("3.Admin");
            System.out.print("Choose your option : ");
            String input= scanner.nextLine();
            if(input.trim().equals("1")){
                return Passenger.Role.PASSENGER;
            }
            else if(input.trim().equals("2")){
                return Passenger.Role.TICKETCOLLECTOR;
            }
            else if(input.trim().equals("3")){
                return Passenger.Role.ADMIN;
            }
            else{
                showErrorMessage("Select a valid Role.");
            }
        }
    }
    private String promptPhoneNumber(){
        while (true){
            System.out.print("Enter your Phone number : ");
            String input = scanner.nextLine();
            String error= signUpPresenter.validatePhoneNumber(input);
            if(error == null )return input.trim();
            showErrorMessage(error);
        }
    }
    void onSignUpSuccessful() {
        System.out.println();
        System.out.println("Account created successfully.");
        System.out.println("\nPlease sign in to continue.");
        new SignInView().init();
    }
    void showErrorMessage(String errorMsg){
        System.out.println(errorMsg);
    }
}