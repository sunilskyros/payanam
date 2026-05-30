package com.sunilskyros.payanam.data.repository;

import com.sunilskyros.payanam.data.dto.BusLocationHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BusLocationHistoryRepository extends JpaRepository<BusLocationHistory, Long> {
    List<BusLocationHistory> findByBusIdOrderByTimestampDesc(int busId);
}
