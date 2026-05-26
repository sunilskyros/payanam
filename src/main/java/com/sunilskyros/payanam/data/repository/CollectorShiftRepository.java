package com.sunilskyros.payanam.data.repository;

import com.sunilskyros.payanam.data.dto.CollectorShift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CollectorShiftRepository extends JpaRepository<CollectorShift, Long> {
    List<CollectorShift> findByCollectorPhoneOrderByIdDesc(String collectorPhone);
    Optional<CollectorShift> findFirstByCollectorPhoneAndStatusOrderByIdDesc(String collectorPhone, String status);
    List<CollectorShift> findAllByOrderByIdDesc();
}
