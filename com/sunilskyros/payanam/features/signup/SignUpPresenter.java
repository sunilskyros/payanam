package com.sunilskyros.payanam.features.signup;

import com.sunilskyros.payanam.data.dto.Passenger;
import com.sunilskyros.payanam.util.PasswordUtil;

import java.util.regex.Pattern;

public class SignUpPresenter {
    private final SignUpView signUpView;
    private final SignUpModel signUpModel;

    private static final Pattern MOBILE_PATTERN = Pattern.compile("^[6-9]\\d {9}$");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d). {8, }$");
    private static final int MIN_NAME_LENGTH = 3;
    private static final int MAX_NAME_LENGTH = 50;

    public SignUpPresenter(SignUpView signUpView) {
        this.signUpView = signUpView;
        this.signUpModel = new SignUpModel();
    }

    /**
     * Registers a new passenger in the system.
     * Hashes the password and delegates database saving to the model.
     * Instructs the view on success or failure.
     * @param name The full name of the passenger.
     * @param phoneNumber The mobile phone number.
     * @param password The plain text password.
     */
    void registerPassenger(String name, String phoneNumber, String password) {
        Passenger passenger = new Passenger();
        passenger.setName(name);
        passenger.setPhoneNumber(phoneNumber);
        passenger.setRole(Passenger.Role.PASSENGER);
        passenger.setPassword(PasswordUtil.hash(password));
        passenger.setStatus(Passenger.Status.ACTIVE);

        Passenger saved = signUpModel.registerPassenger(passenger);
        if (saved == null) {
            signUpView.showErrorMessage("Could not create account. Please try again.");
            return;
        }
        signUpView.onSignUpSuccessful();
    }

    /**
     * Validates the passenger's name.
     * @param name The input string for the name.
     * @return An error message if invalid, or null if valid.
     */
    String validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return "Name cannot be empty";
        }
        String trimmed = name.trim();
        if (trimmed.length() < MIN_NAME_LENGTH || trimmed.length() > MAX_NAME_LENGTH) {
            return "Name must be between " + MIN_NAME_LENGTH + " and " + MAX_NAME_LENGTH + " characters";
        }
        return null;
    }

    /**
     * Validates the password strength.
     * Requires at least 8 characters, containing both letters and numbers.
     * @param password The input string for the password.
     * @return An error message if invalid, or null if valid.
     */
    String validatePassword(String password) {
        if (password == null || password.isEmpty()) {
            return "Password cannot be empty";
        }
        if (!PASSWORD_PATTERN.matcher(password).matches()) {
            return "Password must be at least 8 characters and contain letters and numbers";
        }
        return null;
    }

    /**
     * Checks if the confirmation password matches the original password.
     * @param password The original password.
     * @param confirmPassword The confirmation password.
     * @return An error message if they don't match, or null if valid.
     */
    String validateConfirmPassword(String password, String confirmPassword) {
        if (confirmPassword == null || !confirmPassword.equals(password)) {
            return "Passwords do not match";
        }
        return null;
    }

    /**
     * Validates the mobile phone number format.
     * Requires exactly 10 digits starting with 6, 7, 8, or 9.
     * @param mobile The input string for the phone number.
     * @return An error message if invalid, or null if valid.
     */
    String validatePhoneNumber(String mobile) {
        if (mobile == null || mobile.trim().isEmpty()) {
            return "Mobile number cannot be empty";
        }
        if (!MOBILE_PATTERN.matcher(mobile.trim()).matches()) {
            return "Enter a valid 10 digit mobile number";
        }
        return null;
    }
}
