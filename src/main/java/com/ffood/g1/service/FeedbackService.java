package com.ffood.g1.service;

import com.ffood.g1.entity.Feedback;
import com.ffood.g1.entity.User;
import com.ffood.g1.enum_pay.FeedbackStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface FeedbackService {
    Feedback createFeedback(User user, String comment, Integer foodId, Integer canteenId);

    List<Feedback> getFeedbacksByCanteenIdAndStatus(Integer canteenId, FeedbackStatus feedbackStatus);

    List<Feedback> getFeedbacksByFoodIdAndStatus(Integer foodId, FeedbackStatus feedbackStatus);

    Page<Feedback> getFeedbacksByCanteen(Integer canteenId, FeedbackStatus status, Pageable pageable);

    void updateFeedbackStatus(Integer feedbackId, FeedbackStatus status);

    void createFeedbackSystem(User user, String comment, Integer foodId, Integer canteenId, FeedbackStatus feedbackStatus);

    Page<Feedback> findByStatus(FeedbackStatus status, Pageable pageable);

    Page<Feedback> findByStatusAndDateRange(FeedbackStatus status, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
}
