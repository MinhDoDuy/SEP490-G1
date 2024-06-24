package com.ffood.g1.service.impl;

import com.ffood.g1.entity.CartItem;
import com.ffood.g1.repository.CartItemRepository;
import com.ffood.g1.service.CartItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class CartItemServiceImpl implements CartItemService {
    @Autowired
    private CartItemRepository cartItemRepository;

    public List<CartItem> getCartItemsByUserId(Integer userId) {
        return cartItemRepository.findCartItemsByUserId(userId);
    }

}
