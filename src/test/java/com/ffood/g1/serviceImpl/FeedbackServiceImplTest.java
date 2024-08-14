package com.ffood.g1.serviceImpl;

import com.ffood.g1.entity.Feedback;
import com.ffood.g1.entity.User;
import com.ffood.g1.enum_pay.FeedbackStatus;
import com.ffood.g1.repository.FeedbackRepository;
import com.ffood.g1.service.impl.FeedbackServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FeedbackServiceImplTest {

    @InjectMocks
    private FeedbackServiceImpl feedbackService;

    @Mock
    private FeedbackRepository feedbackRepository;

    // Trường hợp bình thường: Kiểm thử tạo Feedback mới
    @Test
    void testCreateFeedback_Normal() {
        User user = new User(); // tạo User giả lập
        Feedback feedback = Feedback.builder()
                .user(user)
                .comment("Good food")
                .timeCreated(LocalDateTime.now())
                .foodId(1)
                .canteenId(1)
                .feedbackStatus(FeedbackStatus.PENDING)
                .build();

        when(feedbackRepository.save(any(Feedback.class))).thenReturn(feedback);

        Feedback result = feedbackService.createFeedback(user, "Good food", 1, 1);

        assertEquals(feedback, result);
        verify(feedbackRepository, times(1)).save(any(Feedback.class));
    }

    // Trường hợp bình thường: Kiểm thử lấy Feedback theo CanteenId và Status
    @Test
    void testGetFeedbacksByCanteenIdAndStatus_Normal() {
        Feedback feedback1 = Feedback.builder().canteenId(1).feedbackStatus(FeedbackStatus.VIEWABLE).build();
        Feedback feedback2 = Feedback.builder().canteenId(1).feedbackStatus(FeedbackStatus.VIEWABLE).build();

        when(feedbackRepository.findFeedbackByCanteenIdAndStatus(1, FeedbackStatus.VIEWABLE))
                .thenReturn(Arrays.asList(feedback1, feedback2));

        List<Feedback> result = feedbackService.getFeedbacksByCanteenIdAndStatus(1, FeedbackStatus.VIEWABLE);

        assertEquals(2, result.size());
        assertTrue(result.contains(feedback1));
        assertTrue(result.contains(feedback2));

        verify(feedbackRepository, times(1)).findFeedbackByCanteenIdAndStatus(1, FeedbackStatus.VIEWABLE);
    }

    // Trường hợp bình thường: Kiểm thử cập nhật trạng thái Feedback
    @Test
    void testUpdateFeedbackStatus_Normal() {
        Feedback feedback = Feedback.builder().feedbackId(1).feedbackStatus(FeedbackStatus.PENDING).build();

        when(feedbackRepository.findById(1)).thenReturn(Optional.of(feedback));

        feedbackService.updateFeedbackStatus(1, FeedbackStatus.VIEWABLE);

        assertEquals(FeedbackStatus.VIEWABLE, feedback.getFeedbackStatus());
        verify(feedbackRepository, times(1)).save(feedback);
    }

    // Trường hợp bình thường: Kiểm thử tìm kiếm Feedback theo trạng thái với phân trang
    @Test
    void testFindByStatus_Normal() {
        Pageable pageable = PageRequest.of(0, 10);

        Feedback feedback1 = Feedback.builder().feedbackId(1).feedbackStatus(FeedbackStatus.PENDING).build();
        Feedback feedback2 = Feedback.builder().feedbackId(2).feedbackStatus(FeedbackStatus.PENDING).build();

        Page<Feedback> page = new PageImpl<>(Arrays.asList(feedback1, feedback2), pageable, 2);

        when(feedbackRepository.findByStatus(FeedbackStatus.PENDING, pageable)).thenReturn(page);

        Page<Feedback> result = feedbackService.findByStatus(FeedbackStatus.PENDING, pageable);

        assertEquals(2, result.getTotalElements());
        assertEquals(2, result.getContent().size());

        verify(feedbackRepository, times(1)).findByStatus(FeedbackStatus.PENDING, pageable);
    }

    // Trường hợp bình thường: Kiểm thử tìm kiếm Feedback theo trạng thái và khoảng thời gian
    @Test
    void testFindByStatusAndDateRange_Normal() {
        Pageable pageable = PageRequest.of(0, 10);
        LocalDateTime startDate = LocalDateTime.now().minusDays(7);
        LocalDateTime endDate = LocalDateTime.now();

        Feedback feedback1 = Feedback.builder().feedbackId(1).feedbackStatus(FeedbackStatus.PENDING).timeCreated(startDate.plusDays(1)).build();
        Feedback feedback2 = Feedback.builder().feedbackId(2).feedbackStatus(FeedbackStatus.PENDING).timeCreated(startDate.plusDays(2)).build();

        Page<Feedback> page = new PageImpl<>(Arrays.asList(feedback1, feedback2), pageable, 2);

        when(feedbackRepository.findByStatusAndDateRange(FeedbackStatus.PENDING, startDate, endDate, pageable)).thenReturn(page);

        Page<Feedback> result = feedbackService.findByStatusAndDateRange(FeedbackStatus.PENDING, startDate, endDate, pageable);

        assertEquals(2, result.getTotalElements());
        assertEquals(2, result.getContent().size());

        verify(feedbackRepository, times(1)).findByStatusAndDateRange(FeedbackStatus.PENDING, startDate, endDate, pageable);
    }

    // Trường hợp không hợp lệ: Ngày bắt đầu sau ngày kết thúc
    @Test
    void testFindByStatusAndDateRange_InvalidDateRange() {
        Pageable pageable = PageRequest.of(0, 10);
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = LocalDateTime.now().minusDays(7);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                feedbackService.findByStatusAndDateRange(FeedbackStatus.PENDING, startDate, endDate, pageable)
        );

        assertEquals("Ngày bắt đầu không thể sau ngày kết thúc", exception.getMessage());
        verify(feedbackRepository, times(0)).findByStatusAndDateRange(any(), any(), any(), any());
    }
}
