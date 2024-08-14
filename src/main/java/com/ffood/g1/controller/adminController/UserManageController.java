package com.ffood.g1.controller.adminController;

import com.ffood.g1.entity.Canteen;
import com.ffood.g1.entity.Role;
import com.ffood.g1.entity.User;
import com.ffood.g1.service.CanteenService;
import com.ffood.g1.service.RoleService;
import com.ffood.g1.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    public String searchUsers(@RequestParam(value = "keyword", required = false) String keyword,
                              @RequestParam(value = "role", required = false) Integer roleId,
                              @RequestParam(value = "canteen", required = false) Integer canteenId,
                              @RequestParam(value = "page", defaultValue = "0") int page,
                              @RequestParam(value = "size", defaultValue = "10") int size,
                              Model model) {
        Pageable pageable = PageRequest.of(page, size);

        // Search users with the provided filters
        Page<User> userPage = userService.searchUsersFilter(keyword, roleId, canteenId, page, size);

        model.addAttribute("userPage", userPage);
        model.addAttribute("roles", roleService.getAllRoles());
        model.addAttribute("canteens", canteenService.getAllCanteens());

        // Preserve search parameters for pagination links
        model.addAttribute("keyword", keyword);
        model.addAttribute("roleId", roleId);
        model.addAttribute("canteenId", canteenId);

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
                               RedirectAttributes redirectAttributes) {

        userService.updateUserStatus(userId, isActive); // Luôn luôn role_id = 3
        redirectAttributes.addFlashAttribute("successMessage", "Cập nhật người dùng thành công!");

        return "redirect:/manage-user";
    }
}
