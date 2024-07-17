package com.ffood.g1.entity;

import com.ffood.g1.enum_pay.FeedbackStatus;
import com.ffood.g1.enum_pay.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "feedback")
public class Feedback {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "feedback_id")
    private Integer feedbackId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "comment", nullable = false, length = 500)
    private String comment;

    @Column(name = "time_created", nullable = false)
    private LocalDateTime timeCreated;

    @Column(name = "food_id")
    private Integer foodId;;

    @Column(name = "canteen_id")
    private Integer canteenId;

    @Enumerated(EnumType.STRING)
    @Column(name = "feedback_status")
    private FeedbackStatus feedbackStatus;

}
