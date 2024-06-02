package com.ffood.g1.controller;

import com.ffood.g1.entity.User;
import com.ffood.g1.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
public class ProfileController {

    @Autowired
    private UserService userService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @GetMapping("/view-profile/{userId}")
    public String viewProfile(@PathVariable Integer userId, Model model) {
        User user = userService.loadUserById(userId);
        if (user != null) {
            model.addAttribute("user", user);
            return "profile"; // Thymeleaf template name
        } else {
            model.addAttribute("error", "User not found");
            return "error"; // Error page template
        }
    }

    @PostMapping("/update-profile")
    public String updateProfile(@ModelAttribute User user, BindingResult result) {
        if (result.hasErrors()) {
            return "profile"; // Return to profile page if there are validation errors
        }
        userService.updateUser(user);
        return "redirect:/view-profile/" + user.getUserId();
    }


    @GetMapping("/change-password")
    public String showChangePasswordForm() {
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
            return "redirect:/view-profile/" + user.getUserId() ;
        }

        if (passwordEncoder.matches(oldPassword, user.getPassword()) == passwordEncoder.matches(newPassword, user.getPassword())) {
            model.addAttribute("error", "Current password and new password can't the same");
            return "redirect:/view-profile/" + user.getUserId() ;
        }

        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("error", "New passwords do not match");
            return "redirect:/view-profile/" + user.getUserId();
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userService.updatePassword(user, newPassword);
        model.addAttribute("message", "Password changed successfully");


        return "redirect:/login";
    }



}
