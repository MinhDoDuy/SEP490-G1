package com.ffood.g1.service.impl;

import com.ffood.g1.entity.Cart;
import com.ffood.g1.entity.Order;
import com.ffood.g1.entity.OrderDetail;
import com.ffood.g1.repository.OrderRepository;
import com.ffood.g1.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {


    @Autowired
    private OrderRepository orderRepository;

    public void createOrder(Cart cart, String address, String paymentMethod) {
        Order order = new Order();
        order.setUser(cart.getUser());
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(1);
        order.setOrderAddress(address);
        order.setTotalOrderPrice(cart.getCartItems().stream().mapToDouble(item -> item.getPrice() * item.getQuantity()).sum());
        order.setNote(paymentMethod);
        order.setOrderDetails(cart.getCartItems().stream().map(cartItem -> {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(order);
            orderDetail.setFood(cartItem.getFood());
            orderDetail.setQuantity(cartItem.getQuantity());
            orderDetail.setPrice(cartItem.getPrice());
            return orderDetail;
        }).collect(Collectors.toSet()));
        orderRepository.save(order);
    }
}
