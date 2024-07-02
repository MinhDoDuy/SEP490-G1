package com.ffood.g1.controller;

import com.ffood.g1.entity.OrderDetail;
import com.ffood.g1.service.OrderDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class OrderDetailController {
    @Autowired
    private OrderDetailService orderDetailService;

    @GetMapping("/order-details/{orderId}")
    public List<OrderDetail> getOrderDetails(@PathVariable Integer orderId) {
        return orderDetailService.getOrderDetailsByOrderId(orderId);
    }




}
