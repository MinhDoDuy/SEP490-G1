package com.ffood.g1.controller.manageStaff;

import com.ffood.g1.entity.User;
import com.ffood.g1.service.FoodService;
import com.ffood.g1.service.OrderService;
import com.ffood.g1.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
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
    public String showDashboard(@RequestParam("canteenId") Integer canteenId, Model model, Principal principal, RedirectAttributes redirectAttributes) {
        // Lấy thông tin người dùng hiện tại từ Principal
        User currentUser = userService.findByEmail(principal.getName());

        // Kiểm tra xem `canteenId` có khớp với `canteenId` của `currentUser`
        if (!currentUser.getCanteen().getCanteenId().equals(canteenId)) {
            redirectAttributes.addFlashAttribute("error", "Bạn không có quyền truy cập dashboard của canteen khác.");
            return "redirect:/dashboard-manager?canteenId=" + currentUser.getCanteen().getCanteenId(); // Chuyển hướng về dashboard của canteen mà người dùng quản lý
        }

        Integer staffCount = userService.countStaffByCanteenId(canteenId);
        Integer foodCount = foodService.countFoodByCanteenId(canteenId);
        Integer completedOrdersCount = orderService.countCompletedOrdersByCanteenId(canteenId);

        // Doanh thu online
        List<Object[]> revenueDataByMonthOnline = orderService.findRevenueDataCanteenByMonthOnline(canteenId);
        List<Object[]> revenueDataByYearOnline = orderService.findRevenueDataCanteenByYearOnline(canteenId);

        List<String> revenueLabelsByMonthOnline = new ArrayList<>();
        List<Double> revenueDataByMonthOnlineList = new ArrayList<>();
        for (Object[] data : revenueDataByMonthOnline) {
            revenueLabelsByMonthOnline.add((String) data[0]);
            revenueDataByMonthOnlineList.add((Double) data[1]);
        }

        List<String> revenueLabelsByYearOnline = new ArrayList<>();
        List<Double> revenueDataByYearOnlineList = new ArrayList<>();
        for (Object[] data : revenueDataByYearOnline) {
            revenueLabelsByYearOnline.add((String) data[0]);
            revenueDataByYearOnlineList.add((Double) data[1]);
        }

        // Doanh thu tại quầy
        List<Object[]> revenueDataByMonthAtCounter = orderService.findRevenueDataCanteenByMonthAtCounter(canteenId);
        List<Object[]> revenueDataByYearAtCounter = orderService.findRevenueDataCanteenByYearAtCounter(canteenId);

        List<String> revenueLabelsByMonthAtCounter = new ArrayList<>();
        List<Double> revenueDataByMonthAtCounterList = new ArrayList<>();
        for (Object[] data : revenueDataByMonthAtCounter) {
            revenueLabelsByMonthAtCounter.add((String) data[0]);
            revenueDataByMonthAtCounterList.add((Double) data[1]);
        }

        List<String> revenueLabelsByYearAtCounter = new ArrayList<>();
        List<Double> revenueDataByYearAtCounterList = new ArrayList<>();
        for (Object[] data : revenueDataByYearAtCounter) {
            revenueLabelsByYearAtCounter.add((String) data[0]);
            revenueDataByYearAtCounterList.add((Double) data[1]);
        }

        List<Object[]> orderStatsByMonth = orderService.getOrderStatsByCanteenAndMonth(canteenId);
        List<Object[]> orderStatsByYear = orderService.getOrderStatsByCanteenAndYear(canteenId);

        List<String> orderLabelsByMonth = new ArrayList<>();
        List<Long> orderDataByMonth = new ArrayList<>();
        for (Object[] data : orderStatsByMonth) {
            orderLabelsByMonth.add((String) data[0]);
            orderDataByMonth.add(((Number) data[1]).longValue());
        }

        List<String> orderLabelsByYear = new ArrayList<>();
        List<Long> orderDataByYear = new ArrayList<>();
        for (Object[] data : orderStatsByYear) {
            orderLabelsByYear.add((String) data[0]);
            orderDataByYear.add(((Number) data[1]).longValue());
        }

        List<Object[]> bestSellingItems = orderService.getBestSellingItemsByCanteen(canteenId);

        List<String> bestSellingLabels = new ArrayList<>();
        List<Long> bestSellingData = new ArrayList<>();
        for (Object[] data : bestSellingItems) {
            bestSellingLabels.add((String) data[0]);
            bestSellingData.add(((Number) data[1]).longValue());
        }

        model.addAttribute("staffCount", staffCount);
        model.addAttribute("foodCount", foodCount);
        model.addAttribute("completedOrdersCount", completedOrdersCount);
        model.addAttribute("revenueLabelsByMonthOnline", revenueLabelsByMonthOnline);
        model.addAttribute("revenueDataByMonthOnline", revenueDataByMonthOnlineList);
        model.addAttribute("revenueLabelsByYearOnline", revenueLabelsByYearOnline);
        model.addAttribute("revenueDataByYearOnline", revenueDataByYearOnlineList);
        model.addAttribute("revenueLabelsByMonthAtCounter", revenueLabelsByMonthAtCounter);
        model.addAttribute("revenueDataByMonthAtCounter", revenueDataByMonthAtCounterList);
        model.addAttribute("revenueLabelsByYearAtCounter", revenueLabelsByYearAtCounter);
        model.addAttribute("revenueDataByYearAtCounter", revenueDataByYearAtCounterList);
        model.addAttribute("orderLabelsByMonth", orderLabelsByMonth);
        model.addAttribute("orderDataByMonth", orderDataByMonth);
        model.addAttribute("orderLabelsByYear", orderLabelsByYear);
        model.addAttribute("orderDataByYear", orderDataByYear);
        model.addAttribute("bestSellingLabels", bestSellingLabels);
        model.addAttribute("bestSellingData", bestSellingData);
        model.addAttribute("canteenId", canteenId);

        return "staff-management/dashboard-manager";
    }
}
