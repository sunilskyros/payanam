package com.sunilskyros.payanam.features.signin;

import com.sunilskyros.payanam.data.dto.Passenger;
import com.sunilskyros.payanam.data.repository.PayanamDB;

class SignInModel {

    /**
     * Authenticates a passenger against the database.
     * @param phoneNumber The passenger's phone number.
     * @param password The plain text password to verify.
     * @return The authenticated Passenger object, or null if authentication fails.
     */
    Passenger authenticate(String phoneNumber, String password) {
        return PayanamDB.getInstance().authenticatePassenger(phoneNumber, password);
    }
}
