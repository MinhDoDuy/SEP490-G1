package com.ffood.g1.controller;

import com.ffood.g1.entity.*;
import com.ffood.g1.repository.CartItemRepository;
import com.ffood.g1.repository.UserRepository;
import com.ffood.g1.service.CartItemService;
import com.ffood.g1.service.CartService;
import com.ffood.g1.service.FoodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
    @Autowired
    private CartItemRepository cartItemRepository;


    @GetMapping("/add_to_cart")
    public String addToCart(@RequestParam("foodId") Integer foodId,
                            @RequestParam("quantity") int quantity,
                            @RequestParam("price") Integer price,
                            @RequestParam("url") Integer url,
                            HttpSession session,
                            RedirectAttributes redirectAttributes,
                            Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // Check if the user is authenticated
        if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
            redirectAttributes.addFlashAttribute("message", "You need to log in to add items to the cart.");
            return "redirect:/login";
        }
        String email = authentication.getName();
        User user = userService.findByEmail(email);
        Cart cart = cartService.getOrCreateCart(user);
        model.addAttribute("user", user);

        Integer finalTotalQuantity = cartService.getTotalQuantityByUser(user);
        int totalQuantity = finalTotalQuantity != null ? finalTotalQuantity : 0;
        session.setAttribute("totalQuantity", totalQuantity);

        cartService.addToCart(cart, foodId, quantity, LocalDateTime.now(), price);

        session.setAttribute("messageAddFood", "Sản phẩm đã được thêm vào giỏ hàng thành công!!!");
        ;

        if (url == 1) {
            return "redirect:/homepage";
        } else if (url == 2) {
            return "redirect:/canteen_details";
        } else if (url == 3) {
            return "redirect:/food_details?id=" + foodId;
        }
        return null;
    }

    @GetMapping("/cart_items")
    public String getCartItems(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userService.findByEmail(email);
        Integer userId = user.getUserId();
        List<CartItem> cartItems = cartItemService.getCartItemsByUserId(userId);

        // Grouping cart items by canteen
        Map<Canteen, List<CartItem>> groupedItems = cartItems.stream()
                .collect(Collectors.groupingBy(item -> item.getFood().getCanteen()));

        model.addAttribute("cartItemsGroupedByCanteen", groupedItems);

        Integer totalOrderPrice = cartItems.stream()
                .mapToInt(item -> item.getQuantity() * item.getFood().getPrice())
                .sum();

        model.addAttribute("totalOrderPrice", totalOrderPrice);
        return "cart/cart";
    }

    @PostMapping("/remove_cartItem")
    public String removeCartItem(@RequestParam("cartItemId") Integer cartItemId) {
        cartService.removeCartItem(cartItemId);
        return "redirect:/cart_items";
    }


    @GetMapping("/update_cart_quantity")
    public ResponseEntity<String> updateCartQuantity(@RequestParam("cartItemId") Integer cartItemId,
                                                     @RequestParam("quantity") int quantity,
                                                     Model model,
                                                     HttpSession session
    ) {

        CartItem cart = cartItemRepository.getCartItemByCartItemId(cartItemId);
        int foodQuantity = cart.getFood().getFoodQuantity();
        try {
            if (quantity <= foodQuantity) {
                cartItemService.updateCartItemQuantity(cartItemId, quantity);
                return ResponseEntity.ok("Quantity updated successfully");
            } else
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error updating quantity");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error updating quantity");
        }


    }




    @PostMapping("/payment")
    public String showPaymentScreen(@RequestParam("cartData") String cartData, Model model) {
        model.addAttribute("cartData", cartData);
        System.out.println("Cứ the cart data is: " + cartData);
        return "cart/payment"; // The name of the payment screen HTML file
    }

}

