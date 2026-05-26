package com.sunilskyros.payanam.data.repository;

import com.sunilskyros.payanam.data.dto.Bus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BusRepository extends JpaRepository<Bus, Integer> {
}
