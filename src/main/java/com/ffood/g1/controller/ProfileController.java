package com.ffood.g1.controller;

import com.ffood.g1.entity.Canteen;
import com.ffood.g1.entity.User;
import com.ffood.g1.exception.SpringBootFileUploadException;
import com.ffood.g1.service.FileS3Service;
import com.ffood.g1.service.UserService;
import com.ffood.g1.utils.PhoneUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.security.Principal;

@Controller
public class ProfileController {

    @Autowired
    private UserService userService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private FileS3Service fileS3Service;

    @Autowired
    public void PasswordController(UserService userService) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/view-profile/{userId}")
    public String viewProfile(@PathVariable(required = false) Integer userId, Model model, Principal principal, RedirectAttributes redirectAttributes) {
        if (userId == null) {
            redirectAttributes.addFlashAttribute("error", "user ID is required.");
            return "redirect:/404"; // Chuyển hướng về trang 404 nếu userId là null
        }

        User currentUser = userService.findByEmail(principal.getName());

        // Nếu `userId` không trùng với `userId` của người dùng hiện tại
        if (!currentUser.getUserId().equals(userId)) {
            redirectAttributes.addFlashAttribute("error", "Bạn không có quyền truy cập thông tin của người khác.");
            return "redirect:/view-profile/" + currentUser.getUserId(); // Chuyển hướng về trang profile của người dùng hiện tại
        }

        User user = userService.loadUserById(userId);
        if (user != null) {
            model.addAttribute("user", user);
            return "profile"; // Thymeleaf template name
        } else {
            redirectAttributes.addFlashAttribute("error", "Người dùng không tồn tại.");
            return "redirect:/view-profile/" + currentUser.getUserId(); // Chuyển hướng về trang profile của người dùng hiện tại
        }
    }




    @PostMapping("/update-profile")
    public String updateProfile(@ModelAttribute @Valid User user, BindingResult result,
                                @RequestParam("imageProfileInput") MultipartFile imageProfileInput,
                                Model model)
            throws SpringBootFileUploadException, IOException {

        // Lấy thông tin user hiện tại từ database
        User existingUser = userService.getUserById(user.getUserId());

        // Xác thực số điện thoại
        String phoneValidationResult = PhoneUtils.validatePhoneNumber(user.getPhone());
        if (phoneValidationResult != null) {
            model.addAttribute("phoneError", phoneValidationResult);
            model.addAttribute("existingImage", existingUser.getUserImage());
            return "profile";
        }

        // Kiểm tra nếu số điện thoại thay đổi và đã tồn tại trong database
        if (!existingUser.getPhone().equals(user.getPhone()) && userService.isPhoneExist(user.getPhone())) {
            model.addAttribute("phoneError", "Số điện thoại đã tồn tại.");
            model.addAttribute("existingImage", existingUser.getUserImage());
            return "profile";
        }

        // Xử lý upload ảnh nếu có
        if (imageProfileInput != null && !imageProfileInput.isEmpty()) {
            String userImageUrl = fileS3Service.uploadFile(imageProfileInput);
            user.setUserImage(userImageUrl);
        } else {
            user.setUserImage(existingUser.getUserImage());
        }

        // Cập nhật thông tin user
        userService.updateUser(user);
        model.addAttribute("successMessage", "Cập nhật thành công!");
        return "profile"; // Trả về trang profile
    }

    @GetMapping("/change-password")
    public String showChangePasswordForm(Principal principal, Model model) {
        String email = principal.getName();
        User user = userService.findByEmail(email);
        model.addAttribute("userId", user.getUserId());
        return "change-password";
    }


    @PostMapping("/change-password")
    public String changePassword(@RequestParam("oldPassword") String oldPassword,
                                 @RequestParam("newPassword") String newPassword,
                                 @RequestParam("confirmPassword") String confirmPassword,
                                 Principal principal, Model model) {
        String email = principal.getName();
        User user = userService.findByEmail(email);

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            model.addAttribute("error", "Current password is incorrect");
            return "change-password";
        }

        if (newPassword.length() < 6) {
            model.addAttribute("error", "New password must be at least 6 characters long");
            return "change-password";
        }

        if (passwordEncoder.matches(oldPassword, user.getPassword()) == passwordEncoder.matches(newPassword, user.getPassword())) {
            model.addAttribute("error", "Current password and new password can't be the same");
            return "change-password";
        }

        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("error", "New passwords do not match");
            return "change-password";
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userService.updatePassword(user, newPassword);
        model.addAttribute("successMessage", "Password changed successfully");

        return "change-password";
    }
}