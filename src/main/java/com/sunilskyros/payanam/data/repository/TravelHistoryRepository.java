package com.sunilskyros.payanam.data.repository;

import com.sunilskyros.payanam.data.dto.TravelHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TravelHistoryRepository extends JpaRepository<TravelHistory, Long> {
    List<TravelHistory> findByPassengerPhoneOrderByTravelDateDesc(String passengerPhone);
}
