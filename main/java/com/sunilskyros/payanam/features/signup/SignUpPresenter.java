package com.sunilskyros.payanam.features.signup;

import com.sunilskyros.payanam.data.dto.Passenger;
import com.sunilskyros.payanam.util.InputAndValidation;
import com.sunilskyros.payanam.util.PasswordUtil;

public class SignUpPresenter {
    private final SignUpView signUpView;
    private final SignUpModel signUpModel;
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
        return InputAndValidation.validateName(trimmed);
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
        return InputAndValidation.validatePassWord(password);
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
     * @param phoneNumber The input string for the phone number.
     * @return An error message if invalid, or null if valid.
     */
    String validatePhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            return "Mobile number cannot be empty";
        }
        return InputAndValidation.validatePhoneNumber(phoneNumber);
    }
}
