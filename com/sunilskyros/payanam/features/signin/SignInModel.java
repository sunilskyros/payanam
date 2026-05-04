package com.sunilskyros.payanam.features.signin;

import com.sunilskyros.payanam.data.dto.Passenger;
import com.sunilskyros.payanam.data.repository.PayanamDB;

class SignInModel {
    
    Passenger authenticate(String phoneNumber, String password) {
        return PayanamDB.getInstance().authenticatePassenger(phoneNumber, password);
    }
}
