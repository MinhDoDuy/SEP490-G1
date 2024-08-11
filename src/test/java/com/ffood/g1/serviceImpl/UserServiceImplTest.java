package com.ffood.g1.serviceImpl;

import com.ffood.g1.entity.*;
import com.ffood.g1.repository.*;
import com.ffood.g1.service.CanteenService;
import com.ffood.g1.service.RoleService;
import com.ffood.g1.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private ResetTokenRepository resetTokenRepository;

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private CanteenRepository canteenRepository;

    @Mock
    private RoleService roleService;

    @Mock
    private CanteenService canteenService;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    // Trường hợp bình thường: Kiểm thử tìm kiếm người dùng theo email
    @Test
    void testFindByEmail_Normal() {
        // Tạo đối tượng User sử dụng builder
        User user = User.builder().userId(1).email("test@example.com").build();

        // Giả lập phương thức findByEmail của userRepository để trả về đối tượng User
        when(userRepository.findByEmail("test@example.com")).thenReturn(user);

        // Gọi phương thức service
        User result = userService.findByEmail("test@example.com");

        // Kiểm tra xem kết quả trả về có khớp với mong đợi hay không
        assertEquals(user, result);

        // Xác minh rằng phương thức findByEmail của repository đã được gọi chính xác một lần
        verify(userRepository, times(1)).findByEmail("test@example.com");
    }

    // Trường hợp bất thường: Kiểm thử tìm kiếm người dùng theo email khi không tìm thấy
    @Test
    void testFindByEmail_Abnormal() {
        // Giả lập phương thức findByEmail của userRepository trả về null
        when(userRepository.findByEmail("notfound@example.com")).thenReturn(null);

        // Gọi phương thức service
        User result = userService.findByEmail("notfound@example.com");

        // Kiểm tra xem kết quả trả về có phải là null hay không
        assertNull(result);

        // Xác minh rằng phương thức findByEmail của repository đã được gọi chính xác một lần
        verify(userRepository, times(1)).findByEmail("notfound@example.com");
    }

    // Trường hợp ranh giới: Kiểm thử tìm kiếm người dùng theo email với email rỗng
    @Test
    void testFindByEmail_Boundary() {
        // Giả lập phương thức findByEmail của userRepository trả về null
        when(userRepository.findByEmail("")).thenReturn(null);

        // Gọi phương thức service
        User result = userService.findByEmail("");

        // Kiểm tra xem kết quả trả về có phải là null hay không
        assertNull(result);

        // Xác minh rằng phương thức findByEmail của repository đã được gọi chính xác một lần
        verify(userRepository, times(1)).findByEmail("");
    }

    // Trường hợp bình thường: Kiểm thử cập nhật trạng thái người dùng
    @Test
    void testUpdateUserStatus_Normal() {
        // Tạo đối tượng User sử dụng builder
        User user = User.builder().userId(1).isActive(false).build();

        // Giả lập phương thức findById của userRepository để trả về đối tượng User
        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        // Gọi phương thức service
        userService.updateUserStatus(1, true);

        // Kiểm tra xem trạng thái người dùng đã được cập nhật đúng hay không
        assertTrue(user.getIsActive());

        // Xác minh rằng phương thức save của repository đã được gọi chính xác một lần
        verify(userRepository, times(1)).save(user);
    }

    // Trường hợp bất thường: Kiểm thử cập nhật trạng thái người dùng khi không tìm thấy người dùng
    @Test
    void testUpdateUserStatus_Abnormal() {
        // Giả lập phương thức findById của userRepository trả về Optional rỗng
        when(userRepository.findById(1)).thenReturn(Optional.empty());

        // Gọi phương thức service và kiểm tra xem có ngoại lệ nào bị ném ra hay không
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.updateUserStatus(1, true);
        });

        // Kiểm tra xem thông điệp ngoại lệ có khớp với thông điệp mong đợi hay không
        assertEquals("Invalid user Id:1", exception.getMessage());

        // Xác minh rằng phương thức save của repository không được gọi
        verify(userRepository, times(0)).save(any(User.class));
    }

    // Trường hợp ranh giới: Kiểm thử cập nhật trạng thái người dùng khi userId không hợp lệ
    @Test
    void testUpdateUserStatus_Boundary() {
        // Gọi phương thức service và kiểm tra xem có ngoại lệ nào bị ném ra hay không
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.updateUserStatus(null, true);
        });

        // Kiểm tra xem thông điệp ngoại lệ có khớp với thông điệp mong đợi hay không
        assertEquals("Invalid user Id:null", exception.getMessage());

        // Xác minh rằng phương thức save của repository không được gọi
        verify(userRepository, times(0)).save(any(User.class));
    }

    // Trường hợp bình thường: Kiểm thử việc gửi email đặt lại mật khẩu
    @Test
    void testSendResetPasswordEmail_Normal() {
        HttpServletRequest request = mock(HttpServletRequest.class);

        // Tạo đối tượng User sử dụng builder
        User user = User.builder().userId(1).email("test@example.com").build();

        // Giả lập phương thức findByEmail của userRepository để trả về đối tượng User
        when(userRepository.findByEmail("test@example.com")).thenReturn(user);

        // Giả lập phương thức getBaseUrl của UrlUtil để trả về URL cơ bản
        when(request.getRequestURL()).thenReturn(new StringBuffer("http://localhost"));
        when(request.getRequestURI()).thenReturn("/contextPath");
        when(request.getContextPath()).thenReturn("/contextPath");

        // Gọi phương thức service
        userService.sendResetPasswordEmail("test@example.com", request);

        // Xác minh rằng phương thức save của resetTokenRepository đã được gọi chính xác một lần
        verify(resetTokenRepository, times(1)).save(any(ResetToken.class));

        // Xác minh rằng phương thức send của mailSender đã được gọi chính xác một lần
        verify(mailSender, times(1)).send(any(MimeMessage.class));
    }

    // Trường hợp bất thường: Kiểm thử khi email không tồn tại trong phương thức sendResetPasswordEmail
    @Test
    void testSendResetPasswordEmail_Abnormal() {
        HttpServletRequest request = mock(HttpServletRequest.class);

        // Giả lập phương thức findByEmail của userRepository trả về null
        when(userRepository.findByEmail("notfound@example.com")).thenReturn(null);

        // Gọi phương thức service và kiểm tra xem có ngoại lệ nào bị ném ra hay không
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.sendResetPasswordEmail("notfound@example.com", request);
        });

        // Kiểm tra xem thông điệp ngoại lệ có khớp với thông điệp mong đợi hay không
        assertEquals("No user found with email: notfound@example.com", exception.getMessage());

        // Xác minh rằng phương thức save của resetTokenRepository không được gọi
        verify(resetTokenRepository, times(0)).save(any(ResetToken.class));

        // Xác minh rằng phương thức send của mailSender không được gọi
        verify(mailSender, times(0)).send(any(MimeMessage.class));
    }

    // Trường hợp ranh giới: Kiểm thử khi email là rỗng trong phương thức sendResetPasswordEmail
    @Test
    void testSendResetPasswordEmail_Boundary() {
        HttpServletRequest request = mock(HttpServletRequest.class);

        // Giả lập phương thức findByEmail của userRepository trả về null
        when(userRepository.findByEmail("")).thenReturn(null);

        // Gọi phương thức service và kiểm tra xem có ngoại lệ nào bị ném ra hay không
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.sendResetPasswordEmail("", request);
        });

        // Kiểm tra xem thông điệp ngoại lệ có khớp với thông điệp mong đợi hay không
        assertEquals("No user found with email: ", exception.getMessage());

        // Xác minh rằng phương thức save của resetTokenRepository không được gọi
        verify(resetTokenRepository, times(0)).save(any(ResetToken.class));

        // Xác minh rằng phương thức send của mailSender không được gọi
        verify(mailSender, times(0)).send(any(MimeMessage.class));
    }

    // Trường hợp bình thường: Kiểm thử việc cập nhật mật khẩu người dùng
    @Test
    void testUpdatePassword_Normal() {
        // Tạo đối tượng User sử dụng builder
        User user = User.builder().userId(1).password("oldPassword").build();

        // Giả lập phương thức encode của passwordEncoder để trả về mật khẩu mới
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");

        // Gọi phương thức service
        userService.updatePassword(user, "newPassword");

        // Kiểm tra xem mật khẩu người dùng đã được cập nhật đúng hay không
        assertEquals("encodedNewPassword", user.getPassword());

        // Xác minh rằng phương thức save của userRepository đã được gọi chính xác một lần
        verify(userRepository, times(1)).save(user);
    }

    // Trường hợp bất thường: Kiểm thử khi user là null trong phương thức updatePassword
    @Test
    void testUpdatePassword_Abnormal() {
        // Gọi phương thức service và kiểm tra xem có ngoại lệ nào bị ném ra hay không
        Exception exception = assertThrows(NullPointerException.class, () -> {
            userService.updatePassword(null, "newPassword");
        });

        // Kiểm tra xem thông điệp ngoại lệ có khớp với thông điệp mong đợi hay không
        assertEquals("Cannot invoke \"com.ffood.g1.entity.User.setPassword(String)\" because \"user\" is null", exception.getMessage());

        // Xác minh rằng phương thức save của userRepository không được gọi
        verify(userRepository, times(0)).save(any(User.class));
    }

    // Trường hợp ranh giới: Kiểm thử khi mật khẩu mới là rỗng trong phương thức updatePassword
    @Test
    void testUpdatePassword_Boundary() {
        // Tạo đối tượng User sử dụng builder
        User user = User.builder().userId(1).password("oldPassword").build();

        // Giả lập phương thức encode của passwordEncoder để trả về mật khẩu mới
        when(passwordEncoder.encode("")).thenReturn("");

        // Gọi phương thức service
        userService.updatePassword(user, "");

        // Kiểm tra xem mật khẩu người dùng đã được cập nhật đúng hay không
        assertEquals("", user.getPassword());

        // Xác minh rằng phương thức save của userRepository đã được gọi chính xác một lần
        verify(userRepository, times(1)).save(user);
    }

    // Trường hợp bình thường: Kiểm thử việc tìm kiếm người dùng với phân trang
    @Test
    void testGetAllUsers_Normal() {
        //tạo đối tượng pageable của thư viện domain
        Pageable pageable = PageRequest.of(0, 10);

        // Tạo các đối tượng User sử dụng builder
        User user1 = User.builder().userId(1).email("user1@example.com").build();
        User user2 = User.builder().userId(2).email("user2@example.com").build();

        Page<User> expectedPage = new PageImpl<>(Arrays.asList(user1, user2));

        // Giả lập phương thức findAll của userRepository để trả về trang giả lập
        when(userRepository.findAll(pageable)).thenReturn(expectedPage);

        // Gọi phương thức service
        Page<User> result = userService.getAllUsers(0, 10);

        // Kiểm tra xem trang trả về có khớp với trang mong đợi hay không
        assertEquals(expectedPage, result);

        // Xác minh rằng phương thức findAll của repository đã được gọi chính xác một lần
        verify(userRepository, times(1)).findAll(pageable);
    }

    // Trường hợp bất thường: Kiểm thử khi repository ném ra ngoại lệ trong phương thức getAllUsers
    @Test
    void testGetAllUsers_Abnormal() {
        Pageable pageable = PageRequest.of(0, 10);

        // Giả lập repository ném ra một RuntimeException khi gọi findAll
        when(userRepository.findAll(pageable)).thenThrow(new RuntimeException("Database Error"));

        // Gọi phương thức service và kiểm tra xem có ngoại lệ nào bị ném ra hay không
        Exception exception = assertThrows(RuntimeException.class, () -> {
            userService.getAllUsers(0, 10);
        });

        // Kiểm tra xem thông điệp ngoại lệ có khớp với thông điệp mong đợi hay không
        assertEquals("Database Error", exception.getMessage());

        // Xác minh rằng phương thức findAll của repository đã được gọi chính xác một lần
        verify(userRepository, times(1)).findAll(pageable);
    }

    // Trường hợp ranh giới: Kiểm thử khi không có người dùng nào trong cơ sở dữ liệu trong phương thức getAllUsers
    @Test
    void testGetAllUsers_Boundary() {
        Pageable pageable = PageRequest.of(0, 10);

        // Giả lập phương thức findAll của userRepository để trả về trang rỗng
        when(userRepository.findAll(pageable)).thenReturn(Page.empty());

        // Gọi phương thức service
        Page<User> result = userService.getAllUsers(0, 10);

        // Kiểm tra xem trang trả về có rỗng hay không
        assertTrue(result.isEmpty());

        // Xác minh rằng phương thức findAll của repository đã được gọi chính xác một lần
        verify(userRepository, times(1)).findAll(pageable);
    }

    // Các bài kiểm thử khác cũng tương tự như trên, sử dụng builder để tạo các đối tượng User và kiểm tra các phương thức khác trong UserServiceImpl
}
