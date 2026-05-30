package com.sunilskyros.payanam.data.repository;

import com.sunilskyros.payanam.data.dto.FeedBack;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedbackRepository extends JpaRepository<FeedBack, Long> {
}
