package com.ffood.g1.repository;


import com.ffood.g1.entity.Feedback;
import com.ffood.g1.enum_pay.FeedbackStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Integer> {

    @Query("SELECT f FROM Feedback f  WHERE f.canteenId = :canteenId AND f.feedbackStatus = :status ORDER BY f.timeCreated DESC")
    List<Feedback> findFeedbackByCanteenIdAndStatus(@Param("canteenId") Integer canteenId, @Param("status") FeedbackStatus status);

    @Query("SELECT f FROM Feedback f  WHERE f.foodId = :foodId AND f.feedbackStatus = :feedbackStatus ORDER BY f.timeCreated DESC")
    List<Feedback> findFeedbackByFoodIdAndStatus(@Param("foodId") Integer canteenId, @Param("feedbackStatus") FeedbackStatus feedbackStatus);

    @Query("SELECT f FROM Feedback f WHERE f.canteenId = :canteenId AND f.feedbackStatus = :status ORDER BY f.timeCreated DESC" )
    Page<Feedback> findByCanteenIdAndStatus(@Param("canteenId") Integer canteenId, @Param("status") FeedbackStatus status, Pageable pageable);

    @Query("SELECT f FROM Feedback f WHERE f.feedbackStatus = :status ORDER BY f.timeCreated DESC")
    Page<Feedback> findByStatus(@Param("status") FeedbackStatus status, Pageable pageable);

    @Query("SELECT f FROM Feedback f " +
            "WHERE f.feedbackStatus = :status " +
            "AND f.timeCreated BETWEEN :startDate AND :endDate")
    Page<Feedback> findByStatusAndDateRange(
            @Param("status") FeedbackStatus status,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable
    );

}