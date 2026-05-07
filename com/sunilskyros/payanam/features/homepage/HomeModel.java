package com.sunilskyros.payanam.features.homepage;

import com.sunilskyros.payanam.data.dto.Bus;
import com.sunilskyros.payanam.data.dto.Passenger;
import com.sunilskyros.payanam.data.dto.Stop;
import com.sunilskyros.payanam.data.dto.Ticket;
import com.sunilskyros.payanam.data.repository.PayanamDB;
import com.sunilskyros.payanam.util.PasswordUtil;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class HomeModel {

    private static final int BASE_PRICE_PER_STOP = 10;
    private static final int TICKET_VALIDITY_HOURS = 4;

    // ==================== Bus Operations ====================

    /**
     * Retrieves a specific bus by its ID from the database.
     * @param busNumber The unique ID of the bus.
     * @return Bus object if found, otherwise null.
     */
    public Bus getBusByNumber(int busNumber) {
        return PayanamDB.getInstance().getBusById(busNumber);
    }

    /**
     * Retrieves a map of all available buses from the database.
     * @return Map containing Bus ID as key and Bus object as value.
     */
    public Map<Integer, Bus> getBusList() {
        return PayanamDB.getInstance().getBusList();
    }

    /**
     * Adds a new bus to the database.
     * @param bus The Bus object to be added.
     */
    public void addBus(Bus bus) {
        PayanamDB.getInstance().addBus(bus);
    }

    /**
     * Removes an existing bus from the database based on its ID.
     * @param busId The ID of the bus to remove.
     */
    public void removeBus(int busId) {
        PayanamDB.getInstance().removeBus(busId);
    }

    /**
     * Updates the stops associated with a specific bus.
     * Clears old stops and inserts the new stops provided in the Bus object.
     * @param bus The Bus object containing the updated list of stops.
     */
    public void updateBusStops(Bus bus) {
        PayanamDB.getInstance().updateBusStops(bus);
    }

    // ==================== Search Operations ====================

    /**
     * Searches for all buses that pass through a specific stop name.
     * @param stopName The name of the stop to search for.
     * @return A list of buses that include the specified stop in their route.
     */
    public List<Bus> searchBusesByStop(String stopName) {
        return PayanamDB.getInstance().searchBusesByStop(stopName);
    }

    // ==================== Ticket Business Logic ====================

    /**
     * Creates and persists a ticket for a passenger with calculated price and validity period.
     * Applies business rules to ensure valid stops and calculates ticket expiration.
     * @param passenger The passenger booking the ticket.
     * @param busNumber The ID of the bus.
     * @param sourceStop The starting stop name.
     * @param destinationStop The destination stop name.
     * @return The created Ticket object, or null if validation fails.
     */
    public Ticket createTicket(Passenger passenger, int busNumber, String sourceStop, String destinationStop) {
        if (passenger == null || passenger.getPhoneNumber() == null) {
            return null;
        }

        Bus bus = getBusByNumber(busNumber);
        if (bus == null) {
            return null;
        }

        int startIdx = getStopIndex(bus, sourceStop);
        int endIdx = getStopIndex(bus, destinationStop);
        if (startIdx == 0 || endIdx == 0) {
            return null;
        }

        Ticket ticket = new Ticket();
        ticket.setPassengerPhoneNumber(passenger.getPhoneNumber());
        ticket.setBusId(bus.getId());
        ticket.setBusName(bus.getName());
        ticket.setSourceStop(sourceStop);
        ticket.setDestinationStop(destinationStop);
        ticket.setPrice(calculatePrice(startIdx, endIdx));
        ticket.setBoughtTime(LocalDateTime.now());
        ticket.setValidUntil(LocalDateTime.now().plusHours(TICKET_VALIDITY_HOURS));
        ticket.setIsValid(Boolean.TRUE);

        return PayanamDB.getInstance().addTicket(ticket);
    }

    /**
     * Calculates the ticket price based on the number of stops between source and destination.
     * @param startIdx The sequence index of the source stop.
     * @param endIdx The sequence index of the destination stop.
     * @return The calculated price in integers.
     */
    public int calculatePrice(int startIdx, int endIdx) {
        return BASE_PRICE_PER_STOP * Math.abs(startIdx - endIdx);
    }

    /**
     * Returns the 1-based index of a stop name in a bus's route.
     * Used for validating routes and calculating distances.
     * @param bus The Bus object containing the stops.
     * @param stopName The name of the stop to find.
     * @return The 1-based index of the stop, or 0 if the stop is not found.
     */
    public int getStopIndex(Bus bus, String stopName) {
        List<Stop> stops = bus.getStops();
        for (int i = 0; i < stops.size(); i++) {
            if (stops.get(i).getStopName().equalsIgnoreCase(stopName)) {
                return i + 1;
            }
        }
        return 0;
    }

    /**
     * Retrieves all tickets booked by a specific passenger.
     * @param phoneNumber The phone number of the passenger.
     * @return A list of Ticket objects belonging to the passenger.
     */
    public List<Ticket> getPassengerTickets(String phoneNumber) {
        return PayanamDB.getInstance().getTicketsByPassenger(phoneNumber);
    }

    // ==================== Passenger Operations ====================

    /**
     * Removes a passenger or ticket collector from the system.
     * @param passenger The passenger to be removed.
     */
    public void removePassenger(Passenger passenger) {
        PayanamDB.getInstance().removeUser(passenger);
    }

    /**
     * Retrieves a passenger's details using their phone number.
     * @param phoneNumber The phone number to search for.
     * @return The Passenger object if found, otherwise null.
     */
    public Passenger getPassengerByPhone(String phoneNumber) {
        return PayanamDB.getInstance().getPassengerByPhone(phoneNumber);
    }

    /**
     * Creates and persists a new Ticket Collector account.
     * Applies business rules to securely hash the password before saving.
     * @param name The name of the ticket collector.
     * @param phoneNumber The contact number of the ticket collector.
     * @param password The plain text password.
     * @return The created Passenger object representing the ticket collector, or null if name is empty.
     */
    public Passenger addTicketCollector(String name, String phoneNumber, String password) {
        if (name == null || name.trim().isEmpty()) {
            return null;
        }

        Passenger ticketCollector = new Passenger();
        ticketCollector.setName(name.trim());
        ticketCollector.setPhoneNumber(phoneNumber);
        ticketCollector.setPassword(PasswordUtil.hash(password));
        ticketCollector.setRole(Passenger.Role.TICKETCOLLECTOR);
        ticketCollector.setStatus(Passenger.Status.ACTIVE);

        return PayanamDB.getInstance().addPassenger(ticketCollector);
    }
}
