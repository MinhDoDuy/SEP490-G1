package com.ffood.g1.service;

import com.ffood.g1.entity.Cart;
import com.ffood.g1.entity.Order;
import com.ffood.g1.entity.User;
import com.ffood.g1.enum_pay.OrderStatus;
import com.ffood.g1.enum_pay.OrderType;
import com.ffood.g1.enum_pay.PaymentMethod;

import java.util.List;

public interface OrderService {


    Order createOrder(User user, String address, Integer totalOrderPrice, String note, Cart cart, OrderType orderType, PaymentMethod paymentMethod, OrderStatus orderStatus,String orderCode);

    List<Order> getOrdersByUserIdAndStatus(Integer userId, OrderStatus status);
}
