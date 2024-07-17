package com.ffood.g1.service;

import com.ffood.g1.entity.Feedback;
import com.ffood.g1.entity.User;
import com.ffood.g1.enum_pay.FeedbackStatus;

import java.util.List;

public interface FeedbackService {
    Feedback createFeedback(User user, String comment, Integer foodId, Integer canteenId);

    List<Feedback> getFeedbacksByCanteenIdAndStatus(Integer canteenId, FeedbackStatus feedbackStatus);

    List<Feedback> getFeedbacksByFoodIdAndStatus(Integer foodId, FeedbackStatus feedbackStatus);
}
