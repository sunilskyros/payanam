package com.sunilskyros.payanam.features.signup;

import com.sunilskyros.payanam.features.signin.SignInView;
import com.sunilskyros.payanam.util.InputAndValidation;

public class SignUpView {
    private final SignUpPresenter signUpPresenter;

    public SignUpView() {
        this.signUpPresenter = new SignUpPresenter(this);
    }

    public void init() {
        signup();
    }

    private void signup() {
        System.out.println();
        System.out.println("""
                           Your PAYANAM starts here...
                           ------------------------------""");
        String name=promptName();
        String phoneNumber=promptPhoneNumber();
        String password=promptPassword();
        signUpPresenter.registerPassenger(name, phoneNumber, password);
    }
    private String promptPassword() {
        while (true) {
            String input = InputAndValidation.getStringInput("Enter password (minimum 8 characters with letters and numbers): ");
            String error = signUpPresenter.validatePassword(input);
            if (error != null) {
                showErrorMessage(error);
                continue;
            }
            String confirm = InputAndValidation.getStringInput("Confirm password : ");
            String confirmError = signUpPresenter.validateConfirmPassword(input, confirm);
            if (confirmError != null) {
                showErrorMessage(confirmError);
                continue;
            }
            return input;
        }
    }

    private String promptName() {
        while (true) {
            String input = InputAndValidation.getStringInput("Enter your full name : ");
            String error = signUpPresenter.validateName(input);
            if (error == null) return input.trim();
            showErrorMessage(error);
        }
    }
    private String promptPhoneNumber() {
        while (true) {
            String input = InputAndValidation.getStringInput("Enter your Phone Number : ");
            String error= signUpPresenter.validatePhoneNumber(input);
            if (error == null )return input.trim();
            showErrorMessage(error);
        }
    }
    void onSignUpSuccessful() {
        System.out.println("""
                           Account created successfully.
                           Please sign in to continue.""");
        new SignInView().init();
    }
    void showErrorMessage(String errorMsg) {
        System.out.println(errorMsg);
    }
}
