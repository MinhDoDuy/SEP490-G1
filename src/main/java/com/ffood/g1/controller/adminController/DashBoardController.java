package com.ffood.g1.controller.adminController;

import com.ffood.g1.service.CanteenService;
import com.ffood.g1.service.OrderService;
import com.ffood.g1.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.text.DecimalFormat;
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
        Double totalRevenue = orderService.calculateTotalRevenue();
        String formattedRevenue = new DecimalFormat("#,###").format(totalRevenue);

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

        List<Object[]> revenueDataByDay = orderService.getRevenueDataByDay();
        List<Object[]> revenueDataByMonth = orderService.getRevenueDataByMonth();
        List<Object[]> revenueDataByYear = orderService.getRevenueDataByYear();

        List<String> revenueLabelsByDay = new ArrayList<>();
        List<Double> revenueDataByDayList = new ArrayList<>();
        for (Object[] data : revenueDataByDay) {
            revenueLabelsByDay.add((String) data[0]);
            revenueDataByDayList.add((Double) data[1]);
        }

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

        model.addAttribute("userCount", userCount);
        model.addAttribute("totalRevenue", formattedRevenue);
        model.addAttribute("canteenCount", canteenCount);
        model.addAttribute("bestSellingItemsLabels", bestSellingItemsLabels);
        model.addAttribute("bestSellingItemsData", bestSellingItemsData);
        model.addAttribute("orderStatsLabels", orderStatsLabels);
        model.addAttribute("orderStatsData", orderStatsData);
        model.addAttribute("revenueLabelsByDay", revenueLabelsByDay);
        model.addAttribute("revenueDataByDay", revenueDataByDayList);
        model.addAttribute("revenueLabelsByMonth", revenueLabelsByMonth);
        model.addAttribute("revenueDataByMonth", revenueDataByMonthList);
        model.addAttribute("revenueLabelsByYear", revenueLabelsByYear);
        model.addAttribute("revenueDataByYear", revenueDataByYearList);

        return "admin-management/dashboard";
    }
}
