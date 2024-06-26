package com.ffood.g1.controller.adminController;

import com.ffood.g1.entity.User;
import com.ffood.g1.service.CanteenService;
import com.ffood.g1.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class DashBoardController {

    @Autowired
    private UserService userService;

    @Autowired
    private CanteenService canteenService;

    @GetMapping("/dashboard-admin")
    public String showDashboard(Model model) {
        Integer userCount = userService.countUsers();
        Integer canteenCount = canteenService.countCanteens();
        List<User> users = userService.getUsersSortedByCreatedDate();
        model.addAttribute("userCount", userCount);
        model.addAttribute("canteenCount", canteenCount);
        model.addAttribute("users", users);
        return "./admin-management/dashboard";
    }

}
