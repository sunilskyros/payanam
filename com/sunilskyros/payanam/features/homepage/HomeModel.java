package com.sunilskyros.payanam.features.homepage;

import com.sunilskyros.payanam.data.dto.Bus;
import com.sunilskyros.payanam.data.dto.Passenger;
import com.sunilskyros.payanam.data.dto.Stop;
import com.sunilskyros.payanam.data.dto.Ticket;
import com.sunilskyros.payanam.data.repository.PayanamDB;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HomeModel {
    private final HomeView homeView;

    HomeModel(HomeView homeView){
        this.homeView=homeView;
    }
    void init(Passenger passenger){
        if (passenger == null || passenger.getRole() == null) {
            homeView.showUnauthorized();
            return;
        }
        if (passenger.getRole() == Passenger.Role.PASSENGER) {
            homeView.showPassengerMenu();
        } else {
            homeView.showTicketCollectorMenu();
        }
    }

    private Bus getBusByNumber(int busNumber) {
        return PayanamDB.getInstance().getBusById(busNumber);
    }

    private List<Bus> searchBusesByStop(String stopName) {
        List<Bus> result = new ArrayList<>();
        if (stopName == null || stopName.trim().isEmpty()) return result;

        String normalizedStop = stopName.trim().toLowerCase();
        for (Bus bus : PayanamDB.getInstance().getBusList().values()) {
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
        Bus bus = getBusByNumber(busNumber);
        if (bus == null) return null;
        int basePrice=10;
        int startIdx=getStopIdx(bus,sourceStop);
        int endIdx=getStopIdx(bus,destinationStop);
        Ticket ticket = new Ticket();
        ticket.setPassengerPhoneNumber(passenger.getPhoneNumber());
        ticket.setBusId(bus.getId());
        ticket.setBusName(bus.getName());
        ticket.setSourceStop(sourceStop);
        ticket.setDestinationStop(destinationStop);
        ticket.setPrice(basePrice*(Math.abs(startIdx-endIdx)));
        return PayanamDB.getInstance().addTicket(ticket);
    }

    private int getStopIdx(Bus bus,String stop){
        List<Stop> stops=bus.getStops();
        int idx=0;
        for(int i=0;i<stops.size();i++){
            if(stops.get(i).getStopName().equalsIgnoreCase(stop)){
                idx=i+1;
                break;
            }
        }
        return idx;
    }

    private List<Ticket> getPassengerTickets(String phoneNumber) {
        return PayanamDB.getInstance().getTicketsByPassenger(phoneNumber);
    }

    void listAllBuses() {
        Map<Integer, Bus> busList = PayanamDB.getInstance().getBusList();
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

        Bus bus = getBusByNumber(busNumber);
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

        Ticket ticket = createTicket(passenger, busNumber, source.trim(), destination.trim());
        if (ticket == null) {
            homeView.showError("Could not book ticket");
            return;
        }
        homeView.showBookedTicket(ticket);
    }

    void viewTickets(Passenger passenger) {
        if (passenger == null) {
            homeView.showError("Passenger not found");
            return;
        }
        List<Ticket> tickets = getPassengerTickets(passenger.getPhoneNumber());
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

        Bus bus = getBusByNumber(busNumber);
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

        Map<Integer, Bus> busList = PayanamDB.getInstance().getBusList();
        if (busList.containsKey(busNumber)) {
            homeView.showError("Bus already exists");
            return;
        }

        Bus bus = new Bus();
        bus.setId(busNumber);
        bus.setName(busName.trim());
        bus.setStop(new ArrayList<>());
        busList.put(busNumber, bus);
        homeView.showMessage("Bus added successfully");
    }

    void addStops(String busInput, String stopsInput) {
        int busNumber;
        try {
            busNumber = Integer.parseInt(busInput);
        } catch (NumberFormatException e) {
            homeView.showError("Invalid Bus Number");
            return;
        }

        Bus bus = getBusByNumber(busNumber);
        if (bus == null) {
            homeView.showError("Bus not found");
            return;
        }
        if (stopsInput == null || stopsInput.trim().isEmpty()) {
            homeView.showError("Stops cannot be empty");
            return;
        }

        String[] parts = stopsInput.split(",");
        List<Stop> stops = new ArrayList<>(getBusByNumber(busNumber).getStops());
        int index = stops.size()+1;
        for (String part : parts) {
            String stopName = part.trim();
            if (stopName.isEmpty()) continue;

            Stop stop = new Stop();
            stop.setId(index);
            stop.setBusId(busNumber);
            stop.setOrder(index);
            stop.setStopName(stopName);
            stops.add(stop);
            index++;
        }

        if (stops.isEmpty()) {
            homeView.showError("Enter valid stop names separated by comma");
            return;
        }

        bus.setStop(stops);
        homeView.showMessage("Stops updated successfully");
        homeView.showBus(bus);
        buildRoute(bus);
    }

    private String buildRoute(Bus bus) {
        List<Stop> stops = bus.getStops();
        if (stops == null || stops.isEmpty()) return "Not available";
        for (Stop stop : stops) {
            StringBuilder message=new StringBuilder();
            if(stop.getCurrentStop()==null){
                message.append("[ ] ");
            }
            else if(stop.getCurrentStop()==true) {
                message.append("[->] ");
            } else{
                message.append("[✔] ");
            }
            message.append(stop.getStopName());
            homeView.showStop(message.toString());
        }
        return null;
    }
}
