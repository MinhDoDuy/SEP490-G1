package com.ffood.g1.controller.adminController;

import com.ffood.g1.entity.Role;
import com.ffood.g1.entity.User;
import com.ffood.g1.service.RoleService;
import com.ffood.g1.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
public class UserManageController {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;
    @GetMapping("/manage-user")
    public String listUsers(Model model,
                            @RequestParam(value = "page", defaultValue = "0") int page,
                            @RequestParam(value = "size", defaultValue = "10") int size) {
        Page<User> userPage = userService.getAllUsers(page, size);
        model.addAttribute("userPage", userPage);
        return "./admin-management/manage-user";
    }

    @GetMapping("/search")
    public String searchUsers(Model model,
                              @RequestParam(value = "page", defaultValue = "0") int page,
                              @RequestParam(value = "size", defaultValue = "10") int size,
                              @RequestParam(value = "keyword", required = false) String keyword) {
        Page<User> userPage;

        if (keyword == null || keyword.isEmpty()) {
            userPage = userService.getAllUsers(page, size);
        } else {
            userPage = userService.searchUsers(keyword, page, size);
        }
        model.addAttribute("userPage", userPage);
        model.addAttribute("keyword", keyword);

        return "./admin-management/manage-user";
    }


    @GetMapping("/edit-role/{userId}")
    public String editUserForm(@PathVariable Integer userId, Model model) {
        User user = userService.getUserById(userId);
        List<Role> roles = roleService.findRolesExcludingAdmin();
        model.addAttribute("user", user);
        model.addAttribute("roles", roles);
        return "./admin-management/editRoleUser";
    }

    @PostMapping("/edit-role")
    public String updateUserRole(@RequestParam Integer userId, @RequestParam Integer roleId) {
        userService.updateUserRole(userId, roleId);
        return "redirect:/manage-user";
    }


    @GetMapping("/delete-user/{userId}")
    public String deleteUser(@PathVariable Integer userId, Model model) {
        userService.deleteUserById(userId);
        return "redirect:/manage-user"; // Redirect to the users list page
    }


    @GetMapping("/add-user")
    public String showAddForm(Model model) {
        User user = new User();
        List<Role> roles = roleService.findRolesExcludingAdmin();

        model.addAttribute("user", user);
        model.addAttribute("roles", roles);
        return "./admin-management/add-user"; // Đường dẫn tới template Thymeleaf để hiển thị form thêm người dùng
    }

    @PostMapping("/add-user")
    public String addUser(@ModelAttribute("user") User user, Model model) {
        boolean hasErrors = false;

        if (userService.isEmailExist(user.getEmail())) {
            model.addAttribute("emailError", "Email already exists.");
            hasErrors = true;
        }

        if (userService.isCodeNameExist(user.getCodeName())) {
            model.addAttribute("codeNameError", "Code Name already exists.");
            hasErrors = true;
        }

        if (userService.isPhoneExist(user.getPhone())) {
            model.addAttribute("phoneError", "Phone already exists.");
            hasErrors = true;
        }

        if (hasErrors) {
            model.addAttribute("user", user);
            model.addAttribute("roles", roleService.getAllRoles()); // Make sure to pass roles to the model
            return "./admin-management/add-user"; // Change to your actual form view name
        }
        user.setCreatedDate(LocalDate.now());

        userService.saveUser(user);
        return "redirect:/manage-user";
    }


}
