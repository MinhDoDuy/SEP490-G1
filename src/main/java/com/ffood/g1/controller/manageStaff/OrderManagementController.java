package com.ffood.g1.controller.manageStaff;
import com.ffood.g1.entity.Order;
import com.ffood.g1.entity.User;
import com.ffood.g1.enum_pay.OrderStatus;
import com.ffood.g1.enum_pay.OrderType;
import com.ffood.g1.service.OrderService;
import com.ffood.g1.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    public String manageOrders(@PathVariable Integer canteenId,
                               @RequestParam(value = "page", defaultValue = "0") int page,
                               @RequestParam(value = "size", defaultValue = "10") int size,
                               Model model) {
        Pageable pageable = PageRequest.of(page, size);
        List<OrderStatus> pendingStatus = Arrays.asList(OrderStatus.PENDING);
        List<OrderStatus> progressStatus = Arrays.asList(OrderStatus.PROGRESS);
        List<OrderStatus> completeStatus = Arrays.asList(OrderStatus.COMPLETE);
        List<OrderStatus> cancelStatus = Arrays.asList(OrderStatus.REJECT);

        Page<Order> pendingOrders = orderService.getOrdersByCanteenAndType(canteenId, pendingStatus, OrderType.ONLINE_ORDER, pageable);
        Page<Order> progressOrders = orderService.getOrdersByCanteen(canteenId, progressStatus, pageable);
        Page<Order> completeOrders = orderService.getOrdersByCanteen(canteenId, completeStatus, pageable);
        Page<Order> cancelOrders = orderService.getOrdersByCanteen(canteenId, cancelStatus, pageable);

        List<User> staffList = userService.getStaffByCanteenToShip(canteenId);

        model.addAttribute("pendingOrders", pendingOrders);
        model.addAttribute("progressOrders", progressOrders);
        model.addAttribute("completeOrders", completeOrders);
        model.addAttribute("cancelOrders", cancelOrders);
        model.addAttribute("staffList", staffList);
        model.addAttribute("canteenId", canteenId);

        return "staff-management/order-list";
    }


    @PostMapping("/update-order-status/{orderId}")
    public String assignShipperAndUpdateStatus(@PathVariable Integer orderId, @RequestParam Integer deliveryRoleId, @RequestParam OrderStatus newStatus, @RequestParam Integer canteenId,
            RedirectAttributes redirectAttributes) {

        try {
            User staffShip = userService.getUserById(deliveryRoleId);
            orderService.assignShipperAndUpdateStatus(orderId, deliveryRoleId, newStatus, staffShip.getFullName());
            redirectAttributes.addFlashAttribute("message", "Shipper assigned and order status updated successfully");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/order-list/" + canteenId;
    }

    @PostMapping("/bulk-assign-orders")
    public String bulkAssignOrders(@RequestParam(value = "selectedOrders", required = false) List<Integer> selectedOrders,
                                   @RequestParam("bulkDeliveryRoleId") Integer deliveryRoleId,
                                   @RequestParam("canteenId") Integer canteenId,
                                   RedirectAttributes redirectAttributes) {
        if (selectedOrders == null || selectedOrders.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "No orders selected");
            return "redirect:/order-list/" + canteenId;
        }
        for (Integer orderId : selectedOrders) {
            orderService.assignShipperAndUpdateStatus(orderId, deliveryRoleId, OrderStatus.PROGRESS, userService.getUserById(deliveryRoleId).getFullName());
        }
        redirectAttributes.addFlashAttribute("message", "Shipper assigned and order status updated successfully for selected orders");
        return "redirect:/order-list/" + canteenId;
    }

    @PostMapping("/reject-order/{orderId}")
    public String cancelOrder(@PathVariable Integer orderId, @RequestParam Integer canteenId, RedirectAttributes redirectAttributes) {
        orderService.rejectOrder(orderId);
        redirectAttributes.addFlashAttribute("message", "Order cancelled successfully");
        return "redirect:/order-list/" + canteenId;
    }
}

