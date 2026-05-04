package com.sunilskyros.payanam.features.signup;

import com.sunilskyros.payanam.data.dto.Passenger;
import com.sunilskyros.payanam.data.repository.PayanamDB;

class SignUpModel {
    
    Passenger registerPassenger(Passenger passenger){
        return PayanamDB.getInstance().addPassenger(passenger);
    }
}
