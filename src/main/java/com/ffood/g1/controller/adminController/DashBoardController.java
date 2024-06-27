package com.ffood.g1.controller.adminController;

import com.ffood.g1.service.OrderService;
import com.ffood.g1.service.UserService;
import com.ffood.g1.service.CanteenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
public class DashBoardController {

    @Autowired
    private UserService userService;

    @Autowired
    private CanteenService canteenService;

    @Autowired
    private OrderService orderService;

    @GetMapping("/dashboard-admin")
    public String showDashboard(Model model) {
        Integer userCount = userService.countUsers();
        Integer canteenCount = canteenService.countCanteens();

        List<Object[]> bestSellingItems = orderService.getBestSellingItems();
        List<Object[]> orderStats = orderService.getOrderStats();

        List<String> bestSellingItemsLabels = new ArrayList<>();
        List<Long> bestSellingItemsData = new ArrayList<>();
        for (Object[] item : bestSellingItems) {
            bestSellingItemsLabels.add((String) item[0]);
            bestSellingItemsData.add((Long) item[1]);
        }

        List<String> orderStatsLabels = new ArrayList<>();
        List<Long> orderStatsData = new ArrayList<>();
        for (Object[] stat : orderStats) {
            orderStatsLabels.add((String) stat[0]);
            orderStatsData.add((Long) stat[1]);
        }

        model.addAttribute("userCount", userCount);
        model.addAttribute("canteenCount", canteenCount);
        model.addAttribute("bestSellingItemsLabels", bestSellingItemsLabels);
        model.addAttribute("bestSellingItemsData", bestSellingItemsData);
        model.addAttribute("orderStatsLabels", orderStatsLabels);
        model.addAttribute("orderStatsData", orderStatsData);

        return "admin-management/dashboard";
    }
}
