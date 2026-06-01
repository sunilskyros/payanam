package com.sunilskyros.payanam.features.homepage;

import com.sunilskyros.payanam.data.dto.Bus;
import com.sunilskyros.payanam.data.dto.Passenger;
import com.sunilskyros.payanam.data.dto.Stop;
import com.sunilskyros.payanam.data.dto.Ticket;
import com.sunilskyros.payanam.data.repository.BusRepository;
import com.sunilskyros.payanam.data.repository.PassengerRepository;
import com.sunilskyros.payanam.data.repository.StopRepository;
import com.sunilskyros.payanam.data.repository.TicketRepository;
import com.sunilskyros.payanam.util.PasswordUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class HomeModel {

    private final PassengerRepository passengerRepository;
    private final BusRepository busRepository;
    private final StopRepository stopRepository;
    private final TicketRepository ticketRepository;

    private static final int BASE_PRICE_PER_STOP = 10;
    private static final int TICKET_VALIDITY_HOURS = 4;

    @Autowired
    public HomeModel(PassengerRepository passengerRepository, BusRepository busRepository,
                     StopRepository stopRepository, TicketRepository ticketRepository) {
        this.passengerRepository = passengerRepository;
        this.busRepository = busRepository;
        this.stopRepository = stopRepository;
        this.ticketRepository = ticketRepository;
    }

    // ==================== Bus Operations ====================

    /**
     * Retrieves a specific bus by its ID from the database.
     * @param busNumber The unique ID of the bus.
     * @return Bus object if found, otherwise null.
     */
    public Bus getBusByNumber(int busNumber) {
        Bus bus = busRepository.findById(busNumber).orElse(null);
        if (bus != null) {
            bus.setStops(stopRepository.findByBusIdOrderByIdAsc(busNumber));
        }
        return bus;
    }

    /**
     * Retrieves a map of all available buses from the database.
     * @return Map containing Bus ID as key and Bus object as value.
     */
    public Map<Integer, Bus> getBusList() {
        List<Bus> buses = busRepository.findAll();
        Map<Integer, Bus> busMap = new HashMap<>();
        for (Bus bus : buses) {
            busMap.put(bus.getId(), bus);
        }
        return busMap;
    }

    /**
     * Adds a new bus to the database.
     * @param bus The Bus object to be added.
     */
    public void addBus(Bus bus) {
        busRepository.save(bus);
    }

    /**
     * Removes an existing bus from the database based on its ID.
     * @param busId The ID of the bus to remove.
     */
    public void removeBus(int busId) {
        busRepository.deleteById(busId);
    }

    /**
     * Updates the stops associated with a specific bus.
     * Clears old stops and inserts the new stops provided in the Bus object.
     * @param bus The Bus object containing the updated list of stops.
     */
    @Transactional
    public void updateBusStops(Bus bus) {
        if (bus == null) return;
        stopRepository.deleteByBusId(bus.getId());
        if (bus.getStops() != null && !bus.getStops().isEmpty()) {
            for (Stop stop : bus.getStops()) {
                stop.setBusId(bus.getId());
            }
            stopRepository.saveAll(bus.getStops());
        }
    }

    // ==================== Search Operations ====================

    /**
     * Searches for all buses that pass through a specific stop name.
     * @param stopName The name of the stop to search for.
     * @return A list of buses that include the specified stop in their route.
     */
    public List<Bus> searchBusesByStop(String stopName) {
        List<Stop> stops = stopRepository.findByStopNameContainingIgnoreCase(stopName);
        List<Bus> result = new ArrayList<>();
        Map<Integer, Bus> added = new HashMap<>();
        for (Stop s : stops) {
            if (!added.containsKey(s.getBusId())) {
                Bus bus = busRepository.findById(s.getBusId()).orElse(null);
                if (bus != null) {
                    result.add(bus);
                    added.put(bus.getId(), bus);
                }
            }
        }
        return result;
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

        int numStops = Math.abs(endIdx - startIdx);
        double calculatedPrice = numStops * 1.5;
        int price = (int) Math.round(calculatedPrice);
        if (price < 5) {
            price = 5;
        } else if (price > 25) {
            price = 25;
        }

        Ticket ticket = new Ticket();
        ticket.setPassengerPhoneNumber(passenger.getPhoneNumber());
        ticket.setBusId(busNumber);
        ticket.setBusName(bus.getName());
        ticket.setSourceStop(sourceStop);
        ticket.setDestinationStop(destinationStop);
        ticket.setPrice(price);
        ticket.setBoughtTime(LocalDateTime.now());
        ticket.setValidUntil(LocalDateTime.now().plusHours(TICKET_VALIDITY_HOURS));
        ticket.setIsValid(Boolean.TRUE);

        // Generate dynamic unique booking reference
        String bookingRef = "PYM-" + String.format("%06d", (int)(Math.random() * 1000000));
        ticket.setBookingReference(bookingRef);

        return ticketRepository.save(ticket);
    }

    /**
     * Searches for the stop sequence index for a specific stop name in a bus route.
     * Helper method for ticket price calculation.
     * @param bus The bus object containing the stops route.
     * @param stopName The stop name to look for.
     * @return The integer sequence index of the stop (1-based), or 0 if not found.
     */
    private int getStopIndex(Bus bus, String stopName) {
        if (bus == null || bus.getStops() == null) {
            return 0;
        }
        for (Stop stop : bus.getStops()) {
            if (stop.getStopName().equalsIgnoreCase(stopName)) {
                return stop.getId();
            }
        }
        return 0;
    }

    /**
     * Retrieves all booked tickets of a specific passenger from the database.
     * @param phoneNumber The phone number of the passenger.
     * @return A list of Ticket objects belonging to the passenger.
     */
    public List<Ticket> getPassengerTickets(String phoneNumber) {
        return ticketRepository.findByPassengerPhoneNumberOrderByTicketIdDesc(phoneNumber);
    }

    // ==================== Passenger Operations ====================

    /**
     * Removes an existing passenger from the database.
     * @param passenger The passenger to be removed.
     */
    public void removePassenger(Passenger passenger) {
        passengerRepository.delete(passenger);
    }

    /**
     * Retrieves a passenger by phone number.
     * @param phoneNumber The phone number to query.
     * @return The Passenger object if found, otherwise null.
     */
    public Passenger getPassengerByPhone(String phoneNumber) {
        return passengerRepository.findById(phoneNumber).orElse(null);
    }

    /**
     * Adds a new Ticket Collector (User with TICKETCOLLECTOR role) to the database.
     * Handles password hashing and default profile generation.
     * @param name The name of the ticket collector.
     * @param phone The unique phone number.
     * @param password The raw password to hash.
     * @return The created Passenger object, or null on database failure.
     */
    public Passenger addTicketCollector(String name, String phone, String password) {
        if (passengerRepository.existsById(phone)) {
            return null;
        }
        Passenger ticketCollector = new Passenger();
        ticketCollector.setName(name);
        ticketCollector.setPhoneNumber(phone);
        ticketCollector.setPassword(PasswordUtil.hash(password));
        ticketCollector.setRole(Passenger.Role.TICKETCOLLECTOR);
        ticketCollector.setStatus(Passenger.Status.ACTIVE);

        return passengerRepository.save(ticketCollector);
    }

    /**
     * Retrieves all registered passengers from the database.
     * @return List of all registered Passenger objects.
     */
    public List<Passenger> getAllPassengers() {
        return passengerRepository.findAll();
    }

    /**
     * Retrieves all booked tickets from the database.
     * @return List of all booked Ticket objects.
     */
    public List<Ticket> getAllTickets() {
        return ticketRepository.findAllByOrderByTicketIdDesc();
    }

    /**
     * Retrieves all buses along with their stops from the database.
     * @return List of all populated Bus objects.
     */
    public List<Bus> getAllBusesWithStops() {
        List<Bus> buses = busRepository.findAll();
        for (Bus bus : buses) {
            bus.setStops(stopRepository.findByBusIdOrderByIdAsc(bus.getId()));
        }
        return buses;
    }

    public void addStop(Stop stop) {
        stopRepository.save(stop);
    }
}
