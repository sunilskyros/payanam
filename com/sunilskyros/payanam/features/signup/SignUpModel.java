package com.sunilskyros.payanam.features.signup;

import com.sunilskyros.payanam.data.dto.Passenger;
import com.sunilskyros.payanam.data.repository.PayanamDB;

class SignUpModel {

    /**
     * Persists a newly registered passenger into the database.
     * @param passenger The passenger object to be saved.
     * @return The saved Passenger object, or null if database insertion fails.
     */
    Passenger registerPassenger(Passenger passenger) {
        return PayanamDB.getInstance().addPassenger(passenger);
    }
}
