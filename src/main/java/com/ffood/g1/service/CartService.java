package com.ffood.g1.service;



import com.ffood.g1.entity.Cart;
import com.ffood.g1.entity.Food;
import com.ffood.g1.entity.User;

import java.time.LocalDateTime;

public interface CartService {

    Cart getOrCreateCart(User user);
    void addToCart(Cart cart, Integer foodId, int quantity, LocalDateTime transactionDate, Double price);
    Cart getCartByUserId(Integer userId); // Phương thức này phải được triển khai

    void removeCartItem(Integer cartItemId);

    int getTotalQuantityByUser(User user);
}
