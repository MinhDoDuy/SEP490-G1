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
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Controller
public class OrderManagementController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @GetMapping("/order-list/{canteenId}")
    public String manageOrders(@PathVariable Integer canteenId,
                               @RequestParam(value = "orderStatus", defaultValue = "PENDING") OrderStatus orderStatus,
                               @RequestParam(value = "page", defaultValue = "0") int page,
                               @RequestParam(value = "size", defaultValue = "10") int size,
                               @RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                               @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                               Model model) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Order> orders;

        List<User> staffList = userService.getStaffByCanteenToShip(canteenId);

        if (orderStatus == OrderStatus.COMPLETE && startDate != null && endDate != null) {
            LocalDateTime start = startDate.atStartOfDay();
            LocalDateTime end = endDate.atTime(LocalTime.MAX);
            orders = orderService.getCompletedOrdersByCanteenAndDateRange(canteenId, start, end, pageable);
        } else {
            switch (orderStatus) {
                case PENDING:
                    orders = orderService.getOrdersByCanteenAndType(canteenId, List.of(OrderStatus.PENDING), OrderType.ONLINE_ORDER, pageable);
                    break;
                case PROGRESS:
                    orders = orderService.getOrdersByCanteen(canteenId, List.of(OrderStatus.PROGRESS), pageable);
                    break;
                case COMPLETE:
                    orders = orderService.getOrdersByCanteen(canteenId, List.of(OrderStatus.COMPLETE), pageable);
                    break;
                case REJECT:
                    orders = orderService.getOrdersByCanteen(canteenId, List.of(OrderStatus.REJECT), pageable);
                    break;
                default:
                    orders = Page.empty();
            }
        }

        model.addAttribute("orders", orders);
        model.addAttribute("staffList", staffList);
        model.addAttribute("canteenId", canteenId);
        model.addAttribute("orderStatus", orderStatus);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);

        return "staff-management/order-list";
    }


    @PostMapping("/update-order-status/{orderId}")
    public String assignShipperAndUpdateStatus(@PathVariable Integer orderId
                                            , @RequestParam Integer deliveryRoleId
                                            , @RequestParam OrderStatus newStatus
                                            , @RequestParam Integer canteenId,
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
            redirectAttributes.addFlashAttribute("error", "Không có đơn hàng nào được chọn");
            return "redirect:/order-list/" + canteenId + "?orderStatus=PENDING";
        }

        try {
            User staffShip = userService.getUserById(deliveryRoleId);
            for (Integer orderId : selectedOrders) {
                orderService.assignShipperAndUpdateStatus(orderId, deliveryRoleId, OrderStatus.PROGRESS, staffShip.getFullName());
            }
            redirectAttributes.addFlashAttribute("message", "Đã gán nhân viên giao hàng và cập nhật trạng thái cho các đơn hàng đã chọn");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
        }

        return "redirect:/order-list/" + canteenId + "?orderStatus=PENDING";
    }

    @PostMapping("/reject-order/{orderId}")
    public String cancelOrder(@PathVariable Integer orderId, @RequestParam Integer canteenId, RedirectAttributes redirectAttributes) {
        orderService.rejectOrder(orderId);
        redirectAttributes.addFlashAttribute("message", "Order cancelled successfully");
        return "redirect:/order-list/" + canteenId;
    }
}
