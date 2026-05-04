package com.sunilskyros.payanam.features.homepage;

import com.sunilskyros.payanam.data.dto.Bus;
import com.sunilskyros.payanam.data.dto.Passenger;
import com.sunilskyros.payanam.data.dto.Stop;
import com.sunilskyros.payanam.data.dto.Ticket;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HomePresenter {
    private final HomeView homeView;
    private final HomeModel homeModel;
    private final static DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("hh:mm a");

    public HomePresenter(HomeView homeView) {
        this.homeView = homeView;
        this.homeModel = new HomeModel();
    }

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

    private List<Bus> searchBusesByStop(String stopName) {
        List<Bus> result = new ArrayList<>();
        if (stopName == null || stopName.trim().isEmpty()) return result;

        String normalizedStop = stopName.trim().toLowerCase();
        for (Bus bus : homeModel.getBusList().values()) {
            List<Stop> stops = bus.getStops();
            if (stops == null) continue;
            for (Stop stop : stops) {
                if (stop.getStopName() != null
                        && stop.getStopName().trim().toLowerCase().contains(normalizedStop)) {
                    result.add(bus);
                    break;
                }
            }
        }
        return result;
    }

    private Ticket createTicket(Passenger passenger, int busNumber, String sourceStop, String destinationStop) {
        if (passenger == null || passenger.getPhoneNumber() == null) return null;
        Bus bus = homeModel.getBusByNumber(busNumber);
        if (bus == null) return null;
        int basePrice = 10;
        int startIdx = getStopIdx(bus, sourceStop);
        int endIdx = getStopIdx(bus, destinationStop);
        if (startIdx == 0 || endIdx == 0) return null;

        Ticket ticket = new Ticket();
        ticket.setPassengerPhoneNumber(passenger.getPhoneNumber());
        ticket.setBusId(bus.getId());
        ticket.setBusName(bus.getName());
        ticket.setSourceStop(sourceStop);
        ticket.setDestinationStop(destinationStop);
        ticket.setPrice(basePrice * (Math.abs(startIdx - endIdx)));
        ticket.setBoughtTime(LocalDateTime.now());
        ticket.setValidUntil(LocalDateTime.now().plusHours(4));
        return homeModel.addTicket(ticket);
    }

    private int getStopIdx(Bus bus, String stop) {
        List<Stop> stops = bus.getStops();
        int idx = 0;
        for (int i = 0; i < stops.size(); i++) {
            if (stops.get(i).getStopName().equalsIgnoreCase(stop)) {
                idx = i + 1;
                break;
            }
        }
        return idx;
    }

    void listAllBuses() {
        Map<Integer, Bus> busList = homeModel.getBusList();
        if (busList.isEmpty()) {
            homeView.showMessage("No buses available");
            return;
        }
        showBusList(new ArrayList<>(busList.values()));
    }

    void showBusList(List<Bus> buses) {
        for (Bus bus : buses) {
            homeView.showBus(bus);
        }
    }

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

    void searchBusByStop(String stopName) {
        if (stopName == null || stopName.trim().isEmpty()) {
            homeView.showError("Stop name cannot be empty");
            return;
        }

        List<Bus> buses = searchBusesByStop(stopName);
        if (buses.isEmpty()) {
            homeView.showMessage("No buses found for stop: " + stopName);
            return;
        }
        homeView.showBusesForStop(stopName, buses);
    }

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

        int startIdx = getStopIdx(bus, source.trim());
        int endIdx = getStopIdx(bus, destination.trim());

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

        Ticket ticket = createTicket(passenger, busNumber, source.trim(), destination.trim());
        if (ticket == null) {
            homeView.showError("Could not book ticket. Please check if the source and destination stops are valid.");
            return;
        }
        homeView.showBookedTicket(ticket);
    }

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
        bus.setStop(new ArrayList<>());
        homeModel.addBus(bus);
        homeView.showMessage("Bus added successfully");
    }

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
            if (stopName.isEmpty()) continue;

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

        bus.setStop(stops);
        homeModel.updateBusStops(bus);
        homeView.showMessage("Stops replaced successfully");
        homeView.showBus(bus);
        buildRoute(bus);
    }

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

        bus.setStop(new ArrayList<>());
        homeModel.updateBusStops(bus);
        homeView.showMessage("Stops deleted successfully");
    }

    private String buildRoute(Bus bus) {
        List<Stop> stops = bus.getStops();
        if (stops == null || stops.isEmpty()) return "Not available";
        for (Stop stop : stops) {
            StringBuilder message = new StringBuilder();
            if (stop.getCurrentStop() == null) {
                message.append("[ ] ").append(stop.getStopName()).append(" ");
                if (stop.getUpdatedTime() == LocalTime.of(0, 0)) {
                    message.append(LocalTime.of(0, 0));
                } else message.append(stop.getUpdatedTime().format(FORMATTER));
            } else if (stop.getCurrentStop()) {
                message.append("[->] ").append(stop.getStopName()).append(" ").append(stop.getUpdatedTime().format(FORMATTER));
            } else {
                message.append("[✔] ").append(stop.getStopName()).append(" ").append(stop.getUpdatedTime().format(FORMATTER));
            }
            homeView.showStop(message.toString());
        }
        return null;
    }

    void setDefaultTime(Bus bus) {
        List<Stop> stops = bus.getStops();
        StringBuilder message = new StringBuilder();
        for (Stop current : stops) {
            if (current.getCurrentStop() == null) {
                message.append("[ ] ").append(current.getStopName()).append(" ").append(current.getUpdatedTime());
            }
            homeView.showStop(message.toString());
        }
    }
}
