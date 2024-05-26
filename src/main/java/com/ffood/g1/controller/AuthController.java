package com.ffood.g1.controller;

import com.ffood.g1.entity.User;
import com.ffood.g1.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    @Autowired
    private  UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }
    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("invalidCredentials", true);
        }
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam("username") String email,
                        @RequestParam("password") String password,
                        Model model) {
        User user = userService.findByEmail(email);
        if (user != null && new BCryptPasswordEncoder().matches(password, user.getPassword())) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            SecurityContextHolder.getContext().setAuthentication(authentication);
            return "redirect:/home";
        } else {
            return "redirect:/login?error=true";
        }
    }

}
