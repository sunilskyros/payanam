package com.sunilskyros.payanam.features.signin;

import com.sunilskyros.payanam.data.dto.Passenger;
import com.sunilskyros.payanam.data.repository.PassengerRepository;
import com.sunilskyros.payanam.util.PasswordUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SignInModel {

    private final PassengerRepository passengerRepository;

    @Autowired
    public SignInModel(PassengerRepository passengerRepository) {
        this.passengerRepository = passengerRepository;
    }

    /**
     * Authenticates a passenger against the database.
     * @param phoneNumber The passenger's phone number.
     * @param password The plain text password to verify.
     * @return The authenticated Passenger object, or null if authentication fails.
     */
    public Passenger authenticate(String phoneNumber, String password) {
        return passengerRepository.findById(phoneNumber)
                .filter(p -> PasswordUtil.verify(password, p.getPassword()))
                .orElse(null);
    }
}
