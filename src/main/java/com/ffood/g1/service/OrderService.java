package com.ffood.g1.service;

import com.ffood.g1.entity.Cart;

public interface OrderService {


    void createOrder(Cart cart, String address, String paymentMethod);
}
