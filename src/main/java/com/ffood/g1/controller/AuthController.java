package com.ffood.g1.controller;

import com.ffood.g1.entity.User;
import com.ffood.g1.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    // Phương thức hiển thị trang đăng nhập
    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error, Model model) {
        // Nếu có lỗi, thêm thông báo lỗi vào model
        if (error != null) {
            model.addAttribute("errorMessage", "Invalid email or password.");
        }
        return "login"; // Trả về trang đăng nhập
    }

    // Phương thức hiển thị trang đăng ký
    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("user", new User()); // Thêm đối tượng User mới vào model
        return "register"; // Trả về trang đăng ký
    }

    // Phương thức xử lý yêu cầu đăng ký người dùng mới
    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user, Model model) {
        // Kiểm tra nếu email đã tồn tại
        if (userService.isEmailExist(user.getEmail())) {
            model.addAttribute("errorMessage", "Email already in use."); // Thêm thông báo lỗi vào model
            return "register"; // Trả về trang đăng ký
        }
        userService.register(user); // Đăng ký người dùng mới
        return "redirect:/login?success"; // Chuyển hướng đến trang đăng nhập với thông báo thành công
    }

    // Phương thức xử lý yêu cầu đăng nhập người dùng
    @PostMapping("/login")
    public String loginUser(@RequestParam String email, @RequestParam String password, HttpSession session, Model model) {
        User user = userService.authenticate(email, password); // Xác thực người dùng
        if (user == null) {
            return "redirect:/login?error"; // Chuyển hướng lại trang đăng nhập với thông báo lỗi
        }
        session.setAttribute("user", user); // Lưu thông tin người dùng vào session
        return "redirect:/home"; // Chuyển hướng đến trang chủ
    }

    // Phương thức hiển thị trang chủ
    @GetMapping("/home")
    public String home(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user"); // Lấy thông tin người dùng từ session
        if (user != null) {
            model.addAttribute("username", user.getUsername() + " " + user.getUsername()); // Thêm tên người dùng vào model
        }
        return "home"; // Trả về trang chủ
    }
}
