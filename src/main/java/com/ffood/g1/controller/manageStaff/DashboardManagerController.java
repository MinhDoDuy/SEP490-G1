package com.ffood.g1.controller.manageStaff;

import com.ffood.g1.service.FoodService;
import com.ffood.g1.service.OrderService;
import com.ffood.g1.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
public class DashboardManagerController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @Autowired
    private FoodService foodService;

    @GetMapping("/dashboard-manager")
    public String showDashboard(@RequestParam("canteenId") Integer canteenId, Model model) {
        Integer staffCount = userService.countStaffByCanteenId(canteenId);
        Integer foodCount = foodService.countFoodByCanteenId(canteenId);
        Integer completedOrdersCount = orderService.countCompletedOrdersByCanteenId(canteenId);

        List<Object[]> revenueDataByMonth = orderService.findRevenueDataCanteenByMonth(canteenId);
        List<Object[]> revenueDataByYear = orderService.findRevenueDataCanteenByYear(canteenId);

        List<String> revenueLabelsByMonth = new ArrayList<>();
        List<Double> revenueDataByMonthList = new ArrayList<>();
        for (Object[] data : revenueDataByMonth) {
            revenueLabelsByMonth.add((String) data[0]);
            revenueDataByMonthList.add((Double) data[1]);
        }

        List<String> revenueLabelsByYear = new ArrayList<>();
        List<Double> revenueDataByYearList = new ArrayList<>();
        for (Object[] data : revenueDataByYear) {
            revenueLabelsByYear.add((String) data[0]);
            revenueDataByYearList.add((Double) data[1]);
        }

        model.addAttribute("staffCount", staffCount);
        model.addAttribute("foodCount", foodCount);
        model.addAttribute("completedOrdersCount", completedOrdersCount);
        model.addAttribute("revenueLabelsByMonth", revenueLabelsByMonth);
        model.addAttribute("revenueDataByMonth", revenueDataByMonthList);
        model.addAttribute("revenueLabelsByYear", revenueLabelsByYear);
        model.addAttribute("revenueDataByYear", revenueDataByYearList);
        model.addAttribute("canteenId", canteenId);

        return "staff-management/dashboard-manager";
    }
}
