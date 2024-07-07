package com.ffood.g1.serviceImpl;

import com.ffood.g1.entity.Canteen;
import com.ffood.g1.entity.ResetToken;
import com.ffood.g1.entity.Role;
import com.ffood.g1.entity.User;
import com.ffood.g1.repository.CanteenRepository;
import com.ffood.g1.repository.ResetTokenRepository;
import com.ffood.g1.repository.RoleRepository;
import com.ffood.g1.repository.UserRepository;
import com.ffood.g1.service.impl.CanteenServiceImpl;
import com.ffood.g1.service.impl.UserServiceImpl;
import com.ffood.g1.utils.UrlUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.MockedStatic;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @InjectMocks
    UserServiceImpl userService;

    @Mock
    UserRepository userRepository;

    @Mock
    RoleRepository roleRepository;

    @Mock
    ResetTokenRepository resetTokenRepository;

    @Mock
    JavaMailSender mailSender;

    @Mock
    HttpServletRequest request;

    @Mock
    CanteenRepository canteenRepository;

    @Test
    void loadUserById_Cases() {
        Integer userId = 1;
        User user = User.builder().userId(userId).email("test@example.com").build();

        // Happy Case
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        User resultFound = userService.loadUserById(userId);
        assertEquals(user, resultFound);
        verify(userRepository, times(1)).findById(userId);

        // Reset mock for the next case
        reset(userRepository);

        // Not Found Case
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        User resultNotFound = userService.loadUserById(userId);
        assertNull(resultNotFound);
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void isCodeNameExist_Cases() {
        String codeName = "testCode";

        // CodeName exists
        when(userRepository.findByCodeName(codeName)).thenReturn(new User());
        boolean resultExists = userService.isCodeNameExist(codeName);
        assertTrue(resultExists);
        verify(userRepository, times(1)).findByCodeName(codeName);

        // Reset mock for the next case
        reset(userRepository);

        // CodeName does not exist
        when(userRepository.findByCodeName(codeName)).thenReturn(null);
        boolean resultNotExists = userService.isCodeNameExist(codeName);
        assertFalse(resultNotExists);
        verify(userRepository, times(1)).findByCodeName(codeName);
    }

//    @Test
//    void sendResetPasswordEmail_Cases() {
//        String email = "test@example.com";
//        User user = User.builder().userId(1).email(email).build();
//        String token = UUID.randomUUID().toString();
//        String baseUrl = "http://example.com";
//
//        when(userRepository.findByEmail(email)).thenReturn(user);
//        when(request.getScheme()).thenReturn("http");
//        when(request.getServerName()).thenReturn("example.com");
//        when(request.getServerPort()).thenReturn(80);
//
//        // Mock the UrlUtil.getBaseUrl() method
//        try (MockedStatic<UrlUtil> mockedStatic = mockStatic(UrlUtil.class)) {
//            when(UrlUtil.getBaseUrl(request)).thenReturn(baseUrl);
//
//            // Execute the service call
//            userService.sendResetPasswordEmail(email, request);
//
//            // Verify the interactions
//            verify(userRepository, times(1)).findByEmail(email);
//            verify(resetTokenRepository, times(1)).save(any(ResetToken.class));
//            verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
//        }
//    }

    @Test
    void updateUserStatus_HappyCase() {
        Integer userId = 1;
        Integer roleId = 3;
        Integer canteenId = 1;
        Boolean isActive = true;

        User user = User.builder().userId(userId).build();
        Role role = Role.builder().roleId(roleId).build();
        Canteen canteen = Canteen.builder().canteenId(canteenId).build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(roleRepository.findById(roleId)).thenReturn(Optional.of(role));
        when(canteenRepository.findById(canteenId)).thenReturn(Optional.of(canteen));

        userService.updateUserStatus(userId, roleId, isActive, canteenId);

        assertEquals(isActive, user.getIsActive());
        assertEquals(canteen, user.getCanteen());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void isPhoneExist_Cases() {
        String phone = "123456789";

        // Phone exists
        when(userRepository.findByPhone(phone)).thenReturn(new User());
        boolean resultExists = userService.isPhoneExist(phone);
        assertTrue(resultExists);
        verify(userRepository, times(1)).findByPhone(phone);

        // Reset mock for the next case
        reset(userRepository);

        // Phone does not exist
        when(userRepository.findByPhone(phone)).thenReturn(null);
        boolean resultNotExists = userService.isPhoneExist(phone);
        assertFalse(resultNotExists);
        verify(userRepository, times(1)).findByPhone(phone);
    }

    @Test
    void isEmailExist_Cases() {
        String email = "test@example.com";

        // Email exists
        when(userRepository.findByEmail(email)).thenReturn(new User());
        boolean resultExists = userService.isEmailExist(email);
        assertTrue(resultExists);
        verify(userRepository, times(1)).findByEmail(email);

        // Reset mock for the next case
        reset(userRepository);

        // Email does not exist
        when(userRepository.findByEmail(email)).thenReturn(null);
        boolean resultNotExists = userService.isEmailExist(email);
        assertFalse(resultNotExists);
        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    void updatePasswordReset_Cases() {
        String token = "testToken";
        String newPassword = "newPassword";
        User user = User.builder().userId(1).build();
        ResetToken resetToken = ResetToken.builder().token(token).user(user).build();

        // Token valid
        when(resetTokenRepository.findByToken(token)).thenReturn(resetToken);
        userService.updatePasswordReset(token, newPassword);
        verify(userRepository, times(1)).save(user);
        verify(resetTokenRepository, times(1)).delete(resetToken);

        // Reset mock for the next case
        reset(resetTokenRepository);

        // Token invalid
        when(resetTokenRepository.findByToken(token)).thenReturn(null);
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.updatePasswordReset(token, newPassword);
        });
        assertEquals("Invalid or expired token.", exception.getMessage());
    }

    @Test
    void updateUser_Cases() {
        Integer userId = 1;
        User existingUser = User.builder().userId(userId).email("existing@example.com").build();
        User updatedUser = User.builder().userId(userId).email("updated@example.com").build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        userService.updateUser(updatedUser);
        assertEquals("updated@example.com", existingUser.getEmail());
        verify(userRepository, times(1)).save(existingUser);

        // Reset mock for the next case
        reset(userRepository);

        // User not found
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        userService.updateUser(updatedUser);
        verify(userRepository, times(0)).save(updatedUser);
    }
}
