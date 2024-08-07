package com.ffood.g1.controller.manageStaff;

import com.ffood.g1.entity.Canteen;
import com.ffood.g1.entity.Feedback;
import com.ffood.g1.enum_pay.FeedbackStatus;
import com.ffood.g1.service.CanteenService;
import com.ffood.g1.service.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class FeedbackManagementController {

    @Autowired
    private FeedbackService feedbackService;

    @Autowired
    private CanteenService canteenService;

    @GetMapping("/manage-feedback")
    public String getFeedbacks(@RequestParam Integer canteenId,
                               @RequestParam(value = "status", defaultValue = "PENDING") FeedbackStatus status,
                               @RequestParam(value = "page", defaultValue = "0") int page,
                               @RequestParam(value = "size", defaultValue = "10") int size,
                               Model model) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Feedback> feedbacks;

        switch (status) {
            case PENDING:
                feedbacks = feedbackService.getFeedbacksByCanteen(canteenId, FeedbackStatus.PENDING, pageable);
                break;
            case REJECT:
                feedbacks = feedbackService.getFeedbacksByCanteen(canteenId, FeedbackStatus.REJECT, pageable);
                break;
            default:
                feedbacks = Page.empty();
                break;
        }

        Canteen canteen = canteenService.getCanteenById(canteenId);
        model.addAttribute("canteenName", canteen.getCanteenName());
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
        redirectAttributes.addFlashAttribute("message", "Phản hồi phê duyệt thành công .");
        return "redirect:/manage-feedback?canteenId=" + canteenId;
    }

    @PostMapping("/reject-feedback/{feedbackId}")
    public String rejectFeedback(@PathVariable Integer feedbackId,
                                 @RequestParam Integer canteenId,
                                 RedirectAttributes redirectAttributes) {
        feedbackService.updateFeedbackStatus(feedbackId, FeedbackStatus.REJECT);
        redirectAttributes.addFlashAttribute("message", "Phản hồi bị từ chối.");
        return "redirect:/manage-feedback?canteenId=" + canteenId;
    }
}
