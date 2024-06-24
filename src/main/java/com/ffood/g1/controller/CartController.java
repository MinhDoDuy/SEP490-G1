package com.ffood.g1.controller;

import com.ffood.g1.entity.Cart;
import com.ffood.g1.entity.CartItem;
import com.ffood.g1.entity.User;
import com.ffood.g1.repository.UserRepository;
import com.ffood.g1.service.CartItemService;
import com.ffood.g1.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.time.LocalDateTime;
import java.util.List;

@Controller
public class CartController {

    @Autowired
    private CartService cartService;
    @Autowired
    UserRepository userRepository;

    @Autowired
    UserRepository userService;
    @Autowired
    CartItemService cartItemService;

    @PostMapping("/add_to_cart")
    public ResponseEntity<String> addToCart(@RequestParam("foodId") Integer foodId, @RequestParam("quantity") int quantity, @RequestParam("price") Double price, Model model) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userService.findByEmail(email);

        Cart cart = cartService.getOrCreateCart(user);

        cartService.addToCart(cart, foodId, quantity, LocalDateTime.now(), price);

        return ResponseEntity.ok("Sản phẩm đã được thêm vào giỏ hàng");
    }

    @GetMapping("/cart_items")
    public String getCartItems(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userService.findByEmail(email);
        Integer userId = user.getUserId();

        // Lấy các sản phẩm trong giỏ hàng của người dùng
        List<CartItem> cartItems = cartItemService.getCartItemsByUserId(userId);

        // Đưa dữ liệu giỏ hàng vào model
        model.addAttribute("cartItems", cartItems);

        // Trả về trang HTML
        return "cart_items";
    }

}

