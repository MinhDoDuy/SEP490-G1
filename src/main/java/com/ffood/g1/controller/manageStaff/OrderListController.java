package com.ffood.g1.controller.manageStaff;
import com.ffood.g1.entity.Order;
import com.ffood.g1.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
@Controller
public class OrderListController {
    @Autowired
    private OrderService orderService;

    @GetMapping("/order-list/{canteenId}")
    public String getOrdersByCanteen(@PathVariable Integer canteenId, Model model) {
        List<Order> orders = orderService.getOrdersByCanteen(canteenId);
        model.addAttribute("orders", orders);
        return "staff-management/order-list";
    }
}
