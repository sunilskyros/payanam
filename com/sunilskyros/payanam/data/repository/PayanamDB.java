package com.sunilskyros.payanam.data.repository;

import com.sunilskyros.payanam.data.dto.Bus;
import com.sunilskyros.payanam.data.dto.Passenger;
import com.sunilskyros.payanam.data.dto.Stop;
import com.sunilskyros.payanam.data.dto.Ticket;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PayanamDB {
    private static PayanamDB payanamDB=null;

    private PayanamDB(){}

    public static PayanamDB getInstance(){
        if(payanamDB == null){
            payanamDB = new PayanamDB();
        }
        return payanamDB;
    }

    private static final Map<String,Passenger> PASSENGER_LIST = new HashMap<>();
    private static final Map<Integer,Bus> BUS_LIST = new HashMap<>();
    private static final Map<String, List<Ticket>> TICKET_LIST_BY_PASSENGER = new HashMap<>();
    private static int ticketIdCounter = 1000;

    public Passenger addPassenger(Passenger passenger) {
        if (passenger == null || passenger.getPhoneNumber()== null) return null;

        if (passenger.getStatus() == null) {
            passenger.setStatus(Passenger.Status.ACTIVE);
        }
        if (passenger.getRole() == null) {
            passenger.setRole(Passenger.Role.PASSENGER);
        }
        Passenger existingPassenger = getPassengerByPhone(passenger.getPhoneNumber());
        if(existingPassenger == null) {
            PASSENGER_LIST.put(passenger.getPhoneNumber(),passenger);
            return passenger;
        }
        return null;
    }

    public Passenger authenticatePassenger(String phoneNumber, String password) {
        Passenger passenger = getPassengerByPhone(phoneNumber);
        if (passenger == null) return null;
        if (password == null || !password.equals(passenger.getPassword())) return null;
        return passenger;
    }
    public Passenger getPassengerByPhone(String phoneNumber){
        if (phoneNumber == null) return null;
        if(PASSENGER_LIST.containsKey(phoneNumber)){
            return PASSENGER_LIST.get(phoneNumber);
        }
        return null;
    }

    public Map<Integer,Bus> getBusList() {
        return BUS_LIST;
    }

    public Bus getBusById(int busId) {
        return BUS_LIST.get(busId);
    }

    public Ticket addTicket(Ticket ticket) {
        if (ticket == null || ticket.getPassengerPhoneNumber() == null) return null;
        ticket.setTicketId(++ticketIdCounter);
        List<Ticket> tickets = TICKET_LIST_BY_PASSENGER.computeIfAbsent(
                ticket.getPassengerPhoneNumber(), key -> new ArrayList<>());
        tickets.add(ticket);
        return ticket;
    }

    public List<Ticket> getTicketsByPassenger(String phoneNumber) {
        if (phoneNumber == null) return new ArrayList<>();
        List<Ticket> tickets = TICKET_LIST_BY_PASSENGER.get(phoneNumber);
        if (tickets == null) return new ArrayList<>();
        return new ArrayList<>(tickets);
    }
}
