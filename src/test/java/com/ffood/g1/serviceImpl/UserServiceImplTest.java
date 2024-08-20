package com.ffood.g1.serviceImpl;

import com.ffood.g1.entity.Canteen;
import com.ffood.g1.entity.ResetToken;
import com.ffood.g1.entity.Role;
import com.ffood.g1.entity.User;
import com.ffood.g1.repository.ResetTokenRepository;
import com.ffood.g1.repository.RoleRepository;
import com.ffood.g1.repository.UserRepository;
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
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private ResetTokenRepository resetTokenRepository;

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private CanteenService canteenService;

    @Mock
    private RoleService roleService;

    @Mock
    private HttpServletRequest request;

    // Kiểm thử tìm User bằng email
    @Test
    void testFindByEmail_Normal() {
        User user = new User();
        user.setEmail("test@example.com");

        when(userRepository.findByEmail("test@example.com")).thenReturn(user);

        User result = userService.findByEmail("test@example.com");

        assertEquals(user, result);
        verify(userRepository, times(1)).findByEmail("test@example.com");
    }

    @Test
    void testGetAllStaff_Normal() {
        int page = 0;
        int size = 10;
        Integer canteenId = 1;

        Pageable pageable = PageRequest.of(page, size);

        User user1 = User.builder().userId(1).fullName("user1").build();
        User user2 = User.builder().userId(2).fullName("user2").build();

        // Tạo expectedPage trực tiếp
        Page<User> expectedPage = new PageImpl<>(Arrays.asList(user1, user2), pageable, 2);

        // Mock repository để trả về expectedPage
        when(userRepository.findAllStaffByCanteenId(canteenId, pageable)).thenReturn(expectedPage);

        // Gọi phương thức cần kiểm tra
        Page<User> result = userService.getAllStaff(page, size, canteenId);

        // Kiểm tra xem kết quả có giống với expectedPage không
        assertEquals(expectedPage, result);

        // Xác minh rằng phương thức của repository đã được gọi một lần
        verify(userRepository, times(1)).findAllStaffByCanteenId(canteenId, pageable);
    }




    // Kiểm thử load User bằng ID
    @Test
    void testLoadUserById_Normal() {
        User user = new User();
        user.setUserId(1);

        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        User result = userService.loadUserById(1);

        assertEquals(user, result);
        verify(userRepository, times(1)).findById(1);
    }

    // Kiểm thử gửi email reset mật khẩu
    @Test
    void testSendResetPasswordEmail_Normal() throws Exception {
        User user = new User();
        user.setEmail("test@example.com");

        when(userRepository.findByEmail("test@example.com")).thenReturn(user);

        MimeMessage mimeMessage = mock(MimeMessage.class);

        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doNothing().when(mailSender).send(mimeMessage);

        String token = UUID.randomUUID().toString();
        ResetToken resetToken = new ResetToken(token, user);
        when(resetTokenRepository.save(any(ResetToken.class))).thenReturn(resetToken);

        userService.sendResetPasswordEmail("test@example.com", request);

        verify(mailSender, times(1)).send(mimeMessage);
        verify(resetTokenRepository, times(1)).save(any(ResetToken.class));
    }


    // Kiểm thử tính hợp lệ của token reset mật khẩu
    @Test
    void testIsResetTokenValid_Normal() {
        ResetToken resetToken = new ResetToken("token", new User());
        resetToken.setExpiryDate(LocalDate.now().plusDays(1).atStartOfDay());

        when(resetTokenRepository.findByToken("token")).thenReturn(resetToken);

        boolean result = userService.isResetTokenValid("token");

        assertTrue(result);
        verify(resetTokenRepository, times(1)).findByToken("token");
    }



    // Kiểm thử tải UserDetails bằng email
    @Test
    void testLoadUserByUsername_Normal() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("password");
        Role role = new Role();
        role.setRoleName("ROLE_USER");
        user.setRole(role);

        when(userRepository.findByEmail("test@example.com")).thenReturn(user);

        UserDetails result = userService.loadUserByUsername("test@example.com");

        assertEquals(user.getEmail(), result.getUsername());
        assertEquals(user.getPassword(), result.getPassword());
        assertEquals(1, result.getAuthorities().size());


        verify(userRepository, times(1)).findByEmail("test@example.com");
    }

    // Kiểm thử exception khi không tìm thấy User bằng email
    @Test
    void testLoadUserByUsername_UserNotFound() {
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(null);

        assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername("unknown@example.com"));

        verify(userRepository, times(1)).findByEmail("unknown@example.com");
    }

    // Kiểm thử tính toán số lượng nhân viên trong Canteen
    @Test
    void testCountStaffByCanteenId_Normal() {
        when(userRepository.countStaffByCanteenId(1)).thenReturn(5);

        Integer result = userService.countStaffByCanteenId(1);

        assertEquals(5, result);
        verify(userRepository, times(1)).countStaffByCanteenId(1);
    }

    // Kiểm thử tìm kiếm nhân viên theo từ khóa
    @Test
    void testSearchStaff_Normal() {
        Pageable pageable = PageRequest.of(0, 10);
        User user1 = new User();
        user1.setUserId(1);
        user1.setFullName("John Doe");

        Page<User> page = new PageImpl<>(List.of(user1), pageable, 1);

        when(userRepository.findByKeywordRoleIdAndCanteenId("John", 2, 1, pageable)).thenReturn(page);

        Page<User> result = userService.searchStaff("John", 0, 10, 2, 1);

        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());
        assertTrue(result.getContent().contains(user1));

        verify(userRepository, times(1)).findByKeywordRoleIdAndCanteenId("John", 2, 1, pageable);
    }

    // Kiểm thử gửi email xác nhận Assign Manager
    @Test
    void testSendAssignManagerEmail_Normal() throws Exception {
        User user = new User();
        user.setEmail("test@example.com");
        Role role = new Role();
        role.setRoleId(1);
        user.setRole(role);

        when(userRepository.findByEmail("test@example.com")).thenReturn(user);

        MimeMessage mimeMessage = mock(MimeMessage.class);

        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doNothing().when(mailSender).send(mimeMessage);

        String token = UUID.randomUUID().toString();
        ResetToken resetToken = new ResetToken(token, user);
        when(resetTokenRepository.save(any(ResetToken.class))).thenReturn(resetToken);

        userService.sendAssignManagerEmail("test@example.com", request, 1);

        verify(mailSender, times(1)).send(mimeMessage);
        verify(resetTokenRepository, times(1)).save(any(ResetToken.class));
    }


    // Kiểm thử xác nhận Assign Manager
    @Test
    void testConfirmAssignManager_Normal() {
        User user = new User();
        user.setUserId(1);
        Role role = new Role();
        role.setRoleId(1);
        user.setRole(role);

        ResetToken resetToken = new ResetToken("token", user);
        resetToken.setExpiryDate(LocalDate.now().plusDays(1).atStartOfDay());

        when(resetTokenRepository.findByToken("token")).thenReturn(resetToken);
        when(roleService.findRoleById(3)).thenReturn(role);

        userService.confirmAssignManager("token", 1);

        verify(userRepository, times(1)).save(user);
        verify(resetTokenRepository, times(1)).delete(resetToken);
    }
}
