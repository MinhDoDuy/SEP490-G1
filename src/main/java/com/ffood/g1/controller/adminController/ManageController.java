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

import java.util.List;

@Controller
public class ManageController {
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
        return "manage-user";
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

        return "manage-user";
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
        return "redirect:/manage-user";
    }


    @GetMapping("/delete-user/{userId}")
    public String deleteUser(@PathVariable Integer userId) {
        userService.deleteUserById(userId);

        return "redirect:/manage-user"; // Redirect to the users list page
    }


    @GetMapping("/add-user")
    public String showAddForm(Model model) {
        User user = new User();
        List<Role> roles = roleService.getAllRoles();
        model.addAttribute("user", user);
        model.addAttribute("roles", roles);
        return "add-user"; // Đường dẫn tới template Thymeleaf để hiển thị form thêm người dùng
    }

    @PostMapping("/add-user")
    public String addUser(@ModelAttribute("user") User user) {
        userService.saveUser(user);
        return "redirect:/manage-user";
    }


    @GetMapping("/manage-canteen")
    public String listCanteens(@RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "10") int size,
                               Model model) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Canteen> canteenPage = canteenService.getAllCanteensPage(pageable);
        model.addAttribute("canteenPage", canteenPage);
        return "manage-canteen";
    }


    @GetMapping("/add-canteen")
    public String showAddCanteenForm(Model model) {
        model.addAttribute("canteen", new Canteen());
        model.addAttribute("managers", userService.getAllManagers());
        return "add-canteen";
    }

    @PostMapping("/add-canteen")
    public String addCanteen(@ModelAttribute("canteen") Canteen canteen, Model model) {
        canteenService.saveCanteen(canteen);
        return "redirect:/manage-canteen";
    }



}
