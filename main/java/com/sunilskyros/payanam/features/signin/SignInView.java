package com.sunilskyros.payanam.features.signin;

import com.sunilskyros.payanam.data.dto.LoginRequest;
import com.sunilskyros.payanam.data.dto.Passenger;
import com.sunilskyros.payanam.features.homepage.HomeView;
import com.sunilskyros.payanam.util.InputAndValidation;

public class SignInView {
        private final SignInPresenter signInPresenter;
        private boolean authenticated;

    public SignInView() {
        this.signInPresenter = new SignInPresenter(this);
        this.authenticated=false;
    }
    public void init() {
        System.out.println();
        System.out.println("\n\tContinue your Payanam");
        while (!authenticated) {
            promptAndAuthenticate();
            if (authenticated)return;
        }
    }
    private void promptAndAuthenticate() {
        String phoneNumber = InputAndValidation.getStringInput("Enter your Phone Number : ");
        String password = InputAndValidation.getStringInput("Enter  your password : ");
        LoginRequest request = new LoginRequest();
        request.setPhoneNumber(phoneNumber.trim());
        request.setPassword(password);

        signInPresenter.authenticate(request);
    }
    void onSignInFailed(String message) {
        System.out.println(message);
    }
    void onSignInSuccessful(Passenger passenger) {
        authenticated=true;
        System.out.println("\n\tWelcome, " + passenger.getName());
        new HomeView(passenger).init();
    }
}
