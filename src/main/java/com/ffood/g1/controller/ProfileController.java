package com.ffood.g1.controller;

import com.ffood.g1.entity.User;
import com.ffood.g1.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@Controller
public class ProfileController {

    @Autowired
    private UserService userService;

    @GetMapping("/view-profile/{userId}")
    public String viewProfile(@PathVariable Integer userId, Model model) {
        User user = userService.loadUserById(userId);
        if (user != null) {
            model.addAttribute("user", user);
            return "profile";
        } else {
            model.addAttribute("error", "User not found");
            return "error";
        }
    }

    @PostMapping("/update-profile")
    public String updateProfile(@ModelAttribute("user") User user, Model model, Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        user.setUserId(currentUser.getUserId()); // Ensure the ID is set to the current user ID
        userService.updateUser(user);
        model.addAttribute("user", user);
        return "redirect:/view-profile/" + user.getUserId();
    }
}