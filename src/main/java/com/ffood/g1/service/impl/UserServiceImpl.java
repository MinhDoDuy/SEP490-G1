package com.ffood.g1.service.impl;

import com.ffood.g1.entity.ResetToken;
import com.ffood.g1.entity.User;
import com.ffood.g1.repository.ResetTokenRepository;
import com.ffood.g1.repository.UserRepository;
import com.ffood.g1.service.UserService;
import com.ffood.g1.utils.UrlUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private ResetTokenRepository resetTokenRepository;

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public User loadUserById(Integer userId) {
       return userRepository.findById(userId).orElse(null);
    }

    @Override
    public void  updateUser(User user) {
        User existingUser = userRepository.findById(user.getUserId()).orElse(null);
        if (existingUser != null) {
            existingUser.setFullName(user.getFullName());
            existingUser.setPhone(user.getPhone());
            existingUser.setCodeName(user.getCodeName());
            userRepository.save(existingUser);
        }
    }

    @Override
    public void updatePassword(User user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Override
    public void sendResetPasswordEmail(String email, HttpServletRequest request) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new IllegalArgumentException("No user found with email: " + email);
        }

        String token = UUID.randomUUID().toString();
        ResetToken resetToken = new ResetToken(token, user);
        resetTokenRepository.save(resetToken);

//        String resetUrl = "http://localhost:8080/reset-password?token=" + token;
        // Lấy URL cơ sở của ứng dụng từ HttpServletRequest
        String baseUrl = UrlUtil.getBaseUrl(request);

        // Tạo URL đặt lại mật khẩu
        String resetUrl = baseUrl + "/reset-password?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Password Reset Request");
        message.setText("To reset your password, click the link below:\n" + resetUrl);

        mailSender.send(message);
    }

    @Override
    public boolean isResetTokenValid(String token) {
        ResetToken resetToken = resetTokenRepository.findByToken(token);
        return resetToken != null && !resetToken.isExpired();
    }

    @Override
    public void updatePasswordReset(String token, String password) {
        ResetToken resetToken = resetTokenRepository.findByToken(token);
        if (resetToken == null || resetToken.isExpired()) {
            throw new IllegalArgumentException("Invalid or expired token.");
        }

        User user = resetToken.getUser();
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        user.setPassword(encoder.encode(password));
        userRepository.save(user);

        resetTokenRepository.delete(resetToken);
    }


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority(user.getRole().getRoleName())));
    }
}