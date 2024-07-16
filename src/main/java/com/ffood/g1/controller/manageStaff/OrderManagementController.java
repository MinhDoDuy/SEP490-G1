package com.ffood.g1.controller.manageStaff;
import com.ffood.g1.entity.Order;
import com.ffood.g1.enum_pay.OrderStatus;
import com.ffood.g1.service.OrderService;
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

    @GetMapping("/order-list/{canteenId}")
    public String getOrdersByCanteen(@PathVariable Integer canteenId, Model model) {
        List<OrderStatus> pendingStatus = Arrays.asList(OrderStatus.PENDING);
        List<OrderStatus> prepareStatus = Arrays.asList(OrderStatus.PREPARE);
        List<OrderStatus> readyStatus = Arrays.asList(OrderStatus.READY);
        List<OrderStatus> completeStatus = Arrays.asList(OrderStatus.COMPLETE);
        List<OrderStatus> cancelStatus = Arrays.asList(OrderStatus.CANCEL);

        List<Order> pendingOrders = orderService.getOrdersByCanteen(canteenId, pendingStatus);
        List<Order> prepareOrders = orderService.getOrdersByCanteen(canteenId, prepareStatus);
        List<Order> readyOrders = orderService.getOrdersByCanteen(canteenId, readyStatus);
        List<Order> completeOrders = orderService.getOrdersByCanteen(canteenId, completeStatus);
        List<Order> cancelOrders = orderService.getOrdersByCanteen(canteenId, cancelStatus);

        model.addAttribute("pendingOrders", pendingOrders);
        model.addAttribute("prepareOrders", prepareOrders);
        model.addAttribute("readyOrders", readyOrders);
        model.addAttribute("completeOrders", completeOrders);
        model.addAttribute("cancelOrders", cancelOrders);

        model.addAttribute("canteenId", canteenId);

        return "staff-management/order-list";
    }

    @PostMapping("/update-order-status/{orderId}")
    public String updateOrderStatus(@PathVariable Integer orderId, @RequestParam OrderStatus newStatus, @RequestParam Integer canteenId, RedirectAttributes redirectAttributes) {
        try {
            orderService.updateOrderStatus(orderId, newStatus);
            redirectAttributes.addFlashAttribute("message", "Order status updated successfully");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/order-list/" + canteenId;
    }

    @PostMapping("/cancel-order/{orderId}")
    public String cancelOrder(@PathVariable Integer orderId, @RequestParam Integer canteenId, RedirectAttributes redirectAttributes) {
        orderService.cancelOrder(orderId);
        redirectAttributes.addFlashAttribute("message", "Order cancelled successfully");
        return "redirect:/order-list/" + canteenId;
    }
}

