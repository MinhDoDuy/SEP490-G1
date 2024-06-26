package com.ffood.g1.service;

import com.ffood.g1.entity.Cart;
import com.ffood.g1.entity.Order;
import com.ffood.g1.entity.User;

public interface OrderService {


    Order createOrder(User user, String address, Double totalOrderPrice, String note, Cart cart);
}
