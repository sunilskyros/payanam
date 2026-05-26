package com.sunilskyros.payanam.controller;

import com.sunilskyros.payanam.data.dto.Bus;
import com.sunilskyros.payanam.data.dto.Passenger;
import com.sunilskyros.payanam.data.dto.Stop;
import com.sunilskyros.payanam.data.dto.Ticket;
import com.sunilskyros.payanam.features.homepage.HomeModel;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final HomeModel homeModel;

    @org.springframework.beans.factory.annotation.Autowired
    public AdminController(HomeModel homeModel) {
        this.homeModel = homeModel;
    }

    private boolean isAdmin(HttpSession session) {
        Passenger user = (Passenger) session.getAttribute("user");
        return user != null && user.getRole() == Passenger.Role.ADMIN;
    }

    @PostMapping("/collector")
    public ResponseEntity<String> addCollector(@RequestParam String name, 
                                               @RequestParam String phone, 
                                               @RequestParam String password,
                                               HttpSession session) {
        if (!isAdmin(session)) return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access Denied");
        
        // USE THE PREVIOUSLY CODED BUSINESS LOGIC MODEL!
        Passenger result = homeModel.addTicketCollector(name, phone, password);
        if (result != null) return ResponseEntity.ok("Ticket Collector added successfully");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to add collector");
    }

    @PostMapping("/bus")
    public ResponseEntity<String> addBus(@RequestParam int id, @RequestParam String name, HttpSession session) {
        if (!isAdmin(session)) return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access Denied");
        
        Bus bus = new Bus();
        bus.setId(id);
        bus.setName(name);
        
        // USE THE PREVIOUSLY CODED BUSINESS LOGIC MODEL!
        homeModel.addBus(bus);
        return ResponseEntity.ok("Bus added successfully");
    }

    @DeleteMapping("/bus/{id}")
    public ResponseEntity<String> deleteBus(@PathVariable int id, HttpSession session) {
        if (!isAdmin(session)) return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access Denied");
        
        // USE THE PREVIOUSLY CODED BUSINESS LOGIC MODEL!
        homeModel.removeBus(id);
        return ResponseEntity.ok("Bus deleted successfully");
    }

    @PostMapping("/bus/{id}/stops")
    public ResponseEntity<String> updateStops(@PathVariable int id, @RequestBody List<String> stopNames, HttpSession session) {
        if (!isAdmin(session)) return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access Denied");
        
        Bus bus = new Bus();
        bus.setId(id);
        List<Stop> stops = new ArrayList<>();
        int seq = 1;
        for (String name : stopNames) {
            Stop stop = new Stop();
            stop.setStopName(name);
            stop.setId(seq++);
            stops.add(stop);
        }
        bus.setStops(stops);
        
        // USE THE PREVIOUSLY CODED BUSINESS LOGIC MODEL!
        homeModel.updateBusStops(bus);
        return ResponseEntity.ok("Stops updated successfully");
    }

    @GetMapping("/passengers")
    public ResponseEntity<List<Passenger>> getPassengers(HttpSession session) {
        if (!isAdmin(session)) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        return ResponseEntity.ok(homeModel.getAllPassengers());
    }

    @GetMapping("/tickets")
    public ResponseEntity<List<Ticket>> getTickets(HttpSession session) {
        if (!isAdmin(session)) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        return ResponseEntity.ok(homeModel.getAllTickets());
    }

    @GetMapping("/buses-with-stops")
    public ResponseEntity<List<Bus>> getBusesWithStops(HttpSession session) {
        if (!isAdmin(session)) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        return ResponseEntity.ok(homeModel.getAllBusesWithStops());
    }

    @DeleteMapping("/user/{phone}")
    public ResponseEntity<String> deleteUser(@PathVariable String phone, HttpSession session) {
        if (!isAdmin(session)) return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access Denied");
        Passenger passenger = homeModel.getPassengerByPhone(phone);
        if (passenger != null) {
            homeModel.removePassenger(passenger);
            return ResponseEntity.ok("User deleted successfully");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
    }
}
