package com.ffood.g1.service;

import com.ffood.g1.entity.CartItem;

import java.util.List;

public interface CartItemService {
    List<CartItem> getCartItemsByUserId(Integer userId);

    void updateCartItemQuantity(Integer cartItemId, int quantity);

    List<CartItem> getCartItemsByIds(List<Integer> cartItemIds);

    CartItem getCartItemById(Integer cartItemId);

    void removeCartItemsByIds(List<Integer> cartItemIds);
}
