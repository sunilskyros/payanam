package com.sunilskyros.payanam.data.repository;

import com.sunilskyros.payanam.data.dto.Bus;
import com.sunilskyros.payanam.data.dto.Passenger;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PayanamDB {
    private static PayanamDB payanamDB=null;

    private PayanamDB(){}

    public static PayanamDB getInstance(){
        if(payanamDB == null){
            payanamDB = new PayanamDB();
        }
        return payanamDB;
    }

    private final List<Passenger> passengers=new ArrayList<>();
    private final List<Bus> buses = new ArrayList<>();

    public Passenger addPassenger(Passenger passenger) {
        if (passenger == null || passenger.getPhoneNumber()== null) return null;

        if (passenger.getStatus() == null) {
            passenger.setStatus(Passenger.Status.ACTIVE);
        }
        if (passenger.getRole() == null) {
            passenger.setRole(Passenger.Role.PASSENGER);
        }
        passengers.add(passenger);
        return passenger;
    }

    public Passenger authenticateEmployee(String phoneNumber, String password) {
        Passenger passenger = getPassengerByPhone(phoneNumber);
        if (passenger == null) return null;
        if (password == null || !password.equals(passenger.getPassword())) return null;
        return passenger;
    }
    public Passenger getPassengerByPhone(String phoneNumber){
        if (phoneNumber == null) return null;
        for (Passenger current : passengers) {
            if (phoneNumber.equals(current.getPhoneNumber())) return current;
        }
        return null;
    }
}
