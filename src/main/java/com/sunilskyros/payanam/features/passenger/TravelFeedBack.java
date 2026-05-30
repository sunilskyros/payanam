package com.sunilskyros.payanam.features.passenger;

import com.sunilskyros.payanam.data.dto.FeedBack;
import com.sunilskyros.payanam.data.repository.FeedbackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TravelFeedBack {
    private final FeedbackRepository feedbackRepository;

    @Autowired
    public TravelFeedBack(FeedbackRepository feedbackRepository) {
        this.feedbackRepository = feedbackRepository;
    }

    public void saveFeedback(String phoneNumber, Long busId, int rating, String comments) {
        FeedBack fb = new FeedBack();
        fb.setPhoneNumber(phoneNumber);
        fb.setBusId(busId);
        fb.setRating(rating);
        fb.setFeedback(comments);
        fb.setTime(LocalDateTime.now());
        feedbackRepository.save(fb);
    }
    public List<FeedBack> getAllFeedback() {
        return feedbackRepository.findAll();
    }
}
