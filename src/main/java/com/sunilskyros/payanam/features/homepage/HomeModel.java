package com.sunilskyros.payanam.features.homepage;

import com.sunilskyros.payanam.data.dto.Bus;
import com.sunilskyros.payanam.data.dto.Passenger;
import com.sunilskyros.payanam.data.dto.Stop;
import com.sunilskyros.payanam.data.dto.Ticket;
import com.sunilskyros.payanam.data.repository.BusRepository;
import com.sunilskyros.payanam.data.repository.PassengerRepository;
import com.sunilskyros.payanam.data.repository.StopRepository;
import com.sunilskyros.payanam.data.repository.TicketRepository;
import com.sunilskyros.payanam.data.repository.BusLiveLocationRepository;
import com.sunilskyros.payanam.data.repository.TravelHistoryRepository;
import com.sunilskyros.payanam.data.dto.BusLiveLocation;
import com.sunilskyros.payanam.data.dto.TravelHistory;
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
    private final BusLiveLocationRepository busLiveLocationRepository;
    private final TravelHistoryRepository travelHistoryRepository;

    private static final int BASE_PRICE_PER_STOP = 10;
    private static final int TICKET_VALIDITY_HOURS = 4;

    @Autowired
    public HomeModel(PassengerRepository passengerRepository, BusRepository busRepository,
                     StopRepository stopRepository, TicketRepository ticketRepository,
                     BusLiveLocationRepository busLiveLocationRepository,
                     TravelHistoryRepository travelHistoryRepository) {
        this.passengerRepository = passengerRepository;
        this.busRepository = busRepository;
        this.stopRepository = stopRepository;
        this.ticketRepository = ticketRepository;
        this.busLiveLocationRepository = busLiveLocationRepository;
        this.travelHistoryRepository = travelHistoryRepository;
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

        Stop source = null;
        Stop dest = null;
        if (bus.getStops() != null) {
            for (Stop stop : bus.getStops()) {
                if (stop.getStopName().equalsIgnoreCase(sourceStop)) {
                    source = stop;
                }
                if (stop.getStopName().equalsIgnoreCase(destinationStop)) {
                    dest = stop;
                }
            }
        }

        double distance = 0.0;
        if (source != null && dest != null && source.getLatitude() != null && source.getLongitude() != null
                && dest.getLatitude() != null && dest.getLongitude() != null) {
            distance = calculateHaversineDistance(source.getLatitude(), source.getLongitude(), dest.getLatitude(), dest.getLongitude());
        } else {
            distance = Math.abs(endIdx - startIdx) * 3.0; // Fallback: 3km per stop sequence
        }

        // Round properly to 2 decimal places
        distance = Math.round(distance * 100.0) / 100.0;
        double fare = distance * 3.5;
        fare = Math.round(fare * 100.0) / 100.0;

        Ticket ticket = new Ticket();
        ticket.setPassengerPhoneNumber(passenger.getPhoneNumber());
        ticket.setBusId(busNumber);
        ticket.setBusName(bus.getName());
        ticket.setSourceStop(sourceStop);
        ticket.setDestinationStop(destinationStop);
        ticket.setDistance(distance);
        ticket.setFare(fare);
        ticket.setBoughtTime(LocalDateTime.now());
        ticket.setValidUntil(LocalDateTime.now().plusHours(TICKET_VALIDITY_HOURS));
        ticket.setIsValid(Boolean.TRUE);

        Ticket savedTicket = ticketRepository.save(ticket);

        // Also populate TravelHistory logger
        TravelHistory history = new TravelHistory();
        history.setPassengerPhone(passenger.getPhoneNumber());
        history.setBusId(busNumber);
        history.setBusName(bus.getName());
        history.setSourceStop(sourceStop);
        history.setDestinationStop(destinationStop);
        history.setTravelDate(LocalDateTime.now());
        history.setDistance(distance);
        history.setFare(fare);
        travelHistoryRepository.save(history);

        return savedTicket;
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
        return ticketRepository.findByPassengerPhoneNumber(phoneNumber);
    }

    // ==================== Passenger Operations ====================

    /**
     * Deletes a passenger from the database.
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

    public double calculateHaversineDistance(double lat1, double lon1, double lat2, double lon2) {
        final double R = 6371.0; // Earth radius in kilometers
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    @Transactional
    public void updateBusLocation(int busId, double lat, double lon, double speed, double bearing) {
        BusLiveLocation location = busLiveLocationRepository.findById(busId).orElse(null);
        if (location == null) {
            location = new BusLiveLocation();
            location.setBusId(busId);
        }
        location.setLatitude(lat);
        location.setLongitude(lon);
        location.setSpeed(speed);
        location.setBearing(bearing);
        location.setLastUpdated(LocalDateTime.now());
        busLiveLocationRepository.save(location);

        // Geolocation Stop Detection: Check sequence of stops for this bus
        List<Stop> stops = stopRepository.findByBusIdOrderByIdAsc(busId);
        if (stops != null && !stops.isEmpty()) {
            Stop newlyReachedStop = null;
            for (Stop stop : stops) {
                if (stop.getLatitude() != null && stop.getLongitude() != null) {
                    double dist = calculateHaversineDistance(lat, lon, stop.getLatitude(), stop.getLongitude());
                    // threshold: 100 meters (0.1 km)
                    if (dist <= 0.1) {
                        newlyReachedStop = stop;
                    }
                }
            }
            if (newlyReachedStop != null) {
                for (Stop s : stops) {
                    if (s.getDbId().equals(newlyReachedStop.getDbId())) {
                        s.setCurrentStop(true);
                        s.setUpdatedTime(java.time.LocalTime.now());
                    } else {
                        s.setCurrentStop(false);
                    }
                }
                stopRepository.saveAll(stops);
            }
        }
    }

    public BusLiveLocation getBusLiveLocation(int busId) {
        return busLiveLocationRepository.findById(busId).orElse(null);
    }

    public List<TravelHistory> getPassengerTravelHistory(String passengerPhone) {
        return travelHistoryRepository.findByPassengerPhoneOrderByTravelDateDesc(passengerPhone);
    }

    public void addStop(Stop stop) {
        stopRepository.save(stop);
    }
}
