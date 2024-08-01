package com.ffood.g1.service.impl;

import com.ffood.g1.entity.Canteen;
import com.ffood.g1.entity.ResetToken;
import com.ffood.g1.entity.Role;
import com.ffood.g1.entity.User;
import com.ffood.g1.repository.CanteenRepository;
import com.ffood.g1.repository.ResetTokenRepository;
import com.ffood.g1.repository.RoleRepository;
import com.ffood.g1.repository.UserRepository;
import com.ffood.g1.service.CanteenService;
import com.ffood.g1.service.RoleService;
import com.ffood.g1.service.UserService;
import com.ffood.g1.utils.UrlUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
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

    @Autowired
    private RoleService roleService;

    @Autowired
    private CanteenService canteenService;

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public User loadUserById(Integer userId) {
        return userRepository.findById(userId).orElse(null);
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

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(email);
            helper.setSubject("Đặt lại mật khẩu");

            String htmlMsg = "<html>" +
                    "<body>" +
                    "<p>Xác Nhận Đặt Lại Mật Khẩu, Nhấn vào nút bên dưới:</p>" +
                    "<a href=\"" + resetUrl + "\" style=\"display: inline-block; padding: 10px 20px; font-size: 16px; color: white; background-color: #007bff; text-decoration: none; border-radius: 4px;\">Xác Nhận Đổi Mật Khẩu</a>" +
                    "</body>" +
                    "</html>";
            helper.setText(htmlMsg, true);

            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email", e);
        }
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
    public void updatePassword(User user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Override
    public Page<User> searchUsersFilter(String keyword, Integer roleId, Integer canteenId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return userRepository.searchUsers(keyword, roleId, canteenId, pageable);
    }

    @Override
    //ROLE_ADMIN quản lý user (quản lý role user) đã có phần page để phân trang
    public Page<User> getAllUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return userRepository.findAll(pageable);
    }

    @Override
    public Page<User> getAllStaff(int page, int size, Integer canteenId) {
        Pageable pageable = PageRequest.of(page, size);
        return userRepository.findAllStaffByCanteenId(canteenId, pageable);
    }

    @Override
    public User getUserById(Integer userId) {
        return userRepository.findById(userId).orElse(null);
    }

    @Override
    public void updateUserStatus(Integer userId, Integer roleId, Boolean isActive, Integer canteenId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + userId));
        user.setIsActive(isActive);
        if (canteenId != null) {
            Canteen canteen = canteenRepository.findById(canteenId).orElse(null);
            user.setCanteen(canteen);
        } else {
            user.setCanteen(null);
        }
        user.setUpdatedDate(LocalDate.now());
        userRepository.save(user);
    }


    @Override
    public void saveUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setCreatedDate(LocalDate.now());
        userRepository.save(user);
    }


    @Override
    public Integer countUsers() {
        return Math.toIntExact(userRepository.count());
    }

    @Override
    public Page<User> getStaffUsers(int page, int size, int roleId, int canteenId) {
        Pageable pageable = PageRequest.of(page, size);
        return userRepository.findAllByRoleIdAndCanteenId(roleId, canteenId, pageable);
    }

    @Override
    public Page<User> searchStaff(String keyword, int page, int size, int roleId, int canteenId) {
        Pageable pageable = PageRequest.of(page, size);
        return userRepository.findByKeywordRoleIdAndCanteenId(keyword, roleId, canteenId, pageable);
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
            existingUser.setGender(user.getGender());
            existingUser.setUpdatedDate(LocalDate.now());
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
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), Collections.singletonList(new SimpleGrantedAuthority(user.getRole().getRoleName())));
    }

    @Override
    public Integer countStaffByCanteenId(Integer canteenId) {
        return userRepository.countStaffByCanteenId(canteenId);
    }

    @Override
    public List<User> getStaffByCanteenToShip(Integer canteenId) {
        return userRepository.findAllStaffByCanteenIdToShip(canteenId);
    }

    @Override
    public void sendAssignStaffEmail(String email, HttpServletRequest request, Integer canteenId) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new IllegalArgumentException("Email này không tồn tại.");
        } else if (user.getRole().getRoleId() != 1 || user.getCanteen() != null) {
            throw new IllegalArgumentException("Nhân viên này đang thuộc canteen khác hoặc không có quyền thích hợp.");
        }

        String token = UUID.randomUUID().toString();
        ResetToken resetToken = new ResetToken(token, user);
        resetTokenRepository.save(resetToken);

        String baseUrl = UrlUtil.getBaseUrl(request);
        String assignUrl = baseUrl + "/assign-confirm?token=" + token + "&canteenId=" + canteenId;

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(email);
            helper.setSubject("Assign Staff Request");

            String htmlMsg = "<html>" +
                    "<body>" +
                    "<p>Xác Nhận Làm Nhân Viên Căn Tin, Bấm nút dưới đây:</p>" +
                    "<a href=\"" + assignUrl + "\" style=\"display: inline-block; padding: 10px 20px; font-size: 16px; color: white; background-color: #007bff; text-decoration: none; border-radius: 4px;\">Xác Nhận Nhân Viên</a>" +
                    "</body>" +
                    "</html>";
            helper.setText(htmlMsg, true);

            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }


    @Override
    public void confirmAssignStaff(String token, Integer canteenId) {
        ResetToken resetToken = resetTokenRepository.findByToken(token);
        if (resetToken == null || resetToken.isExpired()) {
            throw new IllegalArgumentException("Invalid or expired token.");
        }

        User user = resetToken.getUser();
        Role staffRole = roleService.findRoleById(2); // ROLE_STAFF
        user.setRole(staffRole);
        user.setCanteen(canteenService.getCanteenById(canteenId)); // Cập nhật canteenId dựa trên thông tin người assign
        userRepository.save(user);

        resetTokenRepository.delete(resetToken);
    }

    @Override
    public void sendAssignManagerEmail(String email, HttpServletRequest request, Integer canteenId) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new IllegalArgumentException("Email này không tồn tại.");
        } else if (user.getRole().getRoleId() != 1 || user.getCanteen() != null) {
            throw new IllegalArgumentException("Nhân viên này đang thuộc canteen khác hoặc không có quyền thích hợp.");
        }

        String token = UUID.randomUUID().toString();
        ResetToken resetToken = new ResetToken(token, user);
        resetTokenRepository.save(resetToken);

        String baseUrl = UrlUtil.getBaseUrl(request);
        String assignUrl = baseUrl + "/assign-manager-confirm?token=" + token + "&canteenId=" + canteenId;

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(email);
            helper.setSubject("Assign Manager Request");

            String htmlMsg = "<html>" +
                    "<body>" +
                    "<p>Xác Nhận Làm Quản Lý Căn Tin, Bấm nút dưới đây:</p>" +
                    "<a href=\"" + assignUrl + "\" style=\"display: inline-block; padding: 10px 20px; font-size: 16px; color: white; background-color: #007bff; text-decoration: none; border-radius: 4px;\">Xác Nhận Quản Lý</a>" +
                    "</body>" +
                    "</html>";
            helper.setText(htmlMsg, true);

            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }

    @Override
    public void confirmAssignManager(String token, Integer canteenId) {
        ResetToken resetToken = resetTokenRepository.findByToken(token);
        if (resetToken == null || resetToken.isExpired()) {
            throw new IllegalArgumentException("Invalid or expired token.");
        }

        User user = resetToken.getUser();
        Role managerRole = roleService.findRoleById(3); // ROLE_MANAGER
        user.setRole(managerRole);
        user.setCanteen(canteenService.getCanteenById(canteenId)); // Cập nhật canteenId dựa trên thông tin người assign
        userRepository.save(user);

        resetTokenRepository.delete(resetToken);
    }


}



