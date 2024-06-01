package com.ffood.g1.controller;

import com.ffood.g1.dto.LoginForm;
import com.ffood.g1.entity.User;
import com.ffood.g1.repository.ResetTokenRepository;
import com.ffood.g1.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Controller // Định nghĩa lớp này là một Spring MVC Controller
public class AuthController {

    private final UserService userService; // Khai báo một biến dịch vụ người dùng, được khởi tạo qua constructor
    private Map<String, User> temporaryUsers = new HashMap<>(); // Khai báo một bản đồ tạm thời để lưu trữ người dùng và OTP


    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private ResetTokenRepository resetTokenRepository;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login") // Xử lý yêu cầu GET tới /login
    public String loginForm(@ModelAttribute("loginForm") LoginForm loginForm, // Tạo một đối tượng LoginForm mới cho view
                            Authentication authentication, // Nhận thông tin xác thực hiện tại
                            Model model,
                            HttpSession session) {
        if (authentication != null && authentication.isAuthenticated()) {
            return "redirect:/home"; // Nếu người dùng đã xác thực, chuyển hướng đến /home
        } else {
            if (session.getAttribute("verificationSuccessMessage") != null) {
                model.addAttribute("successMessage", session.getAttribute("verificationSuccessMessage")); // Thêm thông báo thành công vào model
                session.removeAttribute("verificationSuccessMessage"); // Xóa thông báo khỏi session
            }
            return "login"; // Trả về view login
        }
    }

    @PostMapping("/login") // Xử lý yêu cầu POST tới /login
    public String loginSubmit(@ModelAttribute("loginForm") LoginForm loginForm, HttpSession session) {
        UserDetails userDetails = userService.loadUserByUsername(loginForm.getEmail()); // Lấy chi tiết người dùng dựa trên email
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities()); // Tạo một đối tượng xác thực
        SecurityContextHolder.getContext().setAuthentication(authentication); // Thiết lập xác thực trong ngữ cảnh bảo mật hiện tại
        return "redirect:/home"; // Chuyển hướng đến trang chủ sau khi đăng nhập thành công
    }

    @GetMapping("/forgot-password")
    public String showForgotPasswordForm() {
        return "forgot-password";
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam("email") String email, RedirectAttributes redirectAttributes, HttpServletRequest request) {
        try {
            userService.sendResetPasswordEmail(email, request);
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

    @GetMapping("/register") // Xử lý yêu cầu GET tới /register
    public String showRegistrationForm(Model model,
                                       @RequestParam(required = false) String emailExistsError,
                                       @RequestParam(required = false) String fullName,
                                       @RequestParam(required = false) String password,
                                       @RequestParam(required = false) String codeNameExistsError,
                                       @RequestParam(required = false) String phoneExistsError) {
        model.addAttribute("user", new User()); // Thêm một đối tượng User mới vào model
        model.addAttribute("emailExistsError", emailExistsError); // Thêm thông báo lỗi email vào model
        model.addAttribute("fullName", fullName); // Thêm giá trị của trường fullName vào model để hiển thị lại khi có lỗi
        model.addAttribute("password", password); // Thêm giá trị của trường password vào model để hiển thị lại khi có lỗi
        model.addAttribute("codeNameExistsError", codeNameExistsError); // Thêm giá trị của trường codeName vào model để hiển thị lại khi có lỗi
        model.addAttribute("phoneExistsError", phoneExistsError);// Thêm giá trị của trường phone vào model để hiển thị lại khi có lỗi
        return "register"; // Trả về view register
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user, RedirectAttributes redirectAttributes, HttpServletRequest request) {

        boolean hasError = false;
        String codeNamePattern = "^[a-zA-Z0-9]{6,20}$";

        if (userService.isEmailExist(user.getEmail())) {
            redirectAttributes.addAttribute("emailExistsError", "Email này đã được đăng ký. Vui lòng sử dụng một email khác.");
            hasError = true;
        }

        if (!user.getCodeName().matches(codeNamePattern)) {
            redirectAttributes.addAttribute("codeNameExistsError", "Invalid code name. Please enter letters and numbers between 6-20 characters.");
            hasError = true;
        }

        if (userService.isCodeNameExist(user.getCodeName())) {
            redirectAttributes.addAttribute("codeNameExistsError", "Code Name này đã được đăng ký. Vui lòng sử dụng một code name khác.");
            hasError = true;
        }

        if (userService.isPhoneExist(user.getPhone())) {
            redirectAttributes.addAttribute("phoneExistsError", "Số điện thoại này đã được đăng ký. Vui lòng sử dụng số khác.");
            hasError = true;
        }

        if (hasError) {
            redirectAttributes.addAttribute("fullName", user.getFullName());
            redirectAttributes.addAttribute("password", user.getPassword());
            redirectAttributes.addAttribute("codeName", user.getCodeName());
            redirectAttributes.addAttribute("phone", user.getPhone());
            return "redirect:/register";
        }


        // Xử lý đăng ký người dùng (lưu thông tin người dùng vào cơ sở dữ liệu)
        String otp = generateOTP(6); // Tạo OTP
        temporaryUsers.put(otp, user); // Lưu OTP và người dùng vào bản đồ tạm thời

        try {
            sendOTPByEmail(user.getEmail(), otp); // Gửi OTP qua email
            request.getSession().setAttribute("otp", otp); // Lưu OTP vào session
            user.setCreatedDate(LocalDate.now()); // Đặt ngày tạo cho người dùng
            return "redirect:/register/verify"; // Chuyển hướng đến trang xác minh OTP
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Không gửi được OTP. Vui lòng thử lại."); // Thêm thông báo lỗi gửi OTP vào redirectAttributes
            return "redirect:/register"; // Chuyển hướng lại trang đăng ký
        }
    }

    private String generateOTP(int length) {
        Random random = new Random(); // Tạo đối tượng Random
        StringBuilder otp = new StringBuilder(); // Tạo StringBuilder để xây dựng OTP

        for (int i = 0; i < length; i++) {
            otp.append(random.nextInt(10)); // Thêm một chữ số ngẫu nhiên vào OTP
        }
        return otp.toString(); // Trả về OTP dưới dạng chuỗi
    }

    @GetMapping("/register/verify") // Xử lý yêu cầu GET tới /register/verify
    public String showVerifyOtpForm() {
        return "verify-otp"; // Trả về view verify-otp
    }

    @PostMapping("/register/verify") // Xử lý yêu cầu POST tới /register/verify
    public String processVerifyOtp(HttpServletRequest request,
                                   HttpSession session,
                                   RedirectAttributes redirectAttributes) {
        String otpFromSession = (String) session.getAttribute("otp"); // Lấy OTP từ session
        if (otpFromSession != null && otpFromSession.equals(request.getParameter("otp"))) { // Kiểm tra xem OTP có khớp không
            User user = temporaryUsers.get(otpFromSession); // Lấy người dùng từ bản đồ tạm thời
            userService.registerNewUser(user); // Đăng ký người dùng mới
            temporaryUsers.remove(otpFromSession); // Xóa người dùng khỏi bản đồ tạm thời
            session.setAttribute("verificationSuccessMessage", "Xác minh thành công!"); // Thêm thông báo thành công vào session
            session.removeAttribute("otp"); // Xóa OTP khỏi session
            return "redirect:/login"; // Chuyển hướng đến trang đăng nhập
        } else {
            redirectAttributes.addFlashAttribute("error", "OTP không hợp lệ. Vui lòng thử lại!"); // Thêm thông báo lỗi OTP vào redirectAttributes
            return "redirect:/register/verify"; // Chuyển hướng lại trang xác minh OTP
        }
    }

    public void sendOTPByEmail(String email, String otp) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = mailSender.createMimeMessage(); // Tạo một đối tượng MimeMessage
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom("G1@gmail.com", "G1 Support"); // Đặt địa chỉ email gửi và tên
        helper.setTo(email); // Đặt địa chỉ email nhận

        String subject = "OTP for Registration"; // Đặt tiêu đề email

        String content = "<p>Xin chào,</p>"
                + "<p>OTP của bạn để đăng ký là: <strong>" + otp + "</strong></p>"
                + "<p>Vui lòng sử dụng OTP này để hoàn tất đăng ký của bạn.</p>";

        helper.setSubject(subject); // Đặt tiêu đề email
        helper.setText(content, true); // Đặt nội dung email

        mailSender.send(message); // Gửi email
    }

}