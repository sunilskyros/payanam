package com.sunilskyros.payanam.features.signin;

import com.sunilskyros.payanam.data.dto.LoginRequest;
import com.sunilskyros.payanam.data.dto.Passenger;
import com.sunilskyros.payanam.features.homepage.HomeView;
import com.sunilskyros.payanam.util.ConsoleInput;

import java.util.Scanner;

public class SignInView {
        private final SignInModel signInModel;
        private final Scanner scanner;
        private boolean authenticated;


    public SignInView() {
        this.signInModel = new SignInModel(this);
        this.scanner = ConsoleInput.getScanner();
        this.authenticated=false;
    }
    public void init(){
        System.out.println();
        System.out.println("Continue your Payanam ");
        while (!authenticated){
            promptAndAuthenticate();
            if(authenticated)return;

        }
    }
    private void promptAndAuthenticate() {
        System.out.print("Enter your Phone Number : ");
        String phoneNumber = scanner.nextLine();
        System.out.print("Enter your password: ");
        String password = scanner.nextLine();

        LoginRequest request = new LoginRequest();
        request.setPhoneNumber(phoneNumber.trim());
        request.setPassword(password.trim());

        signInModel.authenticate(request);
    }
    void onSignInFailed(String message){
        System.out.println(message);
    }
    void onSignInSuccessful(Passenger passenger){
        authenticated = true;
        System.out.println("Welcome, " + passenger.getName());
        new HomeView(passenger).init();
    }
}
