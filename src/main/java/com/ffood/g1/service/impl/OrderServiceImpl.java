package com.ffood.g1.service.impl;

import com.ffood.g1.entity.*;
import com.ffood.g1.enum_pay.OrderStatus;
import com.ffood.g1.repository.OrderRepository;
import com.ffood.g1.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderRepository orderRepository;

    public Order createOrder(User user, String address, Double totalOrderPrice, String note, Cart cart) {
        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING_PAYMENT);
        order.setOrderAddress(address);
        order.setTotalOrderPrice(totalOrderPrice);
        order.setNote(note);

        // Lưu Order trước để lấy ID
        order = orderRepository.save(order);

        // Thiết lập các OrderDetail và thêm vào Order đã lưu
        List<OrderDetail> orderDetails = new ArrayList<>();
        for (CartItem cartItem : cart.getCartItems()) {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(order);
            orderDetail.setFood(cartItem.getFood());
            orderDetail.setQuantity(cartItem.getQuantity());
            orderDetail.setPrice(cartItem.getPrice());

            // Debug: In ra các thông tin của CartItem và OrderDetail
            System.out.println("CartItem ID: " + cartItem.getCartItemId());
            System.out.println("OrderDetail Food ID: " + orderDetail.getFood().getFoodId());
            System.out.println("OrderDetail Quantity: " + orderDetail.getQuantity());
            System.out.println("OrderDetail Price: " + orderDetail.getPrice());

            // Thêm OrderDetail vào List
            orderDetails.add(orderDetail);
        }
        order.setOrderDetails(orderDetails);
        // Debug: In ra số lượng OrderDetails trước khi lưu
        System.out.println("Total OrderDetails: " + orderDetails.size());
        // Lưu lại Order cùng với OrderDetail
        order = orderRepository.save(order);
        // Debug: In ra ID của Order sau khi lưu
        System.out.println("Order ID: " + order.getOrderId());
        return order;
    }
}
