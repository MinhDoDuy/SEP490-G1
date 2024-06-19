package com.ffood.g1.service.impl;

import com.ffood.g1.entity.ResetToken;
import com.ffood.g1.entity.Role;
import com.ffood.g1.entity.User;
import com.ffood.g1.repository.CanteenRepository;
import com.ffood.g1.repository.ResetTokenRepository;
import com.ffood.g1.repository.RoleRepository;
import com.ffood.g1.repository.UserRepository;
import com.ffood.g1.service.UserService;
import com.ffood.g1.utils.UrlUtil;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ResetTokenRepository resetTokenRepository;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private CanteenRepository canteenRepository;


    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public User loadUserById(Integer userId) {
        return userRepository.findById(userId).orElse(null);
    }

    @Override
    public boolean isCodeNameExist(String codeName) {
        return userRepository.findByCodeName(codeName) != null;
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


        String baseUrl = UrlUtil.getBaseUrl(request);

        // Tạo URL đặt lại mật khẩu
        String resetUrl = baseUrl + "/reset-password?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Password Reset Request");
        message.setText("To reset your password, click the link below:\n" + resetUrl);

        mailSender.send(message);
    }

    //check token xem đã hết hạn hoặc là check token có tồn tại không
    @Override
    public boolean isResetTokenValid(String token) {
        ResetToken resetToken = resetTokenRepository.findByToken(token);
        return resetToken != null && !resetToken.isExpired();
    }


    @Override
    //truyền vào string token và password sẽ nhập vào
    public void updatePasswordReset(String token, String password) {
        //get thông tin của token đó
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
    //update password cho người dùng khi ở trang profile
    public void updatePassword(User user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Override
    //ROLE_ADMIN quản lý user (quản lý role user) đã có phần page để phân trang
    public Page<User> getAllUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return userRepository.findAll(pageable);
    }

    @Override
    //ROLE_ADMIN tìm kiếm người dùng thông quan fullname, codeName , Email
    public Page<User> searchUsers(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return userRepository.findByFullNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrCodeNameContainingIgnoreCase(keyword, keyword, keyword, pageable);
    }

    @Override
    public User getUserById(Integer userId) {
        return userRepository.findById(userId).orElse(null);
    }
    @Override
    //update người dùng đó và get ra thông tin người dùng đó
    public void updateUserRole(Integer userId, Integer roleId) {
        User user = userRepository.findById(userId).orElse(null);
        Role role = roleRepository.findById(roleId).orElse(null);
        if (user != null && role != null) {
            user.setRole(role);
            userRepository.save(user);
        }
    }

    @Override
    public void deleteUserById(Integer userId) {
        userRepository.deleteById(userId);
    }

    @Override
    public void saveUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    @Override
    public List<User> getAllManagers() {
        return userRepository.findByRoleRoleId(3);
    }




    @Override
    public boolean isPhoneExist(String phone) {
        return userRepository.findByPhone(phone) != null;
    }

    @Override
    //update profile người dùng
    public void updateUser(User user) {
        User existingUser = userRepository.findById(user.getUserId()).orElse(null);
        if (existingUser != null) {
            existingUser.setFullName(user.getFullName());
            existingUser.setPhone(user.getPhone());
            existingUser.setEmail(user.getEmail());
            existingUser.setUserImage(user.getUserImage());
            userRepository.save(existingUser);
        }
    }

    @Override
    public boolean isEmailExist(String email) {
        return userRepository.findByEmail(email) != null;
    }

    @Transactional
    public void registerNewUser(User user) {
        user.setFullName(user.getUserName());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setEmail(user.getEmail());
        user.setPhone(user.getPhone());

        Role role = roleRepository.findByName("ROLE_CUSTOMER");
        if (role == null) {
            throw new IllegalArgumentException("Invalid role");
        } else {
            user.setRole(role);
        }
        userRepository.save(user);
    }

    @Override
    public void saveUserWithDefaultRole(User user) {
        // Implement save user logic if needed
    }



    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), Collections.singletonList(new SimpleGrantedAuthority(user.getRole().getRoleName())));
    }

    public boolean checkEmailAndPassword(String email, String rawPassword) {
        User user = findByEmail(email);
        if (user == null) {
            return false;
        }
        return passwordEncoder.matches(rawPassword, user.getPassword());
    }
}




