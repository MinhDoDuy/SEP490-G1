package com.ffood.g1.service.impl;

import com.ffood.g1.entity.Cart;
import com.ffood.g1.entity.CartItem;
import com.ffood.g1.entity.Food;
import com.ffood.g1.repository.CartItemRepository;
import com.ffood.g1.repository.CartRepository;
import com.ffood.g1.service.CartItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CartItemServiceImpl implements CartItemService {
    @Autowired
    private CartItemRepository cartItemRepository;
    @Autowired
    private CartRepository cartRepository;
    public List<CartItem> getCartItemsByUserId(Integer userId) {
        return cartItemRepository.findCartItemsByUserId(userId);
    }

    @Transactional
    public void updateCartItemQuantity(Integer cartItemId, int quantity) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new EntityNotFoundException("Cart item not found"));
        cartItem.setQuantity(quantity);
        cartItem.setTotalFoodPrice(cartItem.getPrice() * quantity);
        cartItemRepository.save(cartItem);
    }

    @Override
    public List<CartItem> getCartItemsByIds(List<Integer> cartItemIds) {
        return cartItemRepository.findByCartItemIdIn(cartItemIds);
    }

    public CartItem getCartItemById(Integer cartItemId) {
        return cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new EntityNotFoundException("CartItem not found with id: " + cartItemId));
    }

    @Transactional
    public void removeCartItemsByIds(List<Integer> cartItemIds) {
        cartItemRepository.deleteByIds(cartItemIds);
    }



    public CartItem addOrUpdateCartItem(Cart cart, Optional<Food> foodOpt, Integer quantity) {
        CartItem cartItem;
        if (foodOpt.isPresent()) {
            Food food = foodOpt.get();
            Optional<CartItem> existingCartItemOpt = cartItemRepository.findByCartAndFood(cart, food);

            if (existingCartItemOpt.isPresent()) {
                cartItem = existingCartItemOpt.get();
                cartItem.setQuantity(cartItem.getQuantity() + quantity);
                cartItem.setTotalFoodPrice(cartItem.getPrice() * cartItem.getQuantity());
            } else {
                cartItem = new CartItem();
                cartItem.setCart(cart);
                cartItem.setFood(food);
                cartItem.setQuantity(quantity);
                cartItem.setPrice(food.getPrice());
                cartItem.setTotalFoodPrice(cartItem.getPrice() * quantity);
                cartItem.setTransactionDate(LocalDateTime.now());
            }

            cartItemRepository.save(cartItem);

            // Update cart's total amount
            int totalAmount = cart.getCartItems().stream()
                    .mapToInt(CartItem::getTotalFoodPrice)
                    .sum();
            cart.setTotalAmount(totalAmount);
            cartRepository.save(cart);
        } else {
            throw new IllegalArgumentException("Food not found");
        }
        return cartItem;
    }

}
