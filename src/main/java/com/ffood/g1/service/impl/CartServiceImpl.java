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
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;

@Service
public class CartServiceImpl implements CartService {


    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    FoodRepository foodRepository;

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
                .totalAmount(BigDecimal.ZERO)
                .build();

        return cartRepository.save(cart);
    }



    @Override
    @Transactional
    public void addToCart(Cart cart, Integer foodId, int quantity, LocalDateTime transactionDate, Double price) {
        Food food = foodRepository.findById(foodId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy sản phẩm"));

        Optional<CartItem> optionalCartItem = cartItemRepository.findByCartAndFood(cart, food);

        CartItem cartItem = CartItem.builder()
                .cart(cart)
                .food(food)
                .quantity(quantity)
                .price(food.getPrice())
                .transactionDate(transactionDate)
                .build();

        cartItemRepository.save(cartItem);
//        if (optionalCartItem.isPresent()) {
//            CartItem cartItem = optionalCartItem.get();
//            cartItem.setQuantity(cartItem.getQuantity() + quantity);
//        } else {
//            CartItem cartItem = CartItem.builder()
//                    .cart(cart)
//                    .food(food)
//                    .quantity(quantity)
//                    .price(price)
//                    .transactionDate(transactionDate)
//                    .build();
//            if (cart.getCartItems() == null) {
//                cart.setCartItems(new HashSet<>()); // Khởi tạo danh sách nếu chưa có
//            }
//            cart.getCartItems().add(cartItem);
//        }

//        // Tính toán lại totalAmount của Cart
//        if (cart.getCartItems() != null) {
//            BigDecimal totalAmount = cart.getCartItems().stream()
//                    .map(item -> BigDecimal.valueOf(item.getPrice()).multiply(BigDecimal.valueOf(item.getQuantity())))
//                    .reduce(BigDecimal.ZERO, BigDecimal::add);
//            cart.setTotalAmount(totalAmount);
//        }
//        cart.setTotalAmount(totalAmount);
        // Lưu lại Cart sau khi cập nhật thông tin
        //cartRepository.save(cart);
    }




    @Override
    public Cart getCartByUserId(Integer userId) {
        return cartRepository.findByUserId(userId).orElseThrow(() -> new EntityNotFoundException("Không tìm thấy giỏ hàng cho người dùng id: " + userId));
    }




}
