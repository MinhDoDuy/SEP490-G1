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

        List<Object[]> orderStatsByMonth = orderService.getOrderStatsByCanteenAndMonth(canteenId);
        List<Object[]> orderStatsByYear = orderService.getOrderStatsByCanteenAndYear(canteenId); // Thêm hàm này vào Service

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
        model.addAttribute("revenueLabelsByMonth", revenueLabelsByMonth);
        model.addAttribute("revenueDataByMonth", revenueDataByMonthList);
        model.addAttribute("revenueLabelsByYear", revenueLabelsByYear);
        model.addAttribute("revenueDataByYear", revenueDataByYearList);
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
