package com.ffood.g1.controller;

import com.ffood.g1.entity.Cart;
import com.ffood.g1.entity.CartItem;
import com.ffood.g1.entity.Food;
import com.ffood.g1.entity.User;
import com.ffood.g1.repository.UserRepository;
import com.ffood.g1.service.CartItemService;
import com.ffood.g1.service.CartService;
import com.ffood.g1.service.FoodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

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
    @Autowired
    FoodService foodService;


@PostMapping("/add_to_cart")
public String addToCart(@RequestParam("foodId") Integer foodId,
                        @RequestParam("quantity") int quantity,
                        @RequestParam("price") Double price,
                        RedirectAttributes redirectAttributes,
                        Model model) {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String email = authentication.getName();
    User user = userService.findByEmail(email);

    Cart cart = cartService.getOrCreateCart(user);

    cartService.addToCart(cart, foodId, quantity, LocalDateTime.now(), price);
    // Chuyển hướng về trang chủ sau khi thêm vào giỏ hàng
    return "redirect:/food_details?id=" + foodId  ;
}

    @GetMapping("/cart_items")
    public String getCartItems(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (Objects.nonNull(authentication) && authentication.isAuthenticated()) {
            String email = authentication.getName();
            User user = userService.findByEmail(email);
            if (Objects.nonNull(user)) {
                model.addAttribute("user", user);

                int totalQuantity = cartService.getTotalQuantityByUser(user);
                model.addAttribute("totalQuantity", totalQuantity);
            }
        }


        String email = authentication.getName();
        User user = userService.findByEmail(email);
        Integer userId = user.getUserId();


        Integer cartId = cartService.findCartIdByUserId(user.getUserId());
        double totalOrderPrice = cartService.getTotalFoodPriceByCartId(cartId);
        model.addAttribute("totalOrderPrice", totalOrderPrice);


        // Lấy các sản phẩm trong giỏ hàng của người dùng
        List<CartItem> cartItems = cartItemService.getCartItemsByUserId(userId);

        // Đưa dữ liệu giỏ hàng vào model
        model.addAttribute("cartItems", cartItems);

        // Trả về trang HTML
        return "cart";
    }


    @PostMapping("/remove_cartItem")
    public String removeCartItem(@RequestParam("cartItemId") Integer cartItemId) {
        cartService.removeCartItem(cartItemId);
        return "redirect:/cart_items";
    }



}

