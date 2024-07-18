package com.ffood.g1.controller.manageStaff;
import com.ffood.g1.entity.Order;
import com.ffood.g1.entity.User;
import com.ffood.g1.enum_pay.OrderStatus;
import com.ffood.g1.enum_pay.OrderType;
import com.ffood.g1.service.OrderService;
import com.ffood.g1.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
@Controller
public class OrderManagementController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;


    @GetMapping("/order-list/{canteenId}")
    public String getOrdersByCanteen(@PathVariable Integer canteenId, Model model) {
        List<OrderStatus> pendingStatus = Arrays.asList(OrderStatus.PENDING);
        List<OrderStatus> progressStatus = Arrays.asList(OrderStatus.PROGRESS);
        List<OrderStatus> completeStatus = Arrays.asList(OrderStatus.COMPLETE);
        List<OrderStatus> cancelStatus = Arrays.asList(OrderStatus.REJECT);

        List<Order> pendingOrders = orderService.getOrdersByCanteenAndType(canteenId, pendingStatus, OrderType.ONLINE_ORDER);
        List<Order> progressOrder = orderService.getOrdersByCanteen(canteenId, progressStatus);
        List<Order> completeOrders = orderService.getOrdersByCanteen(canteenId, completeStatus);
        List<Order> cancelOrders = orderService.getOrdersByCanteen(canteenId, cancelStatus);

        List<User> staffList = userService.getStaffByCanteenToShip(canteenId);

        model.addAttribute("pendingOrders", pendingOrders);
        model.addAttribute("progressOrder", progressOrder);
        model.addAttribute("completeOrders", completeOrders);
        model.addAttribute("cancelOrders", cancelOrders);
        model.addAttribute("staffList", staffList);
        model.addAttribute("canteenId", canteenId);

        return "staff-management/order-list";
    }

    @PostMapping("/update-order-status/{orderId}")
    public String assignShipperAndUpdateStatus(@PathVariable Integer orderId, @RequestParam Integer deliveryRoleId, @RequestParam OrderStatus newStatus, @RequestParam Integer canteenId, RedirectAttributes redirectAttributes) {
        try {
            orderService.assignShipperAndUpdateStatus(orderId, deliveryRoleId, newStatus);
            redirectAttributes.addFlashAttribute("message", "Shipper assigned and order status updated successfully");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/order-list/" + canteenId;
    }

    @PostMapping("/reject-order/{orderId}")
    public String cancelOrder(@PathVariable Integer orderId, @RequestParam Integer canteenId, RedirectAttributes redirectAttributes) {
        orderService.rejectOrder(orderId);
        redirectAttributes.addFlashAttribute("message", "Order cancelled successfully");
        return "redirect:/order-list/" + canteenId;
    }
}

