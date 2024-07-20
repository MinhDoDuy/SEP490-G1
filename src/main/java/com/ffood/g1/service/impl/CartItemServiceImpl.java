package com.ffood.g1.service.impl;

import com.ffood.g1.entity.CartItem;
import com.ffood.g1.repository.CartItemRepository;
import com.ffood.g1.service.CartItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.List;
@Service
public class CartItemServiceImpl implements CartItemService {
    @Autowired
    private CartItemRepository cartItemRepository;

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

}
