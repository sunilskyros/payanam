package com.sunilskyros.payanam.util;

import com.sunilskyros.payanam.data.dto.Passenger;
import com.sunilskyros.payanam.data.dto.Bus;
import com.sunilskyros.payanam.data.dto.Stop;
import com.sunilskyros.payanam.data.dto.BusLiveLocation;
import com.sunilskyros.payanam.data.repository.PassengerRepository;
import com.sunilskyros.payanam.data.repository.BusRepository;
import com.sunilskyros.payanam.data.repository.StopRepository;
import com.sunilskyros.payanam.data.repository.BusLiveLocationRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Component
public class DatabaseSeeder implements CommandLineRunner {

    private final PassengerRepository passengerRepository;
    private final BusRepository busRepository;
    private final StopRepository stopRepository;
    private final BusLiveLocationRepository busLiveLocationRepository;

    public DatabaseSeeder(PassengerRepository passengerRepository,
                          BusRepository busRepository,
                          StopRepository stopRepository,
                          BusLiveLocationRepository busLiveLocationRepository) {
        this.passengerRepository = passengerRepository;
        this.busRepository = busRepository;
        this.stopRepository = stopRepository;
        this.busLiveLocationRepository = busLiveLocationRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // Seed default Admin if empty
        if (passengerRepository.count() == 0) {
            Passenger admin = new Passenger();
            admin.setPhoneNumber("7604859072");
            admin.setName("Admin");
            admin.setPassword(PasswordUtil.hash("Admin@123"));
            admin.setRole(Passenger.Role.ADMIN);
            admin.setStatus(Passenger.Status.ACTIVE);
            passengerRepository.save(admin);
            System.out.println("\n>>> Database Seeded: Default Admin created successfully!");
            System.out.println(">>> Use Phone: 7604859072 | Password: Admin@123 to log in as Admin.\n");
        }

        // Seed default Bus if empty
        if (busRepository.count() == 0) {
            Bus bus = new Bus();
            bus.setId(101);
            bus.setName("Vellore Fort Line");
            busRepository.save(bus);

            // Seed sequenced coordinate stops
            seedStop(101, 1, "Vellore Fort", 12.9230, 79.1300, true);
            seedStop(101, 2, "CMC Hospital", 12.9272, 79.1348, false);
            seedStop(101, 3, "Vellore Green Circle", 12.9515, 79.1415, false);
            seedStop(101, 4, "Katpadi Junction", 12.9681, 79.1326, false);
            seedStop(101, 5, "Vellore Tech (VIT)", 12.9692, 79.1559, false);

            System.out.println(">>> Database Seeded: Seeded Vellore Fort Line 101 with 5 dynamic stops!");

            // Seed initial Live position
            BusLiveLocation liveLoc = new BusLiveLocation();
            liveLoc.setBusId(101);
            liveLoc.setLatitude(12.9230); // Started at Vellore Fort
            liveLoc.setLongitude(79.1300);
            liveLoc.setSpeed(0.0);
            liveLoc.setBearing(0.0);
            liveLoc.setLastUpdated(LocalDateTime.now());
            busLiveLocationRepository.save(liveLoc);
        }
    }

    private void seedStop(int busId, int seq, String stopName, double lat, double lon, boolean current) {
        Stop stop = new Stop();
        stop.setBusId(busId);
        stop.setId(seq);
        stop.setSequenceNumber(seq);
        stop.setStopName(stopName);
        stop.setLatitude(lat);
        stop.setLongitude(lon);
        stop.setCurrentStop(current);
        stop.setUpdatedTime(LocalTime.now());
        stopRepository.save(stop);
    }
}
