package com.ffood.g1.serviceImpl;

import com.ffood.g1.entity.Canteen;
import com.ffood.g1.entity.ResetToken;
import com.ffood.g1.entity.Role;
import com.ffood.g1.entity.User;
import com.ffood.g1.repository.CanteenRepository;
import com.ffood.g1.repository.ResetTokenRepository;
import com.ffood.g1.repository.RoleRepository;
import com.ffood.g1.repository.UserRepository;
import com.ffood.g1.service.impl.UserServiceImpl;
import com.ffood.g1.utils.UrlUtil;
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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl service;

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
    private CanteenRepository canteenRepository;

    @Mock
    private HttpServletRequest request;

    @Test
    void findByEmail() {
        String email = "test@example.com";
        User user = User.builder().email(email).build();

        when(userRepository.findByEmail(email)).thenReturn(user);

        User result = service.findByEmail(email);

        assertEquals(user, result);
        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    void loadUserById() {
        Integer userId = 1;
        User user = User.builder().userId(userId).build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        User result = service.loadUserById(userId);

        assertEquals(user, result);
        verify(userRepository, times(1)).findById(userId);
    }



    @Test
    void sendResetPasswordEmail() {
        String email = "test@example.com";
        User user = User.builder().email(email).build();
        String token = UUID.randomUUID().toString();
        String baseUrl = "http://localhost:8080";

        when(userRepository.findByEmail(email)).thenReturn(user);
        when(request.getRequestURL()).thenReturn(new StringBuffer(baseUrl));

        service.sendResetPasswordEmail(email, request);

        verify(resetTokenRepository, times(1)).save(any(ResetToken.class));
        verify(mailSender, times(1)).send((MimeMessage) any());
    }

    @Test
    void isResetTokenValid() {
        String token = "testToken";
        ResetToken resetToken = new ResetToken(token, User.builder().build());

        when(resetTokenRepository.findByToken(token)).thenReturn(resetToken);

        boolean result = service.isResetTokenValid(token);

        assertTrue(result);
        verify(resetTokenRepository, times(1)).findByToken(token);
    }

    @Test
    void updatePasswordReset() {
        String token = "testToken";
        String password = "newPassword";
        User user = User.builder().build();
        ResetToken resetToken = new ResetToken(token, user);

        when(resetTokenRepository.findByToken(token)).thenReturn(resetToken);
        when(passwordEncoder.encode(password)).thenReturn("encodedPassword");

        service.updatePasswordReset(token, password);

        verify(userRepository, times(1)).save(user);
        verify(resetTokenRepository, times(1)).delete(resetToken);
    }

    @Test
    void updatePassword() {
        User user = User.builder().build();
        String newPassword = "newPassword";

        when(passwordEncoder.encode(newPassword)).thenReturn("encodedPassword");

        service.updatePassword(user, newPassword);

        verify(userRepository, times(1)).save(user);
    }

    @Test
    void getAllUsers() {
        int page = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(page, size);
        User user1 = User.builder().userId(1).build();
        User user2 = User.builder().userId(2).build();
        Page<User> userPage = new PageImpl<>(Arrays.asList(user1, user2));

        when(userRepository.findAll(pageable)).thenReturn(userPage);

        Page<User> result = service.getAllUsers(page, size);

        assertEquals(2, result.getTotalElements());
        verify(userRepository, times(1)).findAll(pageable);
    }

    @Test
    void searchUsersFilter() {
        String keyword = "test";
        int page = 0;
        int size = 10;
        Integer roleId = 1;
        Integer canteenId = 1;
        Pageable pageable = PageRequest.of(page, size);
        User user1 = User.builder().userId(1).build();
        User user2 = User.builder().userId(2).build();
        Page<User> userPage = new PageImpl<>(Arrays.asList(user1, user2));

        when(userRepository.searchUsers(keyword, roleId, canteenId, pageable)).thenReturn(userPage);

        Page<User> result = service.searchUsersFilter(keyword, roleId, canteenId, page, size);

        assertEquals(2, result.getTotalElements());
        verify(userRepository, times(1)).searchUsers(keyword, roleId, canteenId, pageable);
    }

    @Test
    void getAllStaff() {
        int page = 0;
        int size = 10;
        Integer canteenId = 1;
        Pageable pageable = PageRequest.of(page, size);
        User user1 = User.builder().userId(1).build();
        User user2 = User.builder().userId(2).build();
        Page<User> userPage = new PageImpl<>(Arrays.asList(user1, user2));

        when(userRepository.findAllStaffByCanteenId(canteenId, pageable)).thenReturn(userPage);

        Page<User> result = service.getAllStaff(page, size, canteenId);

        assertEquals(2, result.getTotalElements());
        verify(userRepository, times(1)).findAllStaffByCanteenId(canteenId, pageable);
    }

    @Test
    void getUserById() {
        Integer userId = 1;
        User user = User.builder().userId(userId).build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        User result = service.getUserById(userId);

        assertEquals(user, result);
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void updateUserStatus() {
        Integer userId = 1;
        Integer roleId = 1;
        Boolean isActive = true;
        Integer canteenId = 1;
        User user = User.builder().userId(userId).build();
        Canteen canteen = Canteen.builder().canteenId(canteenId).build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(canteenRepository.findById(canteenId)).thenReturn(Optional.of(canteen));

        service.updateUserStatus(userId, roleId, isActive, canteenId);

        verify(userRepository, times(1)).save(user);
    }

    @Test
    void saveUser() {
        User user = User.builder().password("password").build();

        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");

        service.saveUser(user);

        assertEquals("encodedPassword", user.getPassword());
        assertNotNull(user.getCreatedDate());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void countUsers() {
        long count = 5L;

        when(userRepository.count()).thenReturn(count);

        Integer result = service.countUsers();

        assertEquals(5, result);
        verify(userRepository, times(1)).count();
    }




    @Test
    void isPhoneExist() {
        String phone = "123456789";
        User user = User.builder().phone(phone).build();

        when(userRepository.findByPhone(phone)).thenReturn(user);

        boolean result = service.isPhoneExist(phone);

        assertTrue(result);
        verify(userRepository, times(1)).findByPhone(phone);
    }

    @Test
    void updateUser() {
        User user = User.builder().userId(1).fullName("New Name").build();
        User existingUser = User.builder().userId(1).build();

        when(userRepository.findById(user.getUserId())).thenReturn(Optional.of(existingUser));

        service.updateUser(user);

        verify(userRepository, times(1)).save(existingUser);
    }

    @Test
    void isEmailExist() {
        String email = "test@example.com";
        User user = User.builder().email(email).build();

        when(userRepository.findByEmail(email)).thenReturn(user);

        boolean result = service.isEmailExist(email);

        assertTrue(result);
        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    void registerNewUser() {
        User user = User.builder().fullName("testUser").password("password").email("test@example.com").phone("123456789").build();
        Role role = Role.builder().roleName("ROLE_CUSTOMER").build();

        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(roleRepository.findByName("ROLE_CUSTOMER")).thenReturn(role);

        service.registerNewUser(user);

        assertEquals("encodedPassword", user.getPassword());
        assertEquals(role, user.getRole());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void loadUserByUsername() {
        String email = "test@example.com";
        User user = User.builder().email(email).password("password").role(Role.builder().roleName("ROLE_CUSTOMER").build()).build();

        when(userRepository.findByEmail(email)).thenReturn(user);

        UserDetails userDetails = service.loadUserByUsername(email);

        assertEquals(email, userDetails.getUsername());
        assertEquals("password", userDetails.getPassword());
        assertEquals("ROLE_CUSTOMER", userDetails.getAuthorities().iterator().next().getAuthority());
        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    void countStaffByCanteenId() {
        Integer canteenId = 1;
        long count = 10L;

        when(userRepository.countStaffByCanteenId(canteenId)).thenReturn((int) count);

        Integer result = service.countStaffByCanteenId(canteenId);

        assertEquals(10, result);
        verify(userRepository, times(1)).countStaffByCanteenId(canteenId);
    }
}
