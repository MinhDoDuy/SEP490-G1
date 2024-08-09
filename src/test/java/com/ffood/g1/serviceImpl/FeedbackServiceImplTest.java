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
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FeedbackServiceImplTest {

    @InjectMocks
    private FeedbackServiceImpl feedbackService;

    @Mock
    private FeedbackRepository feedbackRepository;

    // Trường hợp bình thường: Kiểm thử tạo phản hồi
    @Test
    void testCreateFeedback_Normal() {
        // Tạo đối tượng User và Feedback sử dụng builder
        User user = User.builder().userId(1).email("test@example.com").build();
        Feedback feedback = Feedback.builder()
                .user(user)
                .comment("Great food!")
                .timeCreated(LocalDateTime.now())
                .foodId(1)
                .canteenId(1)
                .feedbackStatus(FeedbackStatus.PENDING)
                .build();

        // Giả lập phương thức save của feedbackRepository để trả về đối tượng Feedback
        when(feedbackRepository.save(any(Feedback.class))).thenReturn(feedback);

        // Gọi phương thức service
        Feedback result = feedbackService.createFeedback(user, "Great food!", 1, 1);

        // Kiểm tra xem kết quả trả về có khớp với mong đợi hay không
        assertEquals(feedback, result);

        // Xác minh rằng phương thức save của repository đã được gọi chính xác một lần
        verify(feedbackRepository, times(1)).save(any(Feedback.class));
    }

    // Trường hợp bất thường: Kiểm thử tạo phản hồi khi đối tượng User là null
    @Test
    void testCreateFeedback_Abnormal() {
        // Gọi phương thức service và kiểm tra xem có ngoại lệ nào bị ném ra hay không
        Exception exception = assertThrows(NullPointerException.class, () -> {
            feedbackService.createFeedback(null, "Great food!", 1, 1);
        });

        // Kiểm tra xem thông điệp ngoại lệ có khớp với thông điệp mong đợi hay không
        assertEquals("Cannot invoke \"com.ffood.g1.entity.User.getUserId()\" because \"user\" is null", exception.getMessage());

        // Xác minh rằng phương thức save của repository không được gọi
        verify(feedbackRepository, times(0)).save(any(Feedback.class));
    }

    // Trường hợp ranh giới: Kiểm thử tạo phản hồi với bình luận rỗng
    @Test
    void testCreateFeedback_Boundary() {
        // Tạo đối tượng User sử dụng builder
        User user = User.builder().userId(1).email("test@example.com").build();
        Feedback feedback = Feedback.builder()
                .user(user)
                .comment("")
                .timeCreated(LocalDateTime.now())
                .foodId(1)
                .canteenId(1)
                .feedbackStatus(FeedbackStatus.PENDING)
                .build();

        // Giả lập phương thức save của feedbackRepository để trả về đối tượng Feedback
        when(feedbackRepository.save(any(Feedback.class))).thenReturn(feedback);

        // Gọi phương thức service
        Feedback result = feedbackService.createFeedback(user, "", 1, 1);

        // Kiểm tra xem kết quả trả về có khớp với mong đợi hay không
        assertEquals(feedback, result);

        // Xác minh rằng phương thức save của repository đã được gọi chính xác một lần
        verify(feedbackRepository, times(1)).save(any(Feedback.class));
    }

    // Trường hợp bình thường: Kiểm thử lấy phản hồi theo canteenId và trạng thái
    @Test
    void testGetFeedbacksByCanteenIdAndStatus_Normal() {
        // Tạo đối tượng Feedback sử dụng builder
        Feedback feedback1 = Feedback.builder().feedbackId(1).canteenId(1).feedbackStatus(FeedbackStatus.PENDING).build();
        Feedback feedback2 = Feedback.builder().feedbackId(2).canteenId(1).feedbackStatus(FeedbackStatus.PENDING).build();

        // Giả lập phương thức findFeedbackByCanteenIdAndStatus của feedbackRepository để trả về danh sách Feedback
        when(feedbackRepository.findFeedbackByCanteenIdAndStatus(1, FeedbackStatus.PENDING)).thenReturn(Arrays.asList(feedback1, feedback2));

        // Gọi phương thức service
        List<Feedback> result = feedbackService.getFeedbacksByCanteenIdAndStatus(1, FeedbackStatus.PENDING);

        // Kiểm tra xem kết quả trả về có khớp với mong đợi hay không
        assertEquals(Arrays.asList(feedback1, feedback2), result);

        // Xác minh rằng phương thức findFeedbackByCanteenIdAndStatus của repository đã được gọi chính xác một lần
        verify(feedbackRepository, times(1)).findFeedbackByCanteenIdAndStatus(1, FeedbackStatus.PENDING);
    }

    // Trường hợp bất thường: Kiểm thử lấy phản hồi khi không có phản hồi nào khớp với canteenId và trạng thái
    @Test
    void testGetFeedbacksByCanteenIdAndStatus_Abnormal() {
        // Giả lập phương thức findFeedbackByCanteenIdAndStatus của feedbackRepository trả về danh sách rỗng
        when(feedbackRepository.findFeedbackByCanteenIdAndStatus(1, FeedbackStatus.PENDING)).thenReturn(Collections.emptyList());

        // Gọi phương thức service
        List<Feedback> result = feedbackService.getFeedbacksByCanteenIdAndStatus(1, FeedbackStatus.PENDING);

        // Kiểm tra xem kết quả trả về có rỗng hay không
        assertTrue(result.isEmpty());

        // Xác minh rằng phương thức findFeedbackByCanteenIdAndStatus của repository đã được gọi chính xác một lần
        verify(feedbackRepository, times(1)).findFeedbackByCanteenIdAndStatus(1, FeedbackStatus.PENDING);
    }

    // Trường hợp ranh giới: Kiểm thử lấy phản hồi với canteenId không hợp lệ
    @Test
    void testGetFeedbacksByCanteenIdAndStatus_Boundary() {
        // Giả lập phương thức findFeedbackByCanteenIdAndStatus của feedbackRepository trả về danh sách rỗng
        when(feedbackRepository.findFeedbackByCanteenIdAndStatus(999, FeedbackStatus.PENDING)).thenReturn(Collections.emptyList());

        // Gọi phương thức service
        List<Feedback> result = feedbackService.getFeedbacksByCanteenIdAndStatus(999, FeedbackStatus.PENDING);

        // Kiểm tra xem kết quả trả về có rỗng hay không
        assertTrue(result.isEmpty());

        // Xác minh rằng phương thức findFeedbackByCanteenIdAndStatus của repository đã được gọi chính xác một lần
        verify(feedbackRepository, times(1)).findFeedbackByCanteenIdAndStatus(999, FeedbackStatus.PENDING);
    }

    // Trường hợp bình thường: Kiểm thử cập nhật trạng thái phản hồi
    @Test
    void testUpdateFeedbackStatus_Normal() {
        // Tạo đối tượng Feedback sử dụng builder
        Feedback feedback = Feedback.builder().feedbackId(1).feedbackStatus(FeedbackStatus.PENDING).build();

        // Giả lập phương thức findById của feedbackRepository để trả về đối tượng Feedback
        when(feedbackRepository.findById(1)).thenReturn(Optional.of(feedback));

        // Gọi phương thức service
        feedbackService.updateFeedbackStatus(1, FeedbackStatus.COMPLETE);

        // Kiểm tra xem trạng thái phản hồi đã được cập nhật đúng hay không
        assertEquals(FeedbackStatus.COMPLETE, feedback.getFeedbackStatus());

        // Xác minh rằng phương thức save của repository đã được gọi chính xác một lần
        verify(feedbackRepository, times(1)).save(feedback);
    }

    // Trường hợp bất thường: Kiểm thử cập nhật trạng thái phản hồi khi feedbackId không hợp lệ
    @Test
    void testUpdateFeedbackStatus_Abnormal() {
        // Giả lập phương thức findById của feedbackRepository trả về Optional rỗng
        when(feedbackRepository.findById(1)).thenReturn(Optional.empty());

        // Gọi phương thức service và kiểm tra xem có ngoại lệ nào bị ném ra hay không
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            feedbackService.updateFeedbackStatus(1, FeedbackStatus.COMPLETE);
        });

        // Kiểm tra xem thông điệp ngoại lệ có khớp với thông điệp mong đợi hay không
        assertEquals("Invalid feedback ID", exception.getMessage());

        // Xác minh rằng phương thức save của repository không được gọi
        verify(feedbackRepository, times(0)).save(any(Feedback.class));
    }

    // Trường hợp ranh giới: Kiểm thử cập nhật trạng thái phản hồi với feedbackId là null
    @Test
    void testUpdateFeedbackStatus_Boundary() {
        // Gọi phương thức service và kiểm tra xem có ngoại lệ nào bị ném ra hay không
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            feedbackService.updateFeedbackStatus(null, FeedbackStatus.COMPLETE);
        });

        // Kiểm tra xem thông điệp ngoại lệ có khớp với thông điệp mong đợi hay không
        assertEquals("Invalid feedback ID", exception.getMessage());

        // Xác minh rằng phương thức save của repository không được gọi
        verify(feedbackRepository, times(0)).save(any(Feedback.class));
    }

    // Trường hợp bình thường: Kiểm thử tìm phản hồi theo trạng thái và phạm vi ngày
    @Test
    void testFindByStatusAndDateRange_Normal() {
        Pageable pageable = PageRequest.of(0, 10);

        // Tạo đối tượng Feedback sử dụng builder
        Feedback feedback1 = Feedback.builder().feedbackId(1).feedbackStatus(FeedbackStatus.COMPLETE).timeCreated(LocalDateTime.now().minusDays(1)).build();
        Feedback feedback2 = Feedback.builder().feedbackId(2).feedbackStatus(FeedbackStatus.COMPLETE).timeCreated(LocalDateTime.now()).build();

        Page<Feedback> expectedPage = new PageImpl<>(Arrays.asList(feedback1, feedback2));

        // Giả lập phương thức findByStatusAndDateRange của feedbackRepository để trả về trang giả lập
        when(feedbackRepository.findByStatusAndDateRange(FeedbackStatus.COMPLETE, LocalDateTime.now().minusDays(2), LocalDateTime.now(), pageable)).thenReturn(expectedPage);

        // Gọi phương thức service
        Page<Feedback> result = feedbackService.findByStatusAndDateRange(FeedbackStatus.COMPLETE, LocalDateTime.now().minusDays(2), LocalDateTime.now(), pageable);

        // Kiểm tra xem trang trả về có khớp với trang mong đợi hay không
        assertEquals(expectedPage, result);

        // Xác minh rằng phương thức findByStatusAndDateRange của repository đã được gọi chính xác một lần
        verify(feedbackRepository, times(1)).findByStatusAndDateRange(FeedbackStatus.COMPLETE, LocalDateTime.now().minusDays(2), LocalDateTime.now(), pageable);
    }

    // Trường hợp bất thường: Kiểm thử tìm phản hồi với phạm vi ngày không hợp lệ
    @Test
    void testFindByStatusAndDateRange_Abnormal() {
        Pageable pageable = PageRequest.of(0, 10);

        // Gọi phương thức service và kiểm tra xem có ngoại lệ nào bị ném ra hay không
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            feedbackService.findByStatusAndDateRange(FeedbackStatus.COMPLETE, LocalDateTime.now(), LocalDateTime.now().minusDays(2), pageable);
        });

        // Kiểm tra xem thông điệp ngoại lệ có khớp với thông điệp mong đợi hay không
        assertEquals("Ngày bắt đầu không thể sau ngày kết thúc", exception.getMessage());

        // Xác minh rằng phương thức findByStatusAndDateRange của repository không được gọi
        verify(feedbackRepository, times(0)).findByStatusAndDateRange(FeedbackStatus.COMPLETE, LocalDateTime.now(), LocalDateTime.now().minusDays(2), pageable);
    }

    // Trường hợp ranh giới: Kiểm thử tìm phản hồi với phạm vi ngày không có phản hồi nào khớp
    @Test
    void testFindByStatusAndDateRange_Boundary() {
        Pageable pageable = PageRequest.of(0, 10);

        // Giả lập phương thức findByStatusAndDateRange của feedbackRepository để trả về trang rỗng
        when(feedbackRepository.findByStatusAndDateRange(FeedbackStatus.COMPLETE, LocalDateTime.now().minusDays(2), LocalDateTime.now(), pageable)).thenReturn(Page.empty());

        // Gọi phương thức service
        Page<Feedback> result = feedbackService.findByStatusAndDateRange(FeedbackStatus.COMPLETE, LocalDateTime.now().minusDays(2), LocalDateTime.now(), pageable);

        // Kiểm tra xem trang trả về có rỗng hay không
        assertTrue(result.isEmpty());

        // Xác minh rằng phương thức findByStatusAndDateRange của repository đã được gọi chính xác một lần
        verify(feedbackRepository, times(1)).findByStatusAndDateRange(FeedbackStatus.COMPLETE, LocalDateTime.now().minusDays(2), LocalDateTime.now(), pageable);
    }

    // Các bài kiểm thử khác cũng tương tự như trên, sử dụng builder để tạo các đối tượng Feedback và kiểm tra các phương thức khác trong FeedbackServiceImpl
}
