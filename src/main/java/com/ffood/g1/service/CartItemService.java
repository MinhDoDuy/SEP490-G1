package com.ffood.g1.service;

import com.ffood.g1.entity.CartItem;

import java.util.List;

public interface CartItemService {
    List<CartItem> getCartItemsByUserId(Integer userId);
}
