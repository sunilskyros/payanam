package com.sunilskyros.payanam.data.repository;

import com.sunilskyros.payanam.data.dto.Passenger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PassengerRepository extends JpaRepository<Passenger, String> {
    long countByRole(Passenger.Role role);
}
