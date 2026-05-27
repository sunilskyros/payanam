package com.sunilskyros.payanam.util;

import com.sunilskyros.payanam.data.dto.Passenger;
import com.sunilskyros.payanam.data.repository.PassengerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DatabaseSeeder implements CommandLineRunner {

    private final PassengerRepository passengerRepository;

    public DatabaseSeeder(PassengerRepository passengerRepository) {
        this.passengerRepository = passengerRepository;
    }

    @Override
    public void run(String... args) throws Exception {
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
    }
}
