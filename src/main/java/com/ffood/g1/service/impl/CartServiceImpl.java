package com.ffood.g1.service.impl;

import com.ffood.g1.entity.Cart;
import com.ffood.g1.entity.CartItem;
import com.ffood.g1.entity.Food;
import com.ffood.g1.entity.User;
import com.ffood.g1.repository.CartItemRepository;
import com.ffood.g1.repository.CartRepository;
import com.ffood.g1.repository.FoodRepository;
import com.ffood.g1.service.CartService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CartServiceImpl implements CartService {


    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private FoodRepository foodRepository;

    @Override
    @Transactional
    public Cart getOrCreateCart(User user) {
        Optional<Cart> optionalCart = cartRepository.findByUser(user);
        if (optionalCart.isPresent()) {
            return optionalCart.get();
        }

        Cart cart = Cart.builder()
                .user(user)
                .transactionDate(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .status("active")
                .totalAmount(0)
                .build();
        return cartRepository.save(cart);
    }

    @Override
    @Transactional
    public void addToCart(Cart cart, Integer foodId, int quantity, LocalDateTime transactionDate, Integer price) {
        Food food = foodRepository.findById(foodId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy sản phẩm"));

        // Lấy danh sách các sản phẩm trong giỏ hàng
        List<CartItem> cartItems = cartItemRepository.findByCart(cart);
        Optional<CartItem> optionalCartItem = cartItemRepository.findByCartAndFood(cart, food);

        if (optionalCartItem.isPresent()) {
            CartItem cartItem = optionalCartItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
            cartItem.setTotalFoodPrice(cartItem.getTotalFoodPrice() + (food.getPrice() * quantity));
//        cartItem.setTransactionDate(transactionDate);
            cartItemRepository.save(cartItem);
        } else {
            CartItem cartItem = CartItem.builder()
                    .cart(cart)
                    .food(food)
                    .quantity(quantity)
                    .price(food.getPrice())
                    .totalFoodPrice(food.getPrice() * quantity)
                    .transactionDate(transactionDate)
                    .build();
            cartItemRepository.save(cartItem);
        }
    }



    @Override
    public Cart getCartByUserId(Integer userId) {
        return cartRepository.findByUserId(userId).orElseThrow(() -> new EntityNotFoundException("Không tìm thấy giỏ hàng cho người dùng id: " + userId));
    }


    @Transactional
    @Override
    public void removeCartItem(Integer cartItemId) {
        cartItemRepository.deleteById(cartItemId);
    }
    @Transactional
    @Override
    public Integer getTotalQuantityByUser(User user) {
        return cartRepository.getTotalQuantityByUser(user);

    }

    @Transactional
    @Override
    public Integer findCartIdByUserId(Integer userId) {
        return cartRepository.findCartIdByUserId(userId);
    }
    @Transactional
    @Override
    public Integer getTotalFoodPriceByCartId(Integer cartId) {
        return cartRepository.getTotalFoodPriceByCartId(cartId);
    }

    @Transactional
    @Override
    public void clearCart(Cart cart) {
        cart.getCartItems().clear();
        cartRepository.save(cart);
    }


    public List<Food> findByCategoryId(Integer categoryId) {
        return foodRepository.findByCategoryId(categoryId);
    }


}