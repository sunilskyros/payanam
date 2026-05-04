package com.sunilskyros.payanam.features.homepage;

import com.sunilskyros.payanam.data.dto.Bus;
import com.sunilskyros.payanam.data.dto.Ticket;
import com.sunilskyros.payanam.data.repository.PayanamDB;

import java.util.List;
import java.util.Map;

public class HomeModel {

    public Bus getBusByNumber(int busNumber) {
        return PayanamDB.getInstance().getBusById(busNumber);
    }

    public Map<Integer, Bus> getBusList() {
        return PayanamDB.getInstance().getBusList();
    }

    public void removeBus(int busId) {
        PayanamDB.getInstance().removeBus(busId);
    }

    public Ticket addTicket(Ticket ticket) {
        return PayanamDB.getInstance().addTicket(ticket);
    }

    public List<Ticket> getPassengerTickets(String phoneNumber) {
        return PayanamDB.getInstance().getTicketsByPassenger(phoneNumber);
    }

    public void addBus(Bus bus) {
        PayanamDB.getInstance().addBus(bus);
    }

    public void updateBusStops(Bus bus) {
        PayanamDB.getInstance().updateBusStops(bus);
    }
}