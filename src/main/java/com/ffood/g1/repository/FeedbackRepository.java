package com.ffood.g1.repository;


import com.ffood.g1.entity.Feedback;
import com.ffood.g1.enum_pay.FeedbackStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Integer> {

    @Query("SELECT f FROM Feedback f  WHERE f.canteenId = :canteenId AND f.feedbackStatus = :status")
    List<Feedback> findFeedbackByCanteenIdAndStatus(@Param("canteenId") Integer canteenId, @Param("status") FeedbackStatus status);

    @Query("SELECT f FROM Feedback f  WHERE f.foodId = :foodId AND f.feedbackStatus = :feedbackStatus")
    List<Feedback> findFeedbackByFoodIdAndStatus(@Param("foodId") Integer canteenId, @Param("feedbackStatus") FeedbackStatus feedbackStatus);

    @Query("SELECT f FROM Feedback f WHERE f.canteenId = :canteenId AND f.feedbackStatus = :status")
    Page<Feedback> findByCanteenIdAndStatus(@Param("canteenId") Integer canteenId, @Param("status") FeedbackStatus status, Pageable pageable);



}