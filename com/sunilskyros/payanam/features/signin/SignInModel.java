package com.sunilskyros.payanam.features.signin;

import com.sunilskyros.payanam.data.dto.LoginRequest;
import com.sunilskyros.payanam.data.dto.Passenger;
import com.sunilskyros.payanam.data.repository.PayanamDB;

import java.util.regex.Pattern;

class SignInModel {
    private final SignInView signInView;
    private static final Pattern MOBILE_PATTERN = Pattern.compile("^[6-9]\\d{9}$");

    public SignInModel(SignInView signInView) {
        this.signInView=signInView;
    }

    void authenticate(LoginRequest request) {
        if (request == null) {
            signInView.onSignInFailed("Invalid Phone Number or password");
            return;
        }
        String phoneError = validatePhoneNumber(request.getPhoneNumber());
        if (phoneError != null) {
            signInView.onSignInFailed(phoneError);
            return;
        }
        String passwordError = validatePassword(request.getPassword());
        if (passwordError != null) {
            signInView.onSignInFailed(passwordError);
            return;
        }

        Passenger passenger = PayanamDB.getInstance().authenticateEmployee(
                request.getPhoneNumber(), request.getPassword());
        if (passenger == null) {
            signInView.onSignInFailed("Invalid Phone number or password");
            return;
        }
        if (passenger.getStatus() == Passenger.Status.IN_ACTIVE) {
            signInView.onSignInFailed("Your account is not active. Contact your administrator.");
            return;
        }
        signInView.onSignInSuccessful(passenger);
    }

    String validatePassword(String password) {
        if (password == null || password.isEmpty()) {
            return "Password cannot be empty";
        }
        return null;
    }
    String validatePhoneNumber(String phoneNumber){
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            return "Email cannot be empty";
        }
        if (!MOBILE_PATTERN.matcher(phoneNumber.trim()).matches()) {
            return "Enter a valid Phone number :";
        }
        return null;
    }

}
