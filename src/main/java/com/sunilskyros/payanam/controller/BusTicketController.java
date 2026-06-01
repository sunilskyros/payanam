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
import com.sunilskyros.payanam.data.dto.LiveLocationUpdate;
import com.sunilskyros.payanam.features.realtime.service.RealTimeLocationService;
import com.sunilskyros.payanam.features.passenger.TravelFeedBack;
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
    private final TravelFeedBack travelFeedBack;
    private final RealTimeLocationService realTimeLocationService;

    @Autowired
    public BusTicketController(HomeModel homeModel, 
                               UpdateStopModel updateStopModel, 
                               ValidateTicketModel validateTicketModel,
                               CollectorShiftRepository collectorShiftRepository,
                               PassengerRepository passengerRepository,
                               TicketRepository ticketRepository,
                               BusRepository busRepository,
                               TravelFeedBack travelFeedBack,
                               RealTimeLocationService realTimeLocationService) {
        this.homeModel = homeModel;
        this.updateStopModel = updateStopModel;
        this.validateTicketModel = validateTicketModel;
        this.collectorShiftRepository = collectorShiftRepository;
        this.passengerRepository = passengerRepository;
        this.ticketRepository = ticketRepository;
        this.busRepository = busRepository;
        this.travelFeedBack = travelFeedBack;
        this.realTimeLocationService = realTimeLocationService;
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
        
        if (saved != null) {
            realTimeLocationService.broadcastAdminNotification("TICKET_BOOKED");
            return ResponseEntity.ok("Ticket booked! ID: " + saved.getTicketId() + " | Price: Rs " + saved.getPrice());
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Booking failed. Please ensure source and destination are valid.");
    }

    @GetMapping("/tickets/my")
    public ResponseEntity<List<Ticket>> getMyTickets(HttpSession session) {
        Passenger user = (Passenger) session.getAttribute("user");
        if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        
        return ResponseEntity.ok(homeModel.getPassengerTickets(user.getPhoneNumber()));
    }

    @PostMapping("/tickets/cancel")
    public ResponseEntity<String> cancelTicket(@RequestParam int ticketId, HttpSession session) {
        Passenger user = (Passenger) session.getAttribute("user");
        if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Login required");

        Ticket ticket = ticketRepository.findById(ticketId).orElse(null);
        if (ticket == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Ticket not found");

        if (user.getRole() == Passenger.Role.PASSENGER && !ticket.getPassengerPhoneNumber().equals(user.getPhoneNumber())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
        }

        ticket.setIsValid(false);
        ticketRepository.save(ticket);

        realTimeLocationService.broadcastAdminNotification("TICKET_CANCELLED");
        return ResponseEntity.ok("Ticket successfully cancelled");
    }

    @GetMapping("/tickets/verify")
    public ResponseEntity<?> verifyTicketPublic(@RequestParam String ticketId, 
                                                @RequestParam String sig) {
        Ticket ticket = null;
        try {
            int id = Integer.parseInt(ticketId);
            ticket = ticketRepository.findById(id).orElse(null);
        } catch (NumberFormatException e) {
            ticket = ticketRepository.findByBookingReference(ticketId).orElse(null);
        }
        
        if (ticket == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Ticket not found");
        }

        if (!sig.equals(ticket.getSignature())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid ticket signature! Authenticity check failed.");
        }

        Map<String, Object> response = new HashMap<>();
        response.put("ticketId", ticket.getTicketId());
        response.put("passengerPhoneNumber", ticket.getPassengerPhoneNumber());
        response.put("busId", ticket.getBusId());
        response.put("busName", ticket.getBusName());
        response.put("sourceStop", ticket.getSourceStop());
        response.put("destinationStop", ticket.getDestinationStop());
        response.put("price", ticket.getPrice());
        response.put("boughtTime", ticket.getBoughtTime());
        response.put("validUntil", ticket.getValidUntil());
        response.put("isValid", ticket.getIsValid());
        response.put("bookingReference", ticket.getBookingReference());
        
        return ResponseEntity.ok(response);
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

    @PostMapping("/passenger/feedback")
    public ResponseEntity<String> submitFeedback(@RequestParam Long busId,
                                                 @RequestParam int rating,
                                                 @RequestParam String comments,
                                                 HttpSession session) {
        Passenger user = (Passenger) session.getAttribute("user");
        if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Login required");

        travelFeedBack.saveFeedback(user.getPhoneNumber(), busId, rating, comments);
        return ResponseEntity.ok("Feedback submitted successfully!");
    }

    @GetMapping("/admin/feedback")
    public ResponseEntity<List<com.sunilskyros.payanam.data.dto.FeedBack>> getFeedback(HttpSession session) {
        Passenger user = (Passenger) session.getAttribute("user");
        if (user == null || user.getRole() != Passenger.Role.ADMIN) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(travelFeedBack.getAllFeedback());
    }

    // ---------------- TICKET COLLECTOR ----------------
    
    @PostMapping("/collector/selectBus")
    public ResponseEntity<String> selectBus(@RequestParam int busId, HttpSession session) {
        Passenger user = (Passenger) session.getAttribute("user");
        if (user == null || user.getRole() != Passenger.Role.TICKETCOLLECTOR) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access Denied");
        }
        
        // Automatically close any existing ACTIVE shift for this collector in the database
        collectorShiftRepository.findFirstByCollectorPhoneAndStatusOrderByIdDesc(user.getPhoneNumber(), "ACTIVE")
                .ifPresent(oldShift -> {
                    oldShift.setEndTime(LocalTime.now().toString().substring(0, 5));
                    oldShift.setStatus("COMPLETED");
                    collectorShiftRepository.save(oldShift);
                });
        session.removeAttribute("assignedBusId");
        
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

        realTimeLocationService.broadcastAdminNotification("SHIFT_UPDATED");
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

        realTimeLocationService.broadcastAdminNotification("SHIFT_UPDATED");
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
            
            // Dispatch live WebSocket sync update for reversed route
            LiveLocationUpdate reverseUpdate = new LiveLocationUpdate();
            reverseUpdate.setBusId(busId);
            reverseUpdate.setBusName(bus.getName());
            reverseUpdate.setStops(reversedStops);
            reverseUpdate.setCurrentStopSeq(1);
            reverseUpdate.setCurrentStopName(reversedStops.get(0).getStopName());
            reverseUpdate.setBusStatus("ACTIVE");
            realTimeLocationService.processLocationUpdate(reverseUpdate, user.getPhoneNumber());
            
            return ResponseEntity.ok("ROUTE_REVERSED");
        }
        
        // Dispatch live WebSocket sync update for standard stop progress
        LiveLocationUpdate update = new LiveLocationUpdate();
        update.setBusId(busId);
        update.setBusName(bus.getName());
        update.setStops(stops);
        update.setCurrentStopSeq(stopSeq);
        update.setBusStatus("ACTIVE");
        for (Stop stop : stops) {
            if (stop.getId() == stopSeq) {
                update.setCurrentStopName(stop.getStopName());
                break;
            }
        }
        realTimeLocationService.processLocationUpdate(update, user.getPhoneNumber());
        
        return ResponseEntity.ok("Location updated");
    }

    @PostMapping("/collector/validateTicket")
    public ResponseEntity<String> validateTicket(@RequestParam String ticketId, 
                                                 @RequestParam(required = false) String signature, 
                                                 HttpSession session) {
        Passenger user = (Passenger) session.getAttribute("user");
        if (user == null || user.getRole() != Passenger.Role.TICKETCOLLECTOR) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access Denied");
        }

        Ticket ticket = null;
        try {
            int id = Integer.parseInt(ticketId);
            ticket = validateTicketModel.getTicketById(id);
        } catch (NumberFormatException e) {
            ticket = ticketRepository.findByBookingReference(ticketId).orElse(null);
        }
        
        if (ticket == null) return ResponseEntity.badRequest().body("Ticket not found");
        
        // Cryptographic verification against client ticket forgery
        if (signature != null && !signature.isEmpty() && !signature.equals(ticket.getSignature())) {
            return ResponseEntity.badRequest().body("Invalid ticket signature! Tampering detected.");
        }
        
        if (!ticket.getIsValid()) return ResponseEntity.ok("Ticket is already invalid or used.");

        ticket.setIsValid(false);
        validateTicketModel.updateTicket(ticket);

        // Increment ticket checked count in current active shift
        collectorShiftRepository.findFirstByCollectorPhoneAndStatusOrderByIdDesc(user.getPhoneNumber(), "ACTIVE")
                .ifPresent(shift -> {
                    shift.setTicketsChecked(shift.getTicketsChecked() + 1);
                    collectorShiftRepository.save(shift);
                });

        realTimeLocationService.broadcastAdminNotification("TICKET_VALIDATED");
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
        
        List<Ticket> allTickets = ticketRepository.findAll();
        int revenue = allTickets.stream().mapToInt(Ticket::getPrice).sum();
        
        java.time.LocalDateTime startOfToday = java.time.LocalDate.now().atStartOfDay();
        java.time.LocalDateTime startOfMonth = java.time.LocalDate.now().withDayOfMonth(1).atStartOfDay();
        
        int dailyRevenue = allTickets.stream()
                .filter(t -> t.getBoughtTime() != null && !t.getBoughtTime().isBefore(startOfToday))
                .mapToInt(Ticket::getPrice)
                .sum();
                
        int monthlyRevenue = allTickets.stream()
                .filter(t -> t.getBoughtTime() != null && !t.getBoughtTime().isBefore(startOfMonth))
                .mapToInt(Ticket::getPrice)
                .sum();
        
        long collectorsCount = passengerRepository.countByRole(Passenger.Role.TICKETCOLLECTOR);

        Map<String, Object> overview = new HashMap<>();
        overview.put("passengers", passengers);
        overview.put("buses", activeBuses);
        overview.put("tickets", tickets);
        overview.put("revenue", revenue);
        overview.put("dailyRevenue", dailyRevenue);
        overview.put("monthlyRevenue", monthlyRevenue);
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
}
