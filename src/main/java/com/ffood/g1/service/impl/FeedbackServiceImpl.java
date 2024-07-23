package com.ffood.g1.service.impl;

import com.ffood.g1.entity.Feedback;
import com.ffood.g1.entity.User;
import com.ffood.g1.repository.FeedbackRepository;
import com.ffood.g1.service.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.ffood.g1.enum_pay.FeedbackStatus;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class FeedbackServiceImpl implements FeedbackService {

    @Autowired
    private FeedbackRepository feedbackRepository;

    public Feedback createFeedback(User user, String comment, Integer foodId, Integer canteenId) {
        Feedback feedback = Feedback.builder()
                .user(user)
                .comment(comment)
                .timeCreated(LocalDateTime.now())
                .foodId(foodId)
                .canteenId(canteenId)
                .feedbackStatus(FeedbackStatus.PENDING)
                .build();
        return feedbackRepository.save(feedback);
    }

    public List<Feedback> getFeedbacksByCanteenIdAndStatus(Integer canteenId, FeedbackStatus status) {
        return feedbackRepository.findFeedbackByCanteenIdAndStatus(canteenId, status);
    }

    @Override
    public List<Feedback> getFeedbacksByFoodIdAndStatus(Integer foodId, FeedbackStatus feedbackStatus) {
        return feedbackRepository.findFeedbackByFoodIdAndStatus(foodId, feedbackStatus);
    }

    @Override
    public Page<Feedback> getFeedbacksByCanteen(Integer canteenId, FeedbackStatus status, Pageable pageable) {
        return feedbackRepository.findByCanteenIdAndStatus(canteenId, status, pageable);
    }

    @Override
    public void updateFeedbackStatus(Integer feedbackId, FeedbackStatus status) {
        Feedback feedback = feedbackRepository.findById(feedbackId).orElseThrow(() -> new IllegalArgumentException("Invalid feedback ID"));
        feedback.setFeedbackStatus(status);
        feedbackRepository.save(feedback);
    }

}
