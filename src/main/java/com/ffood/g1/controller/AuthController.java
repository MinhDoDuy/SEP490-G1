package com.ffood.g1.controller;

import com.ffood.g1.dto.LoginForm;
import com.ffood.g1.dto.UserDTO;
import com.ffood.g1.entity.User;
import com.ffood.g1.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Controller
public class AuthController {

    private final UserService userService;
    private Map<String, User> temporaryUsers = new HashMap<>();
    @Autowired
    private JavaMailSender mailSender;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @Controller
    public class LoginController {

        @GetMapping("/login")
        public String loginForm(@ModelAttribute("loginForm") LoginForm loginForm, Authentication authentication, Model model, HttpSession session) {
            if (authentication != null && authentication.isAuthenticated()) {
                return "redirect:/home";
            } else {
                if (session.getAttribute("verificationSuccessMessage") != null) {
                    model.addAttribute("successMessage", session.getAttribute("verificationSuccessMessage"));
                    session.removeAttribute("verificationSuccessMessage");
                }
                return "login";
            }
        }

        @PostMapping("/login")
        public String loginSubmit(@ModelAttribute("loginForm") LoginForm loginForm, HttpSession session) {
            UserDetails userDetails = userService.loadUserByUsername(loginForm.getEmail());
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            return "redirect:/home";
        }


        @GetMapping("/register")
        public String showRegistrationForm(Model model) {
            model.addAttribute("user", new User());
            return "register";
        }

        @PostMapping("/register")
        public String registerUser(@ModelAttribute("user") User user, HttpServletRequest request, RedirectAttributes redirectAttributes, Model model) {
            if (userService.isEmailExist(user.getEmail())) {
                redirectAttributes.addAttribute("emailExistsError", "Email này đã được đăng ký. Vui lòng sử dụng một email khác.");
                redirectAttributes.addAttribute("fullName", user.getFullName());
                redirectAttributes.addAttribute("password", user.getPassword());
                redirectAttributes.addAttribute("codeName", user.getCodeName());
                redirectAttributes.addAttribute("phone", user.getPhone());
                return "redirect:/register";
            }

            String otp = generateOTP(6);
            temporaryUsers.put(otp, user);

            try {
                sendOTPByEmail(user.getEmail(), otp);
                // save otp into session
                request.getSession().setAttribute("otp", otp);
                user.setCreatedDate(LocalDate.now());
                return "redirect:/register/verify";
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("error", "Không gửi được OTP. Vui lòng thử lại.");
                return "redirect:/register";
            }
        }

        private String generateOTP(int length) {
            Random random = new Random();
            StringBuilder otp = new StringBuilder();

            for (int i = 0; i < length; i++) {
                otp.append(random.nextInt(10));
            }
            return otp.toString();
        }

        @GetMapping("/register/verify")
        public String showVerifyOtpForm() {
            return "verify-otp";
        }

        @PostMapping("/register/verify")
        public String processVerifyOtp(HttpServletRequest request, HttpSession session, RedirectAttributes redirectAttributes) {
            String otpFromSession = (String) session.getAttribute("otp");
            if (otpFromSession != null && otpFromSession.equals(request.getParameter("otp"))) {
                User user = temporaryUsers.get(otpFromSession);
                userService.registerNewUser(user);
                temporaryUsers.remove(otpFromSession);

                session.setAttribute("verificationSuccessMessage", "Xác minh thành công !");
                session.removeAttribute("otp");
                return "redirect:/login";
            } else {
                redirectAttributes.addFlashAttribute("error", "OTP không hợp lệ. Vui lòng thử lại !");
                return "redirect:/register/verify";
            }
        }

        public void sendOTPByEmail(String email, String otp) throws MessagingException, UnsupportedEncodingException {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message);

            helper.setFrom("wewe.support@gmail.com", "WeWe Support");
            helper.setTo(email);

            String subject = "OTP for Registration";

            String content = "<p>Xin chào,</p>"
                    + "<p>OTP của bạn để đăng ký là: <strong>" + otp + "</strong></p>"
                    + "<p>Vui lòng sử dụng OTP này để hoàn tất đăng ký của bạn.</p>";

            helper.setSubject(subject);
            helper.setText(content, true);

            mailSender.send(message);
        }


        @GetMapping("/home")
        public String home(Model model) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return "redirect:/login";
            }
            String email = authentication.getName();
            User user = userService.findByEmail(email);
            if (user == null) {
                return "redirect:/login?error=true";
            }
            model.addAttribute("user", user);
            return "home";
        }
    }
}
