package com.ffood.g1.controller.manageStaff;

import com.ffood.g1.entity.Canteen;
import com.ffood.g1.entity.Food;
import com.ffood.g1.entity.Order;
import com.ffood.g1.entity.User;
import com.ffood.g1.enum_pay.OrderStatus;
import com.ffood.g1.enum_pay.OrderType;
import com.ffood.g1.repository.FoodRepository;
import com.ffood.g1.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
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
                               @RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Optional<LocalDate> startDate,
                               @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Optional<LocalDate> endDate,
                               Model model, Principal principal, RedirectAttributes redirectAttributes) {

        // Lấy thông tin người dùng hiện tại từ Principal
        User currentUser = userService.findByEmail(principal.getName());

        // Kiểm tra xem `canteenId` có khớp với `canteenId` của người dùng hiện tại
        if (!currentUser.getCanteen().getCanteenId().equals(canteenId)) {
            redirectAttributes.addFlashAttribute("error", "Bạn không có quyền truy cập đơn hàng của canteen khác.");
            return "redirect:/order-list/" + currentUser.getCanteen().getCanteenId(); // Chuyển hướng về danh sách đơn hàng của canteen mà người dùng thuộc về
        }

        // Ensure orderStatus is not null
        if (orderStatus == null) {
            orderStatus = OrderStatus.PENDING; // Set a default value or handle appropriately
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<Order> orders;

        List<User> staffList = userService.getStaffByCanteenToShip(canteenId);
        Canteen canteen = canteenService.getCanteenById(canteenId);

        LocalDateTime start = startDate.orElse(LocalDate.MIN).atStartOfDay();
        LocalDateTime end = endDate.orElse(LocalDate.MAX).atTime(LocalTime.MAX);

        // Handling COMPLETE status with date filtering
        if (orderStatus == OrderStatus.COMPLETE && (startDate.isPresent() || endDate.isPresent())) {
            if (start.isAfter(end)) {
                model.addAttribute("error", "Ngày bắt đầu không thể sau ngày kết thúc");
                orders = Page.empty();
            } else {
                orders = orderService.getCompletedOrdersByCanteenAndDateRange(canteenId, start, end, pageable);
            }
        }
        // Handling REJECT status with keyword search
        else if (orderStatus == OrderStatus.REJECT && keyword != null && !keyword.isEmpty()) {
            orders = orderService.searchRejectedOrdersByOrderCode(canteenId, keyword, pageable);
        }
        // Handling REJECT status without search
        else if (orderStatus == OrderStatus.REJECT) {
            orders = orderService.getOrdersByCanteen(canteenId, List.of(OrderStatus.REJECT), pageable);
        }
        // Handling REFUND status with keyword search
        else if (orderStatus == OrderStatus.REFUND && keyword != null && !keyword.isEmpty()) {
            orders = orderService.searchRefundedOrdersByOrderCode(canteenId, keyword, pageable);
        }
        // Handling REFUND status without search
        else if (orderStatus == OrderStatus.REFUND) {
            orders = orderService.getOrdersByCanteen(canteenId, List.of(OrderStatus.REFUND), pageable);
        }
        // Handling other statuses (PENDING, PROGRESS)
        else {
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
                default:
                    orders = Page.empty();
            }
        }

        model.addAttribute("orders", orders);
        model.addAttribute("staffList", staffList);
        model.addAttribute("canteenId", canteenId);
        model.addAttribute("orderStatus", orderStatus);
        model.addAttribute("startDate", startDate.orElse(null));
        model.addAttribute("endDate", endDate.orElse(null));
        model.addAttribute("canteenName", canteen.getCanteenName());
        model.addAttribute("keyword", keyword);

        return "staff-management/order-list";
    }




    @GetMapping("/order-list-reject/{canteenId}")
    public String manageRejectedOrders(@PathVariable Integer canteenId,
                                       @RequestParam(value = "keyword", required = false) String keyword,
                                       @RequestParam(value = "page", defaultValue = "0") int page,
                                       @RequestParam(value = "size", defaultValue = "10") int size,
                                       Model model) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Order> orders;

        if (keyword != null && !keyword.isEmpty()) {
            orders = orderService.searchRejectedOrdersByOrderCode(canteenId, keyword, pageable);
        } else {
            orders = orderService.getOrdersByCanteen(canteenId, List.of(OrderStatus.REJECT), pageable);
        }

        model.addAttribute("orders", orders);
        model.addAttribute("canteenId", canteenId);
        model.addAttribute("keyword", keyword);
        return "staff-management/order-list";
    }

    @GetMapping("/order-list-refund/{canteenId}")
    public String manageRefundedOrders(@PathVariable Integer canteenId,
                                       @RequestParam(value = "keyword", required = false) String keyword,
                                       @RequestParam(value = "page", defaultValue = "0") int page,
                                       @RequestParam(value = "size", defaultValue = "10") int size,
                                       Model model) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Order> orders;

        if (keyword != null && !keyword.isEmpty()) {
            orders = orderService.searchRefundedOrdersByOrderCode(canteenId, keyword, pageable);
        } else {
            orders = orderService.getOrdersByCanteen(canteenId, List.of(OrderStatus.REFUND), pageable);
        }

        model.addAttribute("orders", orders);
        model.addAttribute("canteenId", canteenId);
        model.addAttribute("keyword", keyword);
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
            redirectAttributes.addFlashAttribute("message", "Chuyển Đơn hàng cho Shipper thành công");
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
            redirectAttributes.addFlashAttribute("message", "Chuyển Đơn hàng cho Shipper thành công cho các đơn hàng đã chọn");
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

    @GetMapping("/order-list-ship/{canteenId}")
    public String getOrdersForShipper(@PathVariable Integer canteenId,
                                      @RequestParam(value = "deliveryRoleId", required = false) Integer deliveryRoleId,
                                      @RequestParam(value = "page", defaultValue = "0") int page,
                                      @RequestParam(value = "size", defaultValue = "10") int size,
                                      Model model, Principal principal, RedirectAttributes redirectAttributes) {

        if (deliveryRoleId == null) {
            redirectAttributes.addFlashAttribute("error", "Delivery Role ID is required.");
            return "redirect:/404"; // Thay bằng trang lỗi phù hợp
        }

        // Lấy thông tin người dùng hiện tại từ Principal
        User currentUser = userService.findByEmail(principal.getName());

        // Kiểm tra xem `canteenId` có khớp với `canteenId` của `currentUser`
        if (!currentUser.getCanteen().getCanteenId().equals(canteenId)) {
            redirectAttributes.addFlashAttribute("error", "Bạn không có quyền truy cập đơn hàng của canteen khác.");
            return "redirect:/order-list-ship/" + currentUser.getCanteen().getCanteenId() + "?deliveryRoleId=" + currentUser.getUserId();
        }

        // Kiểm tra xem `deliveryRoleId` có thuộc về `canteenId` hiện tại và có phải là chính `currentUser`
        User deliveryUser = userService.loadUserById(deliveryRoleId);
        if (deliveryUser == null || !deliveryUser.getCanteen().getCanteenId().equals(canteenId) || deliveryUser.getRole().getRoleId() != 2 || !deliveryUser.getUserId().equals(currentUser.getUserId())) {
            redirectAttributes.addFlashAttribute("error", "Bạn không có quyền truy cập đơn hàng của người khác trong cùng canteen.");
            return "redirect:/order-list-ship/" + canteenId + "?deliveryRoleId=" + currentUser.getUserId();
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<Order> orders = orderService.getOrdersByCanteenAndDeliveryRole(canteenId, deliveryRoleId, pageable);

        model.addAttribute("orders", orders);
        model.addAttribute("canteenId", canteenId);
        model.addAttribute("deliveryRoleId", deliveryRoleId);
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


}
