package com.ffood.g1.controller;

import com.ffood.g1.entity.User;
import com.ffood.g1.repository.ResetTokenRepository;
import com.ffood.g1.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private ResetTokenRepository resetTokenRepository;

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



    @GetMapping("/forgot-password")
    public String showForgotPasswordForm() {
        return "forgot-password";
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam("email") String email, RedirectAttributes redirectAttributes) {
        try {
            userService.sendResetPasswordEmail(email);
            redirectAttributes.addFlashAttribute("successMessage", "Password reset email sent.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error: " + e.getMessage());
        }
        return "redirect:/forgot-password";
    }

    @GetMapping("/reset-password")
    public String showResetPasswordForm(@RequestParam("token") String token, Model model) {
        if (!userService.isResetTokenValid(token)) {
            model.addAttribute("errorMessage", "Invalid or expired reset token.");
            return "reset-password";
        }

        model.addAttribute("token", token);
        return "reset-password";
    }

    @PostMapping("/reset-password")
    public String processResetPassword(@RequestParam("token") String token,
                                       @RequestParam("password") String password,
                                       RedirectAttributes redirectAttributes) {
        try {
            userService.updatePasswordReset(token, password);
            redirectAttributes.addFlashAttribute("successMessage", "Password reset successful.");
            return "redirect:/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error: " + e.getMessage());
            return "redirect:/reset-password?token=" + token;
        }
    }

}