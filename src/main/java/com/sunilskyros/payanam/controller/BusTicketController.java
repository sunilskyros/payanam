package com.sunilskyros.payanam.controller;

import com.sunilskyros.payanam.data.dto.Bus;
import com.sunilskyros.payanam.data.dto.Passenger;
import com.sunilskyros.payanam.data.dto.Stop;
import com.sunilskyros.payanam.data.dto.Ticket;
import com.sunilskyros.payanam.data.dto.CollectorShift;
import com.sunilskyros.payanam.features.homepage.HomeModel;
import com.sunilskyros.payanam.features.ticketcollector.updatestop.UpdateStopModel;
import com.sunilskyros.payanam.features.ticketcollector.validateticket.ValidateTicketModel;
import com.sunilskyros.payanam.data.repository.CollectorShiftRepository;
import com.sunilskyros.payanam.data.repository.PassengerRepository;
import com.sunilskyros.payanam.data.repository.TicketRepository;
import com.sunilskyros.payanam.data.repository.BusRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class BusTicketController {

    private final HomeModel homeModel;
    private final UpdateStopModel updateStopModel;
    private final ValidateTicketModel validateTicketModel;
    
    private final CollectorShiftRepository collectorShiftRepository;
    private final PassengerRepository passengerRepository;
    private final TicketRepository ticketRepository;
    private final BusRepository busRepository;

    @Autowired
    public BusTicketController(HomeModel homeModel, 
                               UpdateStopModel updateStopModel, 
                               ValidateTicketModel validateTicketModel,
                               CollectorShiftRepository collectorShiftRepository,
                               PassengerRepository passengerRepository,
                               TicketRepository ticketRepository,
                               BusRepository busRepository) {
        this.homeModel = homeModel;
        this.updateStopModel = updateStopModel;
        this.validateTicketModel = validateTicketModel;
        this.collectorShiftRepository = collectorShiftRepository;
        this.passengerRepository = passengerRepository;
        this.ticketRepository = ticketRepository;
        this.busRepository = busRepository;
    }

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

    @GetMapping("/passenger/stats")
    public ResponseEntity<Map<String, Object>> getPassengerStats(HttpSession session) {
        Passenger user = (Passenger) session.getAttribute("user");
        if (user == null || user.getRole() != Passenger.Role.PASSENGER) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        List<Ticket> tickets = homeModel.getPassengerTickets(user.getPhoneNumber());
        int totalTrips = tickets.size();
        int moneySpent = tickets.stream().mapToInt(Ticket::getPrice).sum();

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalTrips", totalTrips);
        stats.put("moneySpent", moneySpent);
        return ResponseEntity.ok(stats);
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

        // Record a new ACTIVE shift in the database
        CollectorShift shift = new CollectorShift();
        shift.setCollectorPhone(user.getPhoneNumber());
        shift.setBusId(busId);
        shift.setBusName(bus.getName());
        shift.setShiftDate(LocalDate.now().toString());
        shift.setStartTime(LocalTime.now().toString().substring(0, 5));
        shift.setTicketsChecked(0);
        shift.setStatus("ACTIVE");
        collectorShiftRepository.save(shift);

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

        // Mark the active shift as COMPLETED in the database
        collectorShiftRepository.findFirstByCollectorPhoneAndStatusOrderByIdDesc(user.getPhoneNumber(), "ACTIVE")
                .ifPresent(shift -> {
                    shift.setEndTime(LocalTime.now().toString().substring(0, 5));
                    shift.setStatus("COMPLETED");
                    collectorShiftRepository.save(shift);
                });

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
        
        // 2. Calculate ETAs for subsequent stops
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

        Ticket ticket = validateTicketModel.getTicketById(ticketId);
        if (ticket == null) return ResponseEntity.badRequest().body("Ticket not found");
        
        if (!ticket.getIsValid()) return ResponseEntity.ok("Ticket is already invalid or used.");

        ticket.setIsValid(false);
        validateTicketModel.updateTicket(ticket);

        // Increment ticket checked count in current active shift
        collectorShiftRepository.findFirstByCollectorPhoneAndStatusOrderByIdDesc(user.getPhoneNumber(), "ACTIVE")
                .ifPresent(shift -> {
                    shift.setTicketsChecked(shift.getTicketsChecked() + 1);
                    collectorShiftRepository.save(shift);
                });

        return ResponseEntity.ok("Ticket Validated Successfully!");
    }

    @GetMapping("/collector/shifts")
    public ResponseEntity<List<CollectorShift>> getCollectorShifts(HttpSession session) {
        Passenger user = (Passenger) session.getAttribute("user");
        if (user == null || user.getRole() != Passenger.Role.TICKETCOLLECTOR) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(collectorShiftRepository.findByCollectorPhoneOrderByIdDesc(user.getPhoneNumber()));
    }

    @GetMapping("/collector/stats")
    public ResponseEntity<Map<String, Object>> getCollectorStats(HttpSession session) {
        Passenger user = (Passenger) session.getAttribute("user");
        if (user == null || user.getRole() != Passenger.Role.TICKETCOLLECTOR) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        List<CollectorShift> shifts = collectorShiftRepository.findByCollectorPhoneOrderByIdDesc(user.getPhoneNumber());
        int totalChecked = shifts.stream().mapToInt(CollectorShift::getTicketsChecked).sum();
        long completedShifts = shifts.stream().filter(s -> "COMPLETED".equals(s.getStatus())).count();

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalChecked", totalChecked);
        stats.put("completedShifts", completedShifts);
        stats.put("totalShifts", shifts.size());
        return ResponseEntity.ok(stats);
    }

    // ---------------- ADMINISTRATOR ----------------

    @GetMapping("/admin/overview")
    public ResponseEntity<Map<String, Object>> getAdminOverview(HttpSession session) {
        Passenger user = (Passenger) session.getAttribute("user");
        if (user == null || user.getRole() != Passenger.Role.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        long passengers = passengerRepository.countByRole(Passenger.Role.PASSENGER);
        long activeBuses = busRepository.count();
        long tickets = ticketRepository.count();
        int revenue = ticketRepository.findAll().stream().mapToInt(Ticket::getPrice).sum();
        long collectorsCount = passengerRepository.countByRole(Passenger.Role.TICKETCOLLECTOR);

        Map<String, Object> overview = new HashMap<>();
        overview.put("passengers", passengers);
        overview.put("buses", activeBuses);
        overview.put("tickets", tickets);
        overview.put("revenue", revenue);
        overview.put("collectors", collectorsCount);
        return ResponseEntity.ok(overview);
    }

    @GetMapping("/admin/collector-activity")
    public ResponseEntity<List<CollectorShift>> getCollectorActivity(HttpSession session) {
        Passenger user = (Passenger) session.getAttribute("user");
        if (user == null || user.getRole() != Passenger.Role.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(collectorShiftRepository.findAllByOrderByIdDesc());
    }

    @PostMapping("/tracking/location")
    public ResponseEntity<String> updateLocation(@RequestParam int busId,
                                                 @RequestParam double lat,
                                                 @RequestParam double lon,
                                                 @RequestParam double speed,
                                                 @RequestParam double bearing) {
        homeModel.updateBusLocation(busId, lat, lon, speed, bearing);
        return ResponseEntity.ok("Location updated successfully");
    }

    @GetMapping("/tracking/location/{busId}")
    public ResponseEntity<com.sunilskyros.payanam.data.dto.BusLiveLocation> getLiveLocation(@PathVariable int busId) {
        com.sunilskyros.payanam.data.dto.BusLiveLocation location = homeModel.getBusLiveLocation(busId);
        if (location != null) {
            return ResponseEntity.ok(location);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @GetMapping("/tracking/stops/{busId}")
    public ResponseEntity<List<Stop>> getRouteStops(@PathVariable int busId) {
        Bus bus = homeModel.getBusByNumber(busId);
        if (bus != null && bus.getStops() != null) {
            return ResponseEntity.ok(bus.getStops());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @GetMapping("/fare/calculate")
    public ResponseEntity<Map<String, Object>> calculateFare(@RequestParam int busId,
                                                             @RequestParam String source,
                                                             @RequestParam String dest) {
        Bus bus = homeModel.getBusByNumber(busId);
        if (bus == null || bus.getStops() == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Stop start = null;
        Stop end = null;
        for (Stop stop : bus.getStops()) {
            if (stop.getStopName().equalsIgnoreCase(source)) {
                start = stop;
            }
            if (stop.getStopName().equalsIgnoreCase(dest)) {
                end = stop;
            }
        }

        int startIdx = start != null ? start.getId() : 0;
        int endIdx = end != null ? end.getId() : 0;

        if (startIdx == 0 || endIdx == 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        double distance = 0.0;
        if (start.getLatitude() != null && start.getLongitude() != null && end.getLatitude() != null && end.getLongitude() != null) {
            distance = homeModel.calculateHaversineDistance(start.getLatitude(), start.getLongitude(), end.getLatitude(), end.getLongitude());
        } else {
            distance = Math.abs(endIdx - startIdx) * 3.0; // 3km sequence multiplier
        }

        distance = Math.round(distance * 100.0) / 100.0;
        double fare = distance * 3.5;
        fare = Math.round(fare * 100.0) / 100.0;

        Map<String, Object> response = new HashMap<>();
        response.put("distance", distance);
        response.put("fare", fare);
        // ETA: Avg speed 40km/h = 1.5 min per km
        double etaMinutes = Math.round(distance * 1.5 * 10.0) / 10.0;
        response.put("etaMinutes", etaMinutes);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/passenger/travel-history")
    public ResponseEntity<List<com.sunilskyros.payanam.data.dto.TravelHistory>> getTravelHistory(HttpSession session) {
        Passenger passenger = (Passenger) session.getAttribute("user");
        if (passenger == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(homeModel.getPassengerTravelHistory(passenger.getPhoneNumber()));
    }
}
