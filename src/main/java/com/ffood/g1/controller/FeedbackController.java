package com.ffood.g1.controller;

import com.ffood.g1.entity.Feedback;
import com.ffood.g1.entity.User;
import com.ffood.g1.service.FeedbackService;
import com.ffood.g1.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class FeedbackController {
    @Autowired
    private FeedbackService feedbackService;

    @Autowired
    private UserService userService;

    @PostMapping("/create_new_feedback_canteen")
    public String createFeedbackCanteen(@RequestParam String comment,  @RequestParam Integer canteenId, Model model)
    {Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
       Integer foodId=null;
        String email = authentication.getName();
        User user = userService.findByEmail(email);
        Feedback feedback = feedbackService.createFeedback(user, comment, foodId, canteenId);
        return "redirect:/canteen_info?canteenId=" + canteenId;
    }

    @PostMapping("/create_new_feedback_food")
    public String createFeedbackFood(@RequestParam String comment,  @RequestParam Integer canteenId,@RequestParam Integer foodId, Model model)
    {Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userService.findByEmail(email);
        Feedback feedback = feedbackService.createFeedback(user, comment, foodId, canteenId);
        return "redirect:/food_details?id=" + foodId;
    }

}
