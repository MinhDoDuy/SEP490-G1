package com.ffood.g1.controller.adminController;

import com.ffood.g1.entity.Canteen;
import com.ffood.g1.entity.Role;
import com.ffood.g1.entity.User;
import com.ffood.g1.service.CanteenService;
import com.ffood.g1.service.RoleService;
import com.ffood.g1.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
public class UserManageController {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private CanteenService canteenService;

    @GetMapping("/manage-user")
    public String listUsers(Model model,
                            @RequestParam(value = "page", defaultValue = "0") int page,
                            @RequestParam(value = "size", defaultValue = "10") int size) {
        Page<User> userPage = userService.getAllUsers(page, size);
        model.addAttribute("userPage", userPage);
        model.addAttribute("roles", roleService.getAllRoles());
        model.addAttribute("canteens", canteenService.getAllCanteens());
        return "admin-management/manage-user";
    }

    @GetMapping("/search")
    public String searchUsers(Model model,
                              @RequestParam(value = "page", defaultValue = "0") int page,
                              @RequestParam(value = "size", defaultValue = "10") int size,
                              @RequestParam(value = "keyword", required = false) String keyword,
                              @RequestParam(value = "role", required = false) Integer roleId,
                              @RequestParam(value = "canteen", required = false) Integer canteenId) {
        Page<User> userPage;

        if ((keyword == null || keyword.isEmpty()) && roleId == null && canteenId == null) {
            userPage = userService.getAllUsers(page, size);
        } else {
            userPage = userService.searchUsersFilter(keyword, roleId, canteenId, page, size);
        }

        model.addAttribute("userPage", userPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("roleId", roleId);
        model.addAttribute("canteenId", canteenId);
        model.addAttribute("roles", roleService.getAllRoles());
        model.addAttribute("canteens", canteenService.getAllCanteens());

        return "admin-management/manage-user";
    }


    @GetMapping("/edit-user/{userId}")
    public String editUserForm(@PathVariable Integer userId, Model model) {
        User user = userService.getUserById(userId);
        List<Canteen> canteens = canteenService.getAllCanteens(); // Lấy danh sách canteen
        model.addAttribute("user", user);
        model.addAttribute("canteens", canteens); // Truyền danh sách canteen đến view
        return "admin-management/edit-user";
    }

    @PostMapping("/edit-user")
    public String editUserStatus(@RequestParam("userId") Integer userId,
                               @RequestParam("isActive") Boolean isActive,
                               @RequestParam(value = "canteenId", required = false) Integer canteenId,
                               RedirectAttributes redirectAttributes) {

        userService.updateUserStatus(userId, 3, isActive, canteenId); // Luôn luôn role_id = 3
        redirectAttributes.addFlashAttribute("successMessage", "User updated successfully");

        return "redirect:/manage-user";
    }


    @GetMapping("/add-user")
    public String showAddForm(Model model) {
        User user = new User();
        List<Canteen> canteens = canteenService.getAllCanteens(); // Lấy danh sách canteen
        model.addAttribute("user", user);
        model.addAttribute("canteens", canteens); // Truyền danh sách canteen đến view
        return "admin-management/add-user"; // Đường dẫn tới template Thymeleaf để hiển thị form thêm người dùng
    }

    @PostMapping("/add-user")
    public String addUser(@ModelAttribute("user") User user, Model model, RedirectAttributes redirectAttributes) {
        boolean hasErrors = false;

        // Kiểm tra các trường không được rỗng
        if (user.getFullName() == null || user.getFullName().isEmpty()) {
            model.addAttribute("fullNameError", "Full Name cannot be empty.");
            hasErrors = true;
        }

        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            model.addAttribute("emailError", "Email cannot be empty.");
            hasErrors = true;
        } else if (userService.isEmailExist(user.getEmail())) {
            model.addAttribute("emailError", "Email already exists.");
            hasErrors = true;
        }

        if (user.getCodeName() == null || user.getCodeName().isEmpty()) {
            model.addAttribute("codeNameError", "Code Name cannot be empty.");
            hasErrors = true;
        } else if (userService.isCodeNameExist(user.getCodeName())) {
            model.addAttribute("codeNameError", "Code Name already exists.");
            hasErrors = true;
        }

        if (user.getPhone() == null || user.getPhone().isEmpty()) {
            model.addAttribute("phoneError", "Phone cannot be empty.");
            hasErrors = true;
        } else if (userService.isPhoneExist(user.getPhone())) {
            model.addAttribute("phoneError", "Phone already exists.");
            hasErrors = true;
        }

        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            model.addAttribute("passwordError", "Password cannot be empty.");
            hasErrors = true;
        }

        if (hasErrors) {
            model.addAttribute("user", user);
            model.addAttribute("canteens", canteenService.getAllCanteens()); // Pass canteens to the model
            return "admin-management/add-user"; // Change to your actual form view name
        }

        user.setRole(roleService.findRoleById(3)); // Thiết lập role_id = 3 cho Manager
        userService.saveUser(user);

        redirectAttributes.addFlashAttribute("successMessage", "User added successfully");

        return "redirect:/manage-user";
    }




}
