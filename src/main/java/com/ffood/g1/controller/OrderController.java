package com.ffood.g1.controller;

import com.ffood.g1.entity.Cart;
import com.ffood.g1.entity.CartItem;
import com.ffood.g1.entity.User;
import com.ffood.g1.repository.UserRepository;
import com.ffood.g1.service.CartItemService;
import com.ffood.g1.service.CartService;
import com.ffood.g1.service.FoodService;
import com.ffood.g1.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class OrderController {
    @Autowired
    UserRepository userRepository;
    @Autowired
    UserRepository userService;
    @Autowired
    CartItemService cartItemService;
    @Autowired
    FoodService foodService;
    @Autowired
    OrderService orderService;
    @Autowired
    private CartService cartService;

    @GetMapping("/checkout")
    public String showCheckoutPage(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof org.springframework.security.authentication.AnonymousAuthenticationToken)) {
            String email = authentication.getName();
            User user = userService.findByEmail(email);
            if (user != null) {
                Integer cartId = cartService.findCartIdByUserId(user.getUserId());
                double totalOrderPrice = cartService.getTotalFoodPriceByCartId(cartId);
                model.addAttribute("totalOrderPrice", totalOrderPrice);
                model.addAttribute("user", user);
            }
            int totalQuantity = cartService.getTotalQuantityByUser(user);
            model.addAttribute("totalQuantity", totalQuantity);
            // Lấy các sản phẩm trong giỏ hàng của người dùng
            List<CartItem> cartItems = cartItemService.getCartItemsByUserId(user.getUserId());
            // Đưa dữ liệu giỏ hàng vào model
            model.addAttribute("cartItems", cartItems);

        }


        return "test";
    }


    @PostMapping("/checkout")
    public String processCheckout(@RequestParam("address") String address,
                                  @RequestParam("paymentMethod") String paymentMethod,
                                  Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof org.springframework.security.authentication.AnonymousAuthenticationToken)) {
            String email = authentication.getName();
            User user = userService.findByEmail(email);
            if (user != null) {
                // Lấy giỏ hàng hiện tại
                Cart cart = cartService.getOrCreateCart(user);

                // Tạo và lưu đơn hàng
                orderService.createOrder(cart, address, paymentMethod);



                // Xóa giỏ hàng
                cartService.clearCart(cart);
            }
        }
        return "redirect:/orderConfirmation";
    }
}
