package com.ffood.g1.controller;

import com.ffood.g1.entity.Feedback;
import com.ffood.g1.entity.User;
import com.ffood.g1.enum_pay.FeedbackStatus;
import com.ffood.g1.service.FeedbackService;
import com.ffood.g1.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

    @GetMapping("/feedback-system-form/{userId}")
    public String feedbackSystemForm(@PathVariable("userId") int userId, Model model) {
        User user = userService.getUserById(userId);
        if (user == null) {
            return "redirect:/login";
        }
        model.addAttribute("user", user);
        model.addAttribute("feedback", new Feedback()); // Tạo một đối tượng Feedback trống để truyền vào biểu mẫu
        return "feedback-system-form"; // Tên của template Thymeleaf cho biểu mẫu phản hồi hệ thống
    }

    @PostMapping("/submit-feedback/{userId}")
    public String submitFeedback(@PathVariable("userId") int userId, @RequestParam String comment, RedirectAttributes redirectAttributes) {
        User user = userService.getUserById(userId);
        if (user == null) {
            return "redirect:/login";
        }
        feedbackService.createFeedbackSystem(user, comment, null, null, FeedbackStatus.VIEWABLE);
        redirectAttributes.addFlashAttribute("successMessage", "Feedback submitted successfully!");
        return "redirect:/feedback-system-form/" + userId; // Điều hướng tới trang bạn muốn
    }
}
