package com.ffood.g1.endpoint;

import com.ffood.g1.entity.Feedback;
import com.ffood.g1.entity.Role;
import com.ffood.g1.entity.User;
import com.ffood.g1.enum_pay.FeedbackStatus;
import com.ffood.g1.repository.FeedbackRepository;
import com.ffood.g1.repository.RoleRepository;
import com.ffood.g1.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@AutoConfigureMockMvc
public class FeedBackEndpointTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private FeedbackRepository feedbackRepository;

    private User user;
    private Role managerRole;
    private Integer canteenId = 2;
    private Integer existingFeedbackId1 = 21;  // ID tồn tại trong cơ sở dữ liệu
    private Integer existingFeedbackId2 = 22;  // ID tồn tại trong cơ sở dữ liệu

    @BeforeEach
    @Transactional  // Đảm bảo rằng phương thức setup() được thực hiện trong phạm vi một transaction
    public void setup() {
        // Lấy Role đã tồn tại trong cơ sở dữ liệu
        managerRole = roleRepository.findById(3)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        // Lấy User đã tồn tại trong cơ sở dữ liệu
        user = userRepository.findByEmail("doduyminh178@gmail.com");

        // Kiểm tra xem vai trò đã được gán đúng chưa (nếu cần)
        if (!user.getRole().getRoleId().equals(managerRole.getRoleId())) {
            throw new RuntimeException("User does not have the correct role");
        }
        if (!user.getCanteen().getCanteenId().equals(canteenId)) {
            throw new RuntimeException("User does not belong to the correct canteen");
        }

        // Đặt trạng thái của các phản hồi về PENDING
        Feedback feedback1 = feedbackRepository.findById(existingFeedbackId1)
                .orElseThrow(() -> new RuntimeException("Feedback not found"));
        feedback1.setFeedbackStatus(FeedbackStatus.PENDING);
        feedbackRepository.save(feedback1);

        Feedback feedback2 = feedbackRepository.findById(existingFeedbackId2)
                .orElseThrow(() -> new RuntimeException("Feedback not found"));
        feedback2.setFeedbackStatus(FeedbackStatus.PENDING);
        feedbackRepository.save(feedback2);
    }

    @Test
    @WithUserDetails("doduyminh178@gmail.com")
    public void testGetFeedbacks_withRealUser() throws Exception {
        mockMvc.perform(get("/manage-feedback")
                        .param("canteenId", String.valueOf(canteenId))
                        .param("status", "PENDING"))
                .andExpect(status().isOk())
                .andExpect(view().name("staff-management/feedback-list"));
    }

    @Test
    @WithUserDetails("doduyminh178@gmail.com")
    public void testApproveFeedback() throws Exception {
        // Kiểm tra trạng thái ban đầu của phản hồi là PENDING
        Feedback feedback = feedbackRepository.findById(existingFeedbackId1)
                .orElseThrow(() -> new RuntimeException("Feedback not found"));
        assertEquals(FeedbackStatus.PENDING, feedback.getFeedbackStatus());

        // Thực hiện phê duyệt phản hồi
        mockMvc.perform(post("/approve-feedback/" + existingFeedbackId1)
                        .param("canteenId", String.valueOf(canteenId)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/manage-feedback?canteenId=" + canteenId))
                .andExpect(flash().attributeExists("message"));

        // Kiểm tra trạng thái của phản hồi sau khi phê duyệt là COMPLETE
        Feedback updatedFeedback = feedbackRepository.findById(existingFeedbackId1)
                .orElseThrow(() -> new RuntimeException("Feedback not found"));
        assertEquals(FeedbackStatus.COMPLETE, updatedFeedback.getFeedbackStatus());
    }

    @Test
    @WithUserDetails("doduyminh178@gmail.com")
    public void testBulkApproveFeedback() throws Exception {
        // Kiểm tra trạng thái ban đầu của phản hồi là PENDING
        Feedback feedback1 = feedbackRepository.findById(existingFeedbackId1)
                .orElseThrow(() -> new RuntimeException("Feedback not found"));
        Feedback feedback2 = feedbackRepository.findById(existingFeedbackId2)
                .orElseThrow(() -> new RuntimeException("Feedback not found"));
        assertEquals(FeedbackStatus.PENDING, feedback1.getFeedbackStatus());
        assertEquals(FeedbackStatus.PENDING, feedback2.getFeedbackStatus());

        // Thực hiện phê duyệt phản hồi
        mockMvc.perform(post("/bulk-approve-feedback")
                        .param("canteenId", String.valueOf(canteenId))
                        .param("selectedFeedbacks", existingFeedbackId1.toString(), existingFeedbackId2.toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/manage-feedback?canteenId=" + canteenId + "&status=PENDING"))
                .andExpect(flash().attributeExists("message"));

        // Kiểm tra trạng thái của phản hồi sau khi phê duyệt là COMPLETE
        Feedback updatedFeedback1 = feedbackRepository.findById(existingFeedbackId1)
                .orElseThrow(() -> new RuntimeException("Feedback not found"));
        Feedback updatedFeedback2 = feedbackRepository.findById(existingFeedbackId2)
                .orElseThrow(() -> new RuntimeException("Feedback not found"));
        assertEquals(FeedbackStatus.COMPLETE, updatedFeedback1.getFeedbackStatus());
        assertEquals(FeedbackStatus.COMPLETE, updatedFeedback2.getFeedbackStatus());
    }

    @Test
    @WithUserDetails("doduyminh178@gmail.com")
    public void testRejectFeedback() throws Exception {
        // Kiểm tra trạng thái ban đầu của phản hồi là PENDING
        Feedback feedback = feedbackRepository.findById(existingFeedbackId1)
                .orElseThrow(() -> new RuntimeException("Feedback not found"));
        assertEquals(FeedbackStatus.PENDING, feedback.getFeedbackStatus());

        // Thực hiện từ chối phản hồi
        mockMvc.perform(post("/reject-feedback/" + existingFeedbackId1)
                        .param("canteenId", String.valueOf(canteenId)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/manage-feedback?canteenId=" + canteenId))
                .andExpect(flash().attributeExists("message"));

        // Kiểm tra trạng thái của phản hồi sau khi từ chối là REJECT
        Feedback updatedFeedback = feedbackRepository.findById(existingFeedbackId1)
                .orElseThrow(() -> new RuntimeException("Feedback not found"));
        assertEquals(FeedbackStatus.REJECT, updatedFeedback.getFeedbackStatus());
    }

    @Test
    @WithUserDetails("doduyminh178@gmail.com")
    public void testBulkRejectFeedback() throws Exception {
        // Kiểm tra trạng thái ban đầu của phản hồi là PENDING
        Feedback feedback1 = feedbackRepository.findById(existingFeedbackId1)
                .orElseThrow(() -> new RuntimeException("Feedback not found"));
        Feedback feedback2 = feedbackRepository.findById(existingFeedbackId2)
                .orElseThrow(() -> new RuntimeException("Feedback not found"));
        assertEquals(FeedbackStatus.PENDING, feedback1.getFeedbackStatus());
        assertEquals(FeedbackStatus.PENDING, feedback2.getFeedbackStatus());

        // Thực hiện từ chối phản hồi
        mockMvc.perform(post("/bulk-reject-feedback")
                        .param("canteenId", String.valueOf(canteenId))
                        .param("selectedFeedbacks", existingFeedbackId1.toString(), existingFeedbackId2.toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/manage-feedback?canteenId=" + canteenId + "&status=PENDING"))
                .andExpect(flash().attributeExists("message"));

        // Kiểm tra trạng thái của phản hồi sau khi từ chối là REJECT
        Feedback updatedFeedback1 = feedbackRepository.findById(existingFeedbackId1)
                .orElseThrow(() -> new RuntimeException("Feedback not found"));
        Feedback updatedFeedback2 = feedbackRepository.findById(existingFeedbackId2)
                .orElseThrow(() -> new RuntimeException("Feedback not found"));
        assertEquals(FeedbackStatus.REJECT, updatedFeedback1.getFeedbackStatus());
        assertEquals(FeedbackStatus.REJECT, updatedFeedback2.getFeedbackStatus());
    }

    @Test
    @WithUserDetails("doduyminh178@gmail.com")
    public void testBulkApproveFeedback_noSelection() throws Exception {
        mockMvc.perform(post("/bulk-approve-feedback")
                        .param("canteenId", String.valueOf(canteenId)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/manage-feedback?canteenId=" + canteenId + "&status=PENDING"))
                .andExpect(flash().attributeExists("error"));
    }

    @Test
    @WithUserDetails("doduyminh178@gmail.com")
    public void testBulkRejectFeedback_noSelection() throws Exception {
        mockMvc.perform(post("/bulk-reject-feedback")
                        .param("canteenId", String.valueOf(canteenId)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/manage-feedback?canteenId=" + canteenId + "&status=PENDING"))
                .andExpect(flash().attributeExists("error"));
    }
}
