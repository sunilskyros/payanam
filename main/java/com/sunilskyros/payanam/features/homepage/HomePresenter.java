package com.sunilskyros.payanam.features.homepage;

import com.sunilskyros.payanam.data.dto.Bus;
import com.sunilskyros.payanam.data.dto.Passenger;
import com.sunilskyros.payanam.data.dto.Stop;
import com.sunilskyros.payanam.data.dto.Ticket;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HomePresenter {
    private final HomeView homeView;
    private final HomeModel homeModel;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("hh:mm a");

    public HomePresenter(HomeView homeView) {
        this.homeView = homeView;
        this.homeModel = new HomeModel();
    }

    /**
     * Initializes the home screen based on the user's role.
     * Routes the user to the appropriate menu (Passenger, Ticket Collector, or Admin).
     * @param passenger The logged-in passenger/user.
     */
    void init(Passenger passenger) {
        if (passenger == null || passenger.getRole() == null) {
            homeView.showUnauthorized();
            return;
        }
        if (passenger.getRole() == Passenger.Role.PASSENGER) {
            homeView.showPassengerMenu();
        } else if (passenger.getRole() == Passenger.Role.TICKETCOLLECTOR) {
            homeView.showTicketCollectorMenu();
        } else if (passenger.getRole() == Passenger.Role.ADMIN) {
            homeView.showAdminMenu();
        }
    }

    // ==================== Bus Search & Display ====================

    /**
     * Fetches all available buses from the model and instructs the view to display them.
     */
    void listAllBuses() {
        Map<Integer, Bus> busList = homeModel.getBusList();
        if (busList.isEmpty()) {
            homeView.showMessage("No buses available");
            return;
        }
        showBusList(new ArrayList<>(busList.values()));
    }

    /**
     * Helper method to iterate through a list of buses and display each one via the view.
     * @param buses List of buses to display.
     */
    void showBusList(List<Bus> buses) {
        for (Bus bus : buses) {
            homeView.showBus(bus);
        }
    }

    /**
     * Searches for a bus by its unique ID/Number.
     * Validates input, fetches the bus from the model, and displays its details and route.
     * @param busInput The string input containing the bus number.
     */
    void searchBusByNumber(String busInput) {
        int busNumber;
        try {
            busNumber = Integer.parseInt(busInput);
        } catch (NumberFormatException e) {
            homeView.showError("Invalid Bus Number");
            return;
        }

        Bus bus = homeModel.getBusByNumber(busNumber);
        if (bus == null) {
            homeView.showError("Bus not found");
            return;
        }

        homeView.showBus(bus);
        buildRoute(bus);
    }

    /**
     * Searches for all buses that pass through a specific stop.
     * Validates input and instructs the view to display the matching buses.
     * @param stopName The name of the stop to search for.
     */
    void searchBusByStop(String stopName) {
        if (stopName == null || stopName.trim().isEmpty()) {
            homeView.showError("Stop name cannot be empty");
            return;
        }

        List<Bus> buses = homeModel.searchBusesByStop(stopName);
        if (buses.isEmpty()) {
            homeView.showMessage("No buses found for stop: " + stopName);
            return;
        }
        homeView.showBusesForStop(stopName, buses);
    }

    // ==================== Ticket Operations ====================

    /**
     * Handles the ticket booking process.
     * Validates inputs, ensures logical stop sequences, requests ticket creation from the model,
     * and updates the view with success or error messages.
     * @param passenger The passenger booking the ticket.
     * @param busInput The input containing the bus number.
     * @param source The starting stop name.
     * @param destination The ending stop name.
     */
    void bookTicket(Passenger passenger, String busInput, String source, String destination) {
        int busNumber;
        try {
            busNumber = Integer.parseInt(busInput);
        } catch (NumberFormatException e) {
            homeView.showError("Invalid Bus Number");
            return;
        }

        if (source == null || source.trim().isEmpty() || destination == null || destination.trim().isEmpty()) {
            homeView.showError("Source and destination cannot be empty");
            return;
        }
        if (source.trim().equalsIgnoreCase(destination.trim())) {
            homeView.showError("Source and destination cannot be same");
            return;
        }

        Bus bus = homeModel.getBusByNumber(busNumber);
        if (bus == null) {
            homeView.showError("Bus not found");
            return;
        }

        int startIdx = homeModel.getStopIndex(bus, source.trim());
        int endIdx = homeModel.getStopIndex(bus, destination.trim());

        if (startIdx == 0 || endIdx == 0) {
            homeView.showError("Invalid Source or Destination Stop");
            return;
        }

        if (startIdx >= endIdx) {
            homeView.showError("Destination must be after Source Stop");
            return;
        }

        Stop sourceStopObj = bus.getStops().get(startIdx - 1);
        if (Boolean.FALSE.equals(sourceStopObj.getCurrentStop())) {
            homeView.showError("Bus has already passed " + source.trim());
            return;
        }

        Ticket ticket = homeModel.createTicket(passenger, busNumber, source.trim(), destination.trim());
        if (ticket == null) {
            homeView.showError("Could not book ticket. Please check if the source and destination stops are valid.");
            return;
        }
        homeView.showBookedTicket(ticket);
    }

    /**
     * Retrieves and displays all tickets booked by the current passenger.
     * @param passenger The passenger whose tickets are to be viewed.
     */
    void viewTickets(Passenger passenger) {
        if (passenger == null) {
            homeView.showError("Passenger not found");
            return;
        }
        List<Ticket> tickets = homeModel.getPassengerTickets(passenger.getPhoneNumber());
        if (tickets.isEmpty()) {
            homeView.showMessage("No tickets booked yet.");
            return;
        }
        homeView.showTickets(tickets);
    }

    // ==================== Bus Management (Admin) ====================

    /**
     * Selects and validates a bus for operations like updating stops.
     * @param busInput The bus number input.
     * @return The Bus object if found, otherwise null.
     */
    Bus selectBus(String busInput) {
        int busNumber;
        try {
            busNumber = Integer.parseInt(busInput);
        } catch (NumberFormatException e) {
            homeView.showError("Invalid Bus Number");
            return null;
        }

        Bus bus = homeModel.getBusByNumber(busNumber);
        if (bus == null) {
            homeView.showError("Bus not found");
        }
        return bus;
    }

    /**
     * Adds a new bus to the system.
     * Validates the input, ensures the bus ID doesn't already exist, and saves it via the model.
     * @param busInput The ID/Number of the new bus.
     * @param busName The Name of the new bus.
     */
    void addBus(String busInput, String busName) {
        int busNumber;
        try {
            busNumber = Integer.parseInt(busInput);
        } catch (NumberFormatException e) {
            homeView.showError("Invalid Bus Number");
            return;
        }
        if (busNumber <= 0) {
            homeView.showError("Bus Number must be greater than 0");
            return;
        }
        if (busName == null || busName.trim().isEmpty()) {
            homeView.showError("Bus Name cannot be empty");
            return;
        }

        Map<Integer, Bus> busList = homeModel.getBusList();
        if (busList.containsKey(busNumber)) {
            homeView.showError("Bus already exists");
            return;
        }

        Bus bus = new Bus();
        bus.setId(busNumber);
        bus.setName(busName.trim());
        bus.setStops(new ArrayList<>());
        homeModel.addBus(bus);
        homeView.showMessage("Bus added successfully");
    }

    /**
     * Sets or overwrites the stops for a specific bus.
     * Parses a comma-separated string of stops, validates them, and saves them via the model.
     * @param busInput The bus number to update.
     * @param stopsInput Comma-separated list of stop names.
     */
    void setStops(String busInput, String stopsInput) {
        int busNumber;
        try {
            busNumber = Integer.parseInt(busInput);
        } catch (NumberFormatException e) {
            homeView.showError("Invalid Bus Number");
            return;
        }

        Bus bus = homeModel.getBusByNumber(busNumber);
        if (bus == null) {
            homeView.showError("Bus not found");
            return;
        }
        if (stopsInput == null || stopsInput.trim().isEmpty()) {
            homeView.showError("Stops cannot be empty");
            return;
        }

        String[] parts = stopsInput.split(",");
        List<Stop> stops = new ArrayList<>();
        int index = 1;
        for (String part : parts) {
            String stopName = part.trim();
            if (stopName.isEmpty()) {
                continue;
            }

            Stop stop = new Stop();
            stop.setId(index);
            stop.setBusId(busNumber);
            stop.setUpdatedTime(LocalTime.of(0, 0));
            stop.setStopName(stopName);
            stops.add(stop);
            index++;
        }

        if (stops.isEmpty()) {
            homeView.showError("Enter valid stop names separated by comma");
            return;
        }

        bus.setStops(stops);
        homeModel.updateBusStops(bus);
        homeView.showMessage("Stops replaced successfully");
        homeView.showBus(bus);
        buildRoute(bus);
    }

    /**
     * Deletes a bus from the system after validating its existence.
     * @param busInput The bus number to delete.
     */
    void deleteBus(String busInput) {
        int busNumber;
        try {
            busNumber = Integer.parseInt(busInput);
        } catch (NumberFormatException e) {
            homeView.showError("Invalid Bus Number");
            return;
        }

        Bus bus = homeModel.getBusByNumber(busNumber);
        if (bus == null) {
            homeView.showError("Bus not found");
            return;
        }

        homeModel.removeBus(busNumber);
        homeView.showMessage("Bus deleted successfully");
    }

    /**
     * Clears all stops associated with a specific bus.
     * @param busInput The bus number whose stops should be deleted.
     */
    void deleteStops(String busInput) {
        int busNumber;
        try {
            busNumber = Integer.parseInt(busInput);
        } catch (NumberFormatException e) {
            homeView.showError("Invalid Bus Number");
            return;
        }

        Bus bus = homeModel.getBusByNumber(busNumber);
        if (bus == null) {
            homeView.showError("Bus not found");
            return;
        }

        bus.setStops(new ArrayList<>());
        homeModel.updateBusStops(bus);
        homeView.showMessage("Stops deleted successfully");
    }

    // ==================== Passenger Management (Admin) ====================

    /**
     * Removes a passenger or user from the system based on their phone number.
     * @param passengerPhoneNumber The phone number of the user to remove.
     */
    void removePassenger(String passengerPhoneNumber) {
        Passenger passenger = homeModel.getPassengerByPhone(passengerPhoneNumber);
        if (passenger == null) {
            homeView.showError("Passenger not found");
            return;
        }
        homeModel.removePassenger(passenger);
        homeView.showMessage("Passenger removed successfully");
    }

    /**
     * Registers a new Ticket Collector in the system via the model.
     * @param name Name of the collector.
     * @param phoneNumber Phone number of the collector.
     * @param password Password for the collector's account.
     * @return The created Passenger object, or null if creation failed.
     */
    Passenger addTicketCollector(String name, String phoneNumber, String password) {
        return homeModel.addTicketCollector(name, phoneNumber, password);
    }

    // ==================== Route Display (Presentation Logic) ====================

    /**
     * Formats and builds a visual representation of a bus's route and its current progress.
     * Sends the formatted route step-by-step to the view.
     * @param bus The Bus object containing the route to display.
     */
    private void buildRoute(Bus bus) {
        List<Stop> stops = bus.getStops();
        if (stops == null || stops.isEmpty()) {
            return;
        }

        for (Stop stop : stops) {
            StringBuilder message = new StringBuilder();
            if (stop.getCurrentStop() == null) {
                message.append("[ ] ").append(stop.getStopName()).append(" ");
                if (stop.getUpdatedTime().equals(LocalTime.of(0, 0))) {
                    message.append(LocalTime.of(0, 0));
                } else {
                    message.append(stop.getUpdatedTime().format(FORMATTER));
                }
            } else if (stop.getCurrentStop()) {
                message.append("[->] ").append(stop.getStopName()).append(" ")
                        .append(stop.getUpdatedTime().format(FORMATTER));
            } else {
                message.append("[✔] ").append(stop.getStopName()).append(" ")
                        .append(stop.getUpdatedTime().format(FORMATTER));
            }
            homeView.showStop(message.toString());
        }
    }
}
