package com.ffood.g1.controller.adminController;

import com.ffood.g1.entity.Feedback;
import com.ffood.g1.enum_pay.FeedbackStatus;
import com.ffood.g1.service.CanteenService;
import com.ffood.g1.service.FeedbackService;
import com.ffood.g1.service.OrderService;
import com.ffood.g1.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

@Controller
public class DashBoardController {

    @Autowired
    private UserService userService;

    @Autowired
    private CanteenService canteenService;

    @Autowired
    private FeedbackService feedbackService;

    @GetMapping("/dashboard-admin")
    public String showDashboard(@RequestParam(value = "status", defaultValue = "VIEWABLE") FeedbackStatus status,
                                @RequestParam(value = "page", defaultValue = "0") int page,
                                @RequestParam(value = "size", defaultValue = "10") int size,
                                Model model) {
        Integer userCount = userService.countUsers();
        Integer canteenCount = canteenService.countCanteens();
        Pageable pageable = PageRequest.of(page, size);
        Page<Feedback> feedbacks = feedbackService.findByStatus(status, pageable);

        model.addAttribute("feedbacks", feedbacks);
        model.addAttribute("status", status);
        model.addAttribute("userCount", userCount);
        model.addAttribute("canteenCount", canteenCount);

        return "admin-management/dashboard";
    }
}
