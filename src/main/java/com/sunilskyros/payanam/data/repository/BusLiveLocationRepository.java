package com.sunilskyros.payanam.data.repository;

import com.sunilskyros.payanam.data.dto.BusLiveLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BusLiveLocationRepository extends JpaRepository<BusLiveLocation, Integer> {
}
