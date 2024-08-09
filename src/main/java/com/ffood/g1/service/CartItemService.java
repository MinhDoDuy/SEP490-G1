package com.ffood.g1.service;

import com.ffood.g1.entity.Cart;
import com.ffood.g1.entity.CartItem;
import com.ffood.g1.entity.Food;

import java.util.List;
import java.util.Optional;

public interface CartItemService {
    List<CartItem> getCartItemsByUserId(Integer userId);

    void updateCartItemQuantity(Integer cartItemId, int quantity);

    List<CartItem> getCartItemsByIds(List<Integer> cartItemIds);

    CartItem getCartItemById(Integer cartItemId);

    void removeCartItemsByIds(List<Integer> cartItemIds);

    CartItem addOrUpdateCartItem(Cart cartProvisional, Optional<Food> food, Integer quantity);

    List<CartItem> getCartItemsByDeliveryRoleId(Integer deliveryRoleId);
}
