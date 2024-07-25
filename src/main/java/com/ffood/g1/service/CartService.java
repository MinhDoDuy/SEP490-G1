package com.ffood.g1.service;



import com.ffood.g1.entity.Cart;
import com.ffood.g1.entity.CartItem;
import com.ffood.g1.entity.User;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

public interface CartService {

    Cart getOrCreateCart(User user);
    void addToCart(Cart cart, Integer foodId, int quantity, LocalDateTime transactionDate, Integer price);
    Cart getCartByUserId(Integer userId); // Phương thức này phải được triển khai

    void removeCartItem(Integer cartItemId);

    Integer getTotalQuantityByUser(User user);

    @Transactional
    Integer findCartIdByUserId(Integer userId);

    Integer getTotalFoodPriceByCartId(Integer cartId);

    @Transactional
    void clearCart(Cart cart);


}
