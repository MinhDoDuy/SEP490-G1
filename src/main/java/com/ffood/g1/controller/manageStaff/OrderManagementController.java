package com.ffood.g1.controller.manageStaff;

import com.ffood.g1.entity.Canteen;
import com.ffood.g1.entity.Order;
import com.ffood.g1.entity.User;
import com.ffood.g1.enum_pay.OrderStatus;
import com.ffood.g1.enum_pay.OrderType;
import com.ffood.g1.service.CanteenService;
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

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Controller
public class OrderManagementController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @Autowired
    private CanteenService canteenService;

    @GetMapping("/order-list/{canteenId}")
    public String manageOrders(@PathVariable Integer canteenId,
                               @RequestParam(value = "orderStatus", defaultValue = "PENDING") OrderStatus orderStatus,
                               @RequestParam(value = "keyword", required = false) String keyword,
                               @RequestParam(value = "page", defaultValue = "0") int page,
                               @RequestParam(value = "size", defaultValue = "10") int size,
                               @RequestParam(value = "startDate", required = false)
                               @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Optional<LocalDate> startDate,
                               @RequestParam(value = "endDate", required = false)
                               @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Optional<LocalDate> endDate,
                               Model model, Principal principal, RedirectAttributes redirectAttributes) {

        User currentUser = userService.findByEmail(principal.getName());
        if (!isAuthorizedUser(currentUser, canteenId)) {
            redirectAttributes.addFlashAttribute("error", "Bạn không có quyền truy cập đơn hàng của canteen khác.");
            return "redirect:/order-list/" + currentUser.getCanteen().getCanteenId();
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<Order> orders = fetchOrders(canteenId, orderStatus, keyword, startDate, endDate, pageable, model);

        model.addAttribute("orders", orders);
        populateModel(model, canteenId, orderStatus, keyword, startDate.orElse(null), endDate.orElse(null));

        return "staff-management/order-list";
    }

    @PostMapping("/update-order-status/{orderId}")
    public String assignShipperAndUpdateStatus(@PathVariable Integer orderId,
                                               @RequestParam Integer deliveryRoleId,
                                               @RequestParam OrderStatus newStatus,
                                               @RequestParam Integer canteenId,
                                               RedirectAttributes redirectAttributes) {
        try {
            User staffShip = userService.getUserById(deliveryRoleId);
            orderService.assignShipperAndUpdateStatus(orderId, deliveryRoleId, newStatus, staffShip.getFullName());
            redirectAttributes.addFlashAttribute("message", "Chuyển đơn hàng cho shipper thành công");
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
            orderService.bulkAssignAndUpdateOrders(selectedOrders, deliveryRoleId, staffShip.getFullName());
            redirectAttributes.addFlashAttribute("message", "Chuyển đơn hàng cho shipper thành công cho các đơn hàng đã chọn");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
        }

        return "redirect:/order-list/" + canteenId + "?orderStatus=PENDING";
    }

    @PostMapping("/reject-order")
    public String cancelOrder(@RequestParam Integer orderId,
                              @RequestParam Integer canteenId,
                              @RequestParam String note,
                              RedirectAttributes redirectAttributes) {
        orderService.rejectOrder(orderId, note);
        redirectAttributes.addFlashAttribute("message", "Đơn đã bị hủy với lý do: " + note);
        return "redirect:/order-list/" + canteenId;
    }

    @PostMapping("/refund-order/{orderId}")
    public String refundOrder(@PathVariable Integer orderId,
                              @RequestParam Integer canteenId,
                              @RequestParam String refundReason,
                              RedirectAttributes redirectAttributes) {
        orderService.refundOrder(orderId, refundReason);
        redirectAttributes.addFlashAttribute("message", "Đơn hàng đã được hoàn tiền với lý do: " + refundReason);
        return "redirect:/order-list/" + canteenId + "?orderStatus=REJECT";
    }

    @GetMapping("/order-list-ship/{canteenId}")
    public String getOrdersForShipper(@PathVariable Integer canteenId,
                                      @RequestParam(value = "deliveryRoleId", required = false) Integer deliveryRoleId,
                                      @RequestParam(value = "status", defaultValue = "PROGRESS") String status,
                                      @RequestParam(value = "page", defaultValue = "0") int page,
                                      @RequestParam(value = "size", defaultValue = "10") int size,
                                      Model model, Principal principal, RedirectAttributes redirectAttributes) {

        if (deliveryRoleId == null) {
            redirectAttributes.addFlashAttribute("error", "Delivery Role ID is required.");
            return "redirect:/404";
        }

        User currentUser = userService.findByEmail(principal.getName());
        if (!isAuthorizedUser(currentUser, canteenId)) {
            redirectAttributes.addFlashAttribute("error", "Bạn không có quyền truy cập đơn hàng của canteen khác.");
            return "redirect:/order-list-ship/" + currentUser.getCanteen().getCanteenId() + "?deliveryRoleId=" + currentUser.getUserId();
        }

        if (!isAuthorizedDeliveryRole(currentUser, deliveryRoleId, canteenId)) {
            redirectAttributes.addFlashAttribute("error", "Bạn không có quyền truy cập đơn hàng của người khác trong cùng canteen.");
            return "redirect:/order-list-ship/" + canteenId + "?deliveryRoleId=" + currentUser.getUserId();
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<Order> orders = "COMPLETE".equalsIgnoreCase(status) ?
                orderService.getCompleteOrdersByCanteenAndDeliveryRole(canteenId, deliveryRoleId, pageable) :
                orderService.getOrdersByCanteenAndDeliveryRole(canteenId, deliveryRoleId, pageable);

        model.addAttribute("orders", orders);
        model.addAttribute("canteenId", canteenId);
        model.addAttribute("deliveryRoleId", deliveryRoleId);
        model.addAttribute("status", status);
        return "shipper/order-list";
    }

    @PostMapping("/complete-order/{orderId}")
    public String completeOrder(@PathVariable Integer orderId,
                                @RequestParam Integer canteenId,
                                @RequestParam Integer userId,
                                RedirectAttributes redirectAttributes) {
        orderService.completeOrder(orderId);
        redirectAttributes.addFlashAttribute("successMessage", "Đơn được gửi hoàn thành");
        return "redirect:/order-list-ship/" + canteenId + "?deliveryRoleId=" + userId;
    }

    @PostMapping("/reject-order-ship")
    public String cancelOrderShip(@RequestParam Integer orderId,
                                  @RequestParam Integer canteenId,
                                  @RequestParam String note,
                                  @RequestParam Integer userId,
                                  RedirectAttributes redirectAttributes) {
        orderService.rejectOrder(orderId, note);
        redirectAttributes.addFlashAttribute("successMessage", "Đơn đã bị hủy với lý do: " + note);
        return "redirect:/order-list-ship/" + canteenId + "?deliveryRoleId=" + userId;
    }

    private boolean isAuthorizedUser(User currentUser, Integer canteenId) {
        return currentUser.getCanteen().getCanteenId().equals(canteenId);
    }

    private boolean isAuthorizedDeliveryRole(User currentUser, Integer deliveryRoleId, Integer canteenId) {
        User deliveryUser = userService.loadUserById(deliveryRoleId);
        return deliveryUser != null &&
                deliveryUser.getCanteen().getCanteenId().equals(canteenId) &&
                deliveryUser.getRole().getRoleId() == 2 &&
                deliveryUser.getUserId().equals(currentUser.getUserId());
    }

    private Page<Order> fetchOrders(Integer canteenId, OrderStatus orderStatus, String keyword,
                                    Optional<LocalDate> startDate, Optional<LocalDate> endDate,
                                    Pageable pageable, Model model) {
        Page<Order> orders;
        if (orderStatus == OrderStatus.COMPLETE && (startDate.isPresent() || endDate.isPresent())) {
            orders = fetchCompletedOrders(canteenId, startDate, endDate, pageable, model);
        } else if (orderStatus == OrderStatus.REJECT && keyword != null && !keyword.isEmpty()) {
            orders = orderService.searchRejectedOrdersByOrderCode(canteenId, keyword, pageable);
        } else if (orderStatus == OrderStatus.REJECT) {
            orders = orderService.getOrdersByCanteen(canteenId, List.of(OrderStatus.REJECT), pageable);
        } else if (orderStatus == OrderStatus.REFUND && keyword != null && !keyword.isEmpty()) {
            orders = orderService.searchRefundedOrdersByOrderCode(canteenId, keyword, pageable);
        } else if (orderStatus == OrderStatus.REFUND) {
            orders = orderService.getOrdersByCanteen(canteenId, List.of(OrderStatus.REFUND), pageable);
        } else if (orderStatus == OrderStatus.PROGRESS && keyword != null && !keyword.isEmpty()) {
            orders = orderService.searchProgressOrdersByOrderCode(canteenId, keyword, pageable); // Giả sử bạn đã có phương thức tìm kiếm cho PROGRESS
        } else if (orderStatus == OrderStatus.PROGRESS) {
            orders = orderService.getOrdersByCanteen(canteenId, List.of(OrderStatus.PROGRESS), pageable);
        } else {
            orders = fetchPendingOrProgressOrders(canteenId, orderStatus, pageable);
        }

        return orders;
    }

    private Page<Order> fetchCompletedOrders(Integer canteenId, Optional<LocalDate> startDate,
                                             Optional<LocalDate> endDate, Pageable pageable, Model model) {
        LocalDateTime start = startDate.orElse(LocalDate.MIN).atStartOfDay();
        LocalDateTime end = endDate.orElse(LocalDate.MAX).atTime(LocalTime.MAX);
        if (start.isAfter(end)) {
            model.addAttribute("error", "Ngày bắt đầu không thể sau ngày kết thúc");
            return Page.empty();
        }
        return orderService.getCompletedOrdersByCanteenAndDateRange(canteenId, start, end, pageable);
    }

    private Page<Order> fetchPendingOrProgressOrders(Integer canteenId, OrderStatus orderStatus, Pageable pageable) {
        if (orderStatus == OrderStatus.PENDING) {
            return orderService.getOrdersByCanteenAndType(canteenId, List.of(OrderStatus.PENDING), OrderType.ONLINE_ORDER, pageable);
        } else if (orderStatus == OrderStatus.PROGRESS) {
            return orderService.getOrdersByCanteen(canteenId, List.of(OrderStatus.PROGRESS), pageable);
        } else if (orderStatus == OrderStatus.COMPLETE) {
            return orderService.getOrdersByCanteen(canteenId, List.of(OrderStatus.COMPLETE), pageable);
        } else {
            return Page.empty();
        }
    }

    private void populateModel(Model model, Integer canteenId, OrderStatus orderStatus,
                               String keyword, LocalDate startDate, LocalDate endDate) {
        List<User> staffList = userService.getStaffByCanteenToShip(canteenId);
        Canteen canteen = canteenService.getCanteenById(canteenId);

        model.addAttribute("staffList", staffList);
        model.addAttribute("canteenId", canteenId);
        model.addAttribute("orderStatus", orderStatus);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("canteenName", canteen.getCanteenName());
        model.addAttribute("keyword", keyword);
    }
}
