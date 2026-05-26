package com.sunilskyros.payanam.features.signup;

import com.sunilskyros.payanam.data.dto.Passenger;
import com.sunilskyros.payanam.data.repository.PassengerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SignUpModel {

    private final PassengerRepository passengerRepository;

    @Autowired
    public SignUpModel(PassengerRepository passengerRepository) {
        this.passengerRepository = passengerRepository;
    }

    /**
     * Persists a newly registered passenger into the database.
     * @param passenger The passenger object to be saved.
     * @return The saved Passenger object, or null if database insertion fails.
     */
    public Passenger registerPassenger(Passenger passenger) {
        if (passenger == null || passenger.getPhoneNumber() == null) return null;
        if (passengerRepository.existsById(passenger.getPhoneNumber())) {
            return null;
        }
        if (passenger.getStatus() == null) {
            passenger.setStatus(Passenger.Status.ACTIVE);
        }
        if (passenger.getRole() == null) {
            passenger.setRole(Passenger.Role.PASSENGER);
        }
        return passengerRepository.save(passenger);
    }
}
