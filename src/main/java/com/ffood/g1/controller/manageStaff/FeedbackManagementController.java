package com.ffood.g1.controller.manageStaff;

import com.ffood.g1.entity.Feedback;
import com.ffood.g1.entity.User;
import com.ffood.g1.enum_pay.FeedbackStatus;
import com.ffood.g1.service.FeedbackService;
import com.ffood.g1.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
public class FeedbackManagementController {

    @Autowired
    private FeedbackService feedbackService;

    @Autowired
    private UserService userService;

    @GetMapping("/manage-feedback")
    public String getFeedbacks(@RequestParam Integer canteenId,
                               @RequestParam(value = "status", defaultValue = "PENDING") FeedbackStatus status,
                               @RequestParam(value = "page", defaultValue = "0") int page,
                               @RequestParam(value = "size", defaultValue = "10") int size,
                               Model model, Principal principal, RedirectAttributes redirectAttributes) {

        // Lấy thông tin người dùng hiện tại từ Principal
        User currentUser = userService.findByEmail(principal.getName());

        // Kiểm tra xem `canteenId` có khớp với `canteenId` của người dùng hiện tại
        if (!currentUser.getCanteen().getCanteenId().equals(canteenId)) {
            redirectAttributes.addFlashAttribute("error", "Bạn không có quyền truy cập phản hồi của canteen khác.");
            return "redirect:/manage-feedback?canteenId=" + currentUser.getCanteen().getCanteenId();
        }

        // Ensure status is not null
        if (status == null) {
            status = FeedbackStatus.PENDING; // Set a default value or handle appropriately
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<Feedback> feedbacks;

        // Sử dụng switch-case để xử lý các trạng thái khác nhau
        switch (status) {
            case PENDING:
                feedbacks = feedbackService.getFeedbacksByCanteen(canteenId, FeedbackStatus.PENDING, pageable);
                break;
            case COMPLETE:
                feedbacks = feedbackService.getFeedbacksByCanteen(canteenId, FeedbackStatus.COMPLETE, pageable);
                break;
            case REJECT:
                feedbacks = feedbackService.getFeedbacksByCanteen(canteenId, FeedbackStatus.REJECT, pageable);
                break;
            default:
                feedbacks = Page.empty(); // Trường hợp mặc định nếu trạng thái không xác định
                model.addAttribute("error", "Trạng thái phản hồi không hợp lệ.");
                break;
        }

        model.addAttribute("feedbacks", feedbacks);
        model.addAttribute("canteenId", canteenId);
        model.addAttribute("status", status);

        return "staff-management/feedback-list";
    }


    @PostMapping("/approve-feedback/{feedbackId}")
    public String approveFeedback(@PathVariable Integer feedbackId,
                                  @RequestParam Integer canteenId,
                                  RedirectAttributes redirectAttributes) {
        feedbackService.updateFeedbackStatus(feedbackId, FeedbackStatus.COMPLETE);
        redirectAttributes.addFlashAttribute("message", "Phản hồi phê duyệt thành công.");
        return "redirect:/manage-feedback?canteenId=" + canteenId;
    }

    @PostMapping("/reject-feedback/{feedbackId}")
    public String rejectFeedback(@PathVariable Integer feedbackId,
                                 @RequestParam Integer canteenId,
                                 RedirectAttributes redirectAttributes) {
        feedbackService.updateFeedbackStatus(feedbackId, FeedbackStatus.REJECT);
        redirectAttributes.addFlashAttribute("message", "Từ chối phản hồi.");
        return "redirect:/manage-feedback?canteenId=" + canteenId;
    }
}