package com.sunilskyros.payanam.controller;

import com.sunilskyros.payanam.data.dto.Bus;
import com.sunilskyros.payanam.data.dto.Passenger;
import com.sunilskyros.payanam.data.dto.Stop;
import com.sunilskyros.payanam.data.dto.Ticket;
import com.sunilskyros.payanam.features.homepage.HomeModel;
import com.sunilskyros.payanam.features.ticketcollector.updatestop.UpdateStopModel;
import com.sunilskyros.payanam.features.ticketcollector.validateticket.ValidateTicketModel;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/api")
public class BusTicketController {

    private final HomeModel homeModel = new HomeModel();
    private final UpdateStopModel updateStopModel = new UpdateStopModel();
    private final ValidateTicketModel validateTicketModel = new ValidateTicketModel();

    // ---------------- BUS TRACKING ----------------

    @GetMapping("/buses")
    public Collection<Bus> getBuses() {
        return homeModel.getBusList().values();
    }

    @GetMapping("/buses/{id}")
    public ResponseEntity<Bus> getBusDetails(@PathVariable int id) {
        Bus bus = homeModel.getBusByNumber(id);
        if (bus != null) return ResponseEntity.ok(bus);
        return ResponseEntity.notFound().build();
    }

    // ---------------- PASSENGER ----------------

    @PostMapping("/book")
    public ResponseEntity<String> bookTicket(@RequestParam int busId, 
                                             @RequestParam String source, 
                                             @RequestParam String dest,
                                             HttpSession session) {
        Passenger user = (Passenger) session.getAttribute("user");
        if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Login required");

        // USE THE PREVIOUSLY CODED BUSINESS LOGIC MODEL (Calculates prices and validations!)
        Ticket saved = homeModel.createTicket(user, busId, source, dest);
        
        if (saved != null) return ResponseEntity.ok("Ticket booked! ID: " + saved.getTicketId() + " | Price: Rs " + saved.getPrice());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Booking failed. Please ensure source and destination are valid.");
    }

    @GetMapping("/tickets/my")
    public ResponseEntity<List<Ticket>> getMyTickets(HttpSession session) {
        Passenger user = (Passenger) session.getAttribute("user");
        if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        
        return ResponseEntity.ok(homeModel.getPassengerTickets(user.getPhoneNumber()));
    }

    // ---------------- TICKET COLLECTOR ----------------
    
    @PostMapping("/collector/selectBus")
    public ResponseEntity<String> selectBus(@RequestParam int busId, HttpSession session) {
        Passenger user = (Passenger) session.getAttribute("user");
        if (user == null || user.getRole() != Passenger.Role.TICKETCOLLECTOR) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access Denied");
        }
        
        Integer currentAssigned = (Integer) session.getAttribute("assignedBusId");
        if (currentAssigned != null) {
            return ResponseEntity.badRequest().body("You are already assigned to Bus ID " + currentAssigned + ". Finish your travel first.");
        }
        
        Bus bus = homeModel.getBusByNumber(busId);
        if (bus == null) {
            return ResponseEntity.badRequest().body("Bus not found");
        }
        
        session.setAttribute("assignedBusId", busId);
        return ResponseEntity.ok("Successfully selected Bus: " + bus.getName());
    }

    @GetMapping("/collector/assignedBus")
    public ResponseEntity<Bus> getAssignedBus(HttpSession session) {
        Passenger user = (Passenger) session.getAttribute("user");
        if (user == null || user.getRole() != Passenger.Role.TICKETCOLLECTOR) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        Integer assignedBusId = (Integer) session.getAttribute("assignedBusId");
        if (assignedBusId == null) {
            return ResponseEntity.noContent().build();
        }
        
        Bus bus = homeModel.getBusByNumber(assignedBusId);
        return ResponseEntity.ok(bus);
    }

    @PostMapping("/collector/finishShift")
    public ResponseEntity<String> finishShift(HttpSession session) {
        Passenger user = (Passenger) session.getAttribute("user");
        if (user == null || user.getRole() != Passenger.Role.TICKETCOLLECTOR) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access Denied");
        }
        
        session.removeAttribute("assignedBusId");
        return ResponseEntity.ok("Shift finished successfully.");
    }

    @PostMapping("/collector/updateLocation")
    public ResponseEntity<String> updateLocation(@RequestParam int busId, @RequestParam int stopSeq, HttpSession session) {
        Passenger user = (Passenger) session.getAttribute("user");
        if (user == null || user.getRole() != Passenger.Role.TICKETCOLLECTOR) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access Denied");
        }

        Integer assignedBusId = (Integer) session.getAttribute("assignedBusId");
        if (assignedBusId == null || assignedBusId != busId) {
            return ResponseEntity.badRequest().body("You are not assigned to this bus.");
        }

        Bus bus = homeModel.getBusByNumber(busId);
        if (bus == null) return ResponseEntity.badRequest().body("Bus not found");

        List<Stop> stops = bus.getStops();

        // 1. Update current stop location
        for (Stop stop : stops) {
            if (stop.getId() == stopSeq) {
                stop.setCurrentStop(true);
                stop.setUpdatedTime(LocalTime.now());
            } else {
                stop.setCurrentStop(false);
            }
        }
        
        // 2. Calculate ETAs for subsequent stops (using pre-existing business logic!)
        updateStopModel.calculateEstimatedTimes(stops, stopSeq - 1);
        
        // 3. Persist modifications
        updateStopModel.updateStops(stops);
        
        // 4. If we reached the last stop, reverse the route
        boolean isLastStop = (stopSeq == stops.size());
        if (isLastStop) {
            List<Stop> reversedStops = updateStopModel.reverseRoute(stops);
            bus.setStops(reversedStops);
            updateStopModel.updateBusStops(bus);
            return ResponseEntity.ok("ROUTE_REVERSED");
        }
        
        return ResponseEntity.ok("Location updated");
    }

    @PostMapping("/collector/validateTicket")
    public ResponseEntity<String> validateTicket(@RequestParam int ticketId, HttpSession session) {
        Passenger user = (Passenger) session.getAttribute("user");
        if (user == null || user.getRole() != Passenger.Role.TICKETCOLLECTOR) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access Denied");
        }

        // USE THE PREVIOUSLY CODED BUSINESS LOGIC MODEL!
        Ticket ticket = validateTicketModel.getTicketById(ticketId);
        if (ticket == null) return ResponseEntity.badRequest().body("Ticket not found");
        
        if (!ticket.getIsValid()) return ResponseEntity.ok("Ticket is already invalid or used.");

        ticket.setIsValid(false);
        validateTicketModel.updateTicket(ticket);
        return ResponseEntity.ok("Ticket Validated Successfully!");
    }
}
