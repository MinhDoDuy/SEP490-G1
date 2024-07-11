package com.ffood.g1.controller.manageStaff;
import com.ffood.g1.entity.Order;
import com.ffood.g1.enum_pay.OrderStatus;
import com.ffood.g1.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
@Controller
public class OrderListController {
    @Autowired
    private OrderService orderService;

    @GetMapping("/order-list/{canteenId}")
    public String getOrdersByCanteen(@PathVariable Integer canteenId, Model model) {
        List<OrderStatus> readyAndRepairStatuses = Arrays.asList(OrderStatus.READY, OrderStatus.REPAIR);
        List<OrderStatus> completeStatus = Collections.singletonList(OrderStatus.COMPLETE);

        List<Order> readyAndRepairOrders = orderService.getOrdersByCanteen(canteenId, readyAndRepairStatuses);
        List<Order> completeOrders = orderService.getOrdersByCanteen(canteenId, completeStatus);

        model.addAttribute("readyAndRepairOrders", readyAndRepairOrders);
        model.addAttribute("completeOrders", completeOrders);

        return "staff-management/order-list";
    }
}
