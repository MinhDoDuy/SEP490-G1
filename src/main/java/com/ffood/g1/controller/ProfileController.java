package com.ffood.g1.controller;

import com.ffood.g1.entity.User;
import com.ffood.g1.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

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
    public String updateProfile(@ModelAttribute User user, Model model) {
        userService.updateUser(user);
        return "redirect:/view-profile/" + user.getUserId();
    }
}
