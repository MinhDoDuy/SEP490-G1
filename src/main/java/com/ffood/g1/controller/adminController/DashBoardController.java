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
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
                                @RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Optional<LocalDate> startDate,
                                @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Optional<LocalDate> endDate,
                                Model model) {
        Integer userCount = userService.countUsers();
        Integer canteenCount = canteenService.countCanteens();
        Pageable pageable = PageRequest.of(page, size);
        Page<Feedback> feedbacks;

        if ((startDate.isPresent() && !endDate.isPresent()) || (!startDate.isPresent() && endDate.isPresent())) {
            model.addAttribute("errorMessage", "Vui lòng nhập cả ngày bắt đầu và ngày kết thúc.");
            feedbacks = feedbackService.findByStatus(status, pageable);
        } else if (!startDate.isPresent() && !endDate.isPresent()) {
            feedbacks = feedbackService.findByStatus(status, pageable);
        } else {
            LocalDateTime start = startDate.orElse(LocalDate.MIN).atStartOfDay();
            LocalDateTime end = endDate.orElse(LocalDate.MAX).atTime(LocalTime.MAX);

            try {
                feedbacks = feedbackService.findByStatusAndDateRange(status, start, end, pageable);
            } catch (IllegalArgumentException e) {
                model.addAttribute("errorMessage", e.getMessage());
                feedbacks = Page.empty();
            }
        }

        model.addAttribute("feedbacks", feedbacks);
        model.addAttribute("status", status);
        model.addAttribute("userCount", userCount);
        model.addAttribute("canteenCount", canteenCount);
        model.addAttribute("startDate", startDate.orElse(null));
        model.addAttribute("endDate", endDate.orElse(null));

        return "admin-management/dashboard";
    }


}
