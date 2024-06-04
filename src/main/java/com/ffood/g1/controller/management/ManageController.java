package com.ffood.g1.controller.management;

import com.ffood.g1.entity.Role;
import com.ffood.g1.entity.User;
import com.ffood.g1.service.RoleService;
import com.ffood.g1.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class ManageController {
    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;


    @GetMapping("/manager-user")
    public String listUsers(Model model,
                            @RequestParam(value = "page", defaultValue = "0") int page,
                            @RequestParam(value = "size", defaultValue = "10") int size) {
        Page<User> userPage = userService.getAllUsers(page, size);
        model.addAttribute("userPage", userPage);
        return "manager-user";
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

        return "manager-user";
    }


    @GetMapping("/edit-role/{userId}")
    public String editUserForm(@PathVariable Integer userId, Model model) {
        User user = userService.getUserById(userId);
        List<Role> roles = roleService.getAllRoles();
        model.addAttribute("user", user);
        model.addAttribute("roles", roles);
        return "editRoleUser";
    }

    @PostMapping("/edit-role")
    public String updateUserRole(@RequestParam Integer userId, @RequestParam Integer roleId) {
        userService.updateUserRole(userId, roleId);
        return "redirect:/manager-user";
    }

}
