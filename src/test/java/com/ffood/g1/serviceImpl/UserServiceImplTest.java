package com.ffood.g1.serviceImpl;

import com.ffood.g1.entity.*;
import com.ffood.g1.repository.*;
import com.ffood.g1.service.*;
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
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.mail.MessagingException;
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
    private RoleService roleService;

    @Mock
    private CanteenService canteenService;

    @Mock
    private HttpServletRequest request;

    @Test
    void testFindByEmail() {
        String email = "test@example.com";
        User expectedUser = new User();

        when(userRepository.findByEmail(email)).thenReturn(expectedUser);

        User result = userService.findByEmail(email);

        assertEquals(expectedUser, result);
        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    void testLoadUserById() {
        Integer userId = 1;
        User expectedUser = new User();

        when(userRepository.findById(userId)).thenReturn(Optional.of(expectedUser));

        User result = userService.loadUserById(userId);

        assertEquals(expectedUser, result);
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void testSendResetPasswordEmail() {
        String email = "test@example.com";
        User user = new User();
        user.setEmail(email);

        when(userRepository.findByEmail(email)).thenReturn(user);
        when(request.getRequestURL()).thenReturn(new StringBuffer("http://localhost"));
        when(request.getServletPath()).thenReturn("/reset-password");

        userService.sendResetPasswordEmail(email, request);

        verify(userRepository, times(1)).findByEmail(email);
        verify(resetTokenRepository, times(1)).save(any(ResetToken.class));
        verify(mailSender, times(1)).send(any(MimeMessage.class));
    }

    @Test
    void testIsResetTokenValid() {
        String token = UUID.randomUUID().toString();
        ResetToken resetToken = new ResetToken();
        resetToken.setToken(token);

        when(resetTokenRepository.findByToken(token)).thenReturn(resetToken);

        boolean result = userService.isResetTokenValid(token);

        assertTrue(result);
        verify(resetTokenRepository, times(1)).findByToken(token);
    }

    @Test
    void testUpdatePasswordReset() {
        String token = UUID.randomUUID().toString();
        String password = "newPassword";
        ResetToken resetToken = new ResetToken();
        User user = new User();
        resetToken.setToken(token);
        resetToken.setUser(user);

        when(resetTokenRepository.findByToken(token)).thenReturn(resetToken);
        when(passwordEncoder.encode(password)).thenReturn("encodedPassword");

        userService.updatePasswordReset(token, password);

        assertEquals("encodedPassword", user.getPassword());
        verify(userRepository, times(1)).save(user);
        verify(resetTokenRepository, times(1)).delete(resetToken);
    }

    @Test
    void testUpdatePassword() {
        User user = new User();
        String newPassword = "newPassword";

        when(passwordEncoder.encode(newPassword)).thenReturn("encodedPassword");

        userService.updatePassword(user, newPassword);

        assertEquals("encodedPassword", user.getPassword());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testSearchUsersFilter() {
        String keyword = "test";
        Integer roleId = 1;
        Integer canteenId = 1;
        int page = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(page, size);
        Page<User> expectedPage = new PageImpl<>(Collections.emptyList());

        when(userRepository.searchUsers(keyword, roleId, canteenId, pageable)).thenReturn(expectedPage);

        Page<User> result = userService.searchUsersFilter(keyword, roleId, canteenId, page, size);

        assertEquals(expectedPage, result);
        verify(userRepository, times(1)).searchUsers(keyword, roleId, canteenId, pageable);
    }

    @Test
    void testGetAllUsers() {
        int page = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(page, size);
        Page<User> expectedPage = new PageImpl<>(Collections.emptyList());

        when(userRepository.findAll(pageable)).thenReturn(expectedPage);

        Page<User> result = userService.getAllUsers(page, size);

        assertEquals(expectedPage, result);
        verify(userRepository, times(1)).findAll(pageable);
    }

    @Test
    void testGetAllStaff() {
        int page = 0;
        int size = 10;
        Integer canteenId = 1;
        Pageable pageable = PageRequest.of(page, size);
        Page<User> expectedPage = new PageImpl<>(Collections.emptyList());

        when(userRepository.findAllStaffByCanteenId(canteenId, pageable)).thenReturn(expectedPage);

        Page<User> result = userService.getAllStaff(page, size, canteenId);

        assertEquals(expectedPage, result);
        verify(userRepository, times(1)).findAllStaffByCanteenId(canteenId, pageable);
    }

    @Test
    void testGetUserById() {
        Integer userId = 1;
        User expectedUser = new User();

        when(userRepository.findById(userId)).thenReturn(Optional.of(expectedUser));

        User result = userService.getUserById(userId);

        assertEquals(expectedUser, result);
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void testUpdateUserStatus() {
        Integer userId = 1;
        Boolean isActive = true;
        User user = new User();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userService.updateUserStatus(userId, isActive);

        assertEquals(isActive, user.getIsActive());
        assertNotNull(user.getUpdatedDate());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testSaveUser() {
        User user = new User();
        user.setPassword("password");

        when(passwordEncoder.encode(user.getPassword())).thenReturn("encodedPassword");

        userService.saveUser(user);

        assertEquals("encodedPassword", user.getPassword());
        assertNotNull(user.getCreatedDate());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testCountUsers() {
        long expectedCount = 10L;

        when(userRepository.count()).thenReturn(expectedCount);

        int result = userService.countUsers();

        assertEquals((int) expectedCount, result);
        verify(userRepository, times(1)).count();
    }

    @Test
    void testGetStaffUsers() {
        int page = 0;
        int size = 10;
        int roleId = 2;
        int canteenId = 1;
        Pageable pageable = PageRequest.of(page, size);
        Page<User> expectedPage = new PageImpl<>(Collections.emptyList());

        when(userRepository.findAllByRoleIdAndCanteenId(roleId, canteenId, pageable)).thenReturn(expectedPage);

        Page<User> result = userService.getStaffUsers(page, size, roleId, canteenId);

        assertEquals(expectedPage, result);
        verify(userRepository, times(1)).findAllByRoleIdAndCanteenId(roleId, canteenId, pageable);
    }

    @Test
    void testSearchStaff() {
        String keyword = "test";
        int page = 0;
        int size = 10;
        int roleId = 2;
        int canteenId = 1;
        Pageable pageable = PageRequest.of(page, size);
        Page<User> expectedPage = new PageImpl<>(Collections.emptyList());

        when(userRepository.findByKeywordRoleIdAndCanteenId(keyword, roleId, canteenId, pageable)).thenReturn(expectedPage);

        Page<User> result = userService.searchStaff(keyword, page, size, roleId, canteenId);

        assertEquals(expectedPage, result);
        verify(userRepository, times(1)).findByKeywordRoleIdAndCanteenId(keyword, roleId, canteenId, pageable);
    }

    @Test
    void testIsPhoneExist() {
        String phone = "123456789";
        User user = new User();

        when(userRepository.findByPhone(phone)).thenReturn(user);

        boolean result = userService.isPhoneExist(phone);

        assertTrue(result);
        verify(userRepository, times(1)).findByPhone(phone);
    }

    @Test
    void testUpdateUser() {
        User user = new User();
        user.setUserId(1);
        user.setFullName("Updated Name");
        user.setPhone("123456789");
        user.setEmail("updated@example.com");

        User existingUser = new User();

        when(userRepository.findById(user.getUserId())).thenReturn(Optional.of(existingUser));

        userService.updateUser(user);

        assertEquals(user.getFullName(), existingUser.getFullName());
        assertEquals(user.getPhone(), existingUser.getPhone());
        assertEquals(user.getEmail(), existingUser.getEmail());
        assertNotNull(existingUser.getUpdatedDate());
        verify(userRepository, times(1)).save(existingUser);
    }

    @Test
    void testIsEmailExist() {
        String email = "test@example.com";
        User user = new User();

        when(userRepository.findByEmail(email)).thenReturn(user);

        boolean result = userService.isEmailExist(email);

        assertTrue(result);
        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    void testRegisterNewUser() {
        User user = new User();
        user.setFullName("username");
        user.setPassword("password");
        user.setEmail("test@example.com");
        user.setPhone("123456789");

        Role role = new Role();
        role.setRoleName("ROLE_CUSTOMER");

        when(passwordEncoder.encode(user.getPassword())).thenReturn("encodedPassword");
        when(roleRepository.findByName("ROLE_CUSTOMER")).thenReturn(role);

        userService.registerNewUser(user);

        assertEquals("username", user.getFullName());
        assertEquals("encodedPassword", user.getPassword());
        assertEquals("ROLE_CUSTOMER", user.getRole().getRoleName());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testLoadUserByUsername() {
        String email = "test@example.com";
        User user = new User();
        user.setEmail(email);
        user.setPassword("password");
        Role role = new Role();
        role.setRoleName("ROLE_USER");
        user.setRole(role);

        when(userRepository.findByEmail(email)).thenReturn(user);

        UserDetails result = userService.loadUserByUsername(email);

        assertEquals(email, result.getUsername());
        assertEquals("password", result.getPassword());
        assertTrue(result.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_USER")));
        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    void testCountStaffByCanteenId() {
        Integer canteenId = 1;
        int expectedCount = 10;

        when(userRepository.countStaffByCanteenId(canteenId)).thenReturn(expectedCount);

        int result = userService.countStaffByCanteenId(canteenId);

        assertEquals(expectedCount, result);
        verify(userRepository, times(1)).countStaffByCanteenId(canteenId);
    }

    @Test
    void testSendAssignStaffEmail() {
        String email = "test@example.com";
        Integer canteenId = 1;
        User user = new User();
        user.setEmail(email);
        Role role = new Role();
        role.setRoleId(1);
        user.setRole(role);

        when(userRepository.findByEmail(email)).thenReturn(user);
        when(request.getRequestURL()).thenReturn(new StringBuffer("http://localhost"));
        when(request.getServletPath()).thenReturn("/assign-confirm");

        userService.sendAssignStaffEmail(email, request, canteenId);

        verify(userRepository, times(1)).findByEmail(email);
        verify(resetTokenRepository, times(1)).save(any(ResetToken.class));
        verify(mailSender, times(1)).send(any(MimeMessage.class));
    }

    @Test
    void testConfirmAssignStaff() {
        String token = UUID.randomUUID().toString();
        Integer canteenId = 1;
        ResetToken resetToken = new ResetToken();
        User user = new User();
        Role staffRole = new Role();
        staffRole.setRoleId(2); // ROLE_STAFF
        resetToken.setToken(token);
        resetToken.setUser(user);

        when(resetTokenRepository.findByToken(token)).thenReturn(resetToken);
        when(roleService.findRoleById(2)).thenReturn(staffRole);
        when(canteenService.getCanteenById(canteenId)).thenReturn(new Canteen());

        userService.confirmAssignStaff(token, canteenId);

        assertEquals(staffRole, user.getRole());
        assertNotNull(user.getCanteen());
        verify(userRepository, times(1)).save(user);
        verify(resetTokenRepository, times(1)).delete(resetToken);
    }

    @Test
    void testSendAssignManagerEmail() {
        String email = "test@example.com";
        Integer canteenId = 1;
        User user = new User();
        user.setEmail(email);
        Role role = new Role();
        role.setRoleId(1);
        user.setRole(role);

        when(userRepository.findByEmail(email)).thenReturn(user);
        when(request.getRequestURL()).thenReturn(new StringBuffer("http://localhost"));
        when(request.getServletPath()).thenReturn("/assign-manager-confirm");

        userService.sendAssignManagerEmail(email, request, canteenId);

        verify(userRepository, times(1)).findByEmail(email);
        verify(resetTokenRepository, times(1)).save(any(ResetToken.class));
        verify(mailSender, times(1)).send(any(MimeMessage.class));
    }

    @Test
    void testConfirmAssignManager() {
        String token = UUID.randomUUID().toString();
        Integer canteenId = 1;
        ResetToken resetToken = new ResetToken();
        User user = new User();
        Role managerRole = new Role();
        managerRole.setRoleId(3); // ROLE_MANAGER
        resetToken.setToken(token);
        resetToken.setUser(user);

        when(resetTokenRepository.findByToken(token)).thenReturn(resetToken);
        when(roleService.findRoleById(3)).thenReturn(managerRole);
        when(canteenService.getCanteenById(canteenId)).thenReturn(new Canteen());

        userService.confirmAssignManager(token, canteenId);

        assertEquals(managerRole, user.getRole());
        assertNotNull(user.getCanteen());
        verify(userRepository, times(1)).save(user);
        verify(resetTokenRepository, times(1)).delete(resetToken);
    }
}
