package com.sunilskyros.payanam.data.repository;

import com.sunilskyros.payanam.data.dto.Stop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface StopRepository extends JpaRepository<Stop, Integer> {
    void deleteByBusId(int busId);
    List<Stop> findByBusIdOrderByIdAsc(int busId);
    List<Stop> findByStopNameContainingIgnoreCase(String stopName);
}
