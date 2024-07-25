package com.ffood.g1.controller;

import com.ffood.g1.entity.Cart;
import com.ffood.g1.entity.CartItem;
import com.ffood.g1.entity.Food;
import com.ffood.g1.entity.User;
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
        List<CartItem> cartItems = cartItemService.getCartItemsByUserId(user.getUserId());

        model.addAttribute("user", user);
        Integer finalTotalQuantity = cartService.getTotalQuantityByUser(user);
        int totalQuantity = finalTotalQuantity != null ? finalTotalQuantity : 0;
        session.setAttribute("totalQuantity", totalQuantity);

        // Get the canteen of the food item to be added
        Optional<Food> newFoodOpt = foodService.getFoodById(foodId);
        if (!newFoodOpt.isPresent()) {

            session.setAttribute("messageAddFood", "The food item does not exist.");
            if (url == 1) {
                return "redirect:/homepage";
            } else if (url == 2) {
                return "redirect:/canteen_details";
            } else if (url == 3) {
                return "redirect:/food_details?id=" + foodId;
            }
        }
        Food newFood = newFoodOpt.get();
        Integer newFoodCanteenId = newFood.getCanteen().getCanteenId();

        // Check if the cart is empty
        if (!cartItems.isEmpty()) {
            // Check if the cart already contains items from a different canteen
            for (CartItem cartItem : cartItems) {
                Food existingFood = cartItem.getFood();
                if (!existingFood.getCanteen().getCanteenId().equals(newFoodCanteenId)) {
                    session.setAttribute("messageAddFood", "Bạn đã có sản phẩm của cửa hàng " + existingFood.getCanteen().getCanteenName() + "!! Hãy vui lòng chọn sản phẩm cùng cửa hàng!!!");
                    if (url == 1) {
                        return "redirect:/homepage";
                    } else if (url == 2) {
                        return "redirect:/canteen_details";
                    } else if (url == 3) {
                        return "redirect:/food_details?id=" + foodId;
                    }
                }
            }
        }

        cartService.addToCart(cart, foodId, quantity, LocalDateTime.now(), price);
        session.setAttribute("messageAddFood", "Sản phẩm đã được thêm vào giỏ hàng thành công!!!");

        if (url == 1) {
            System.out.println("Vẫn oke mà!!!");
            return "redirect:/homepage";
        } else if (url == 2) {
            return "redirect:/canteen_details";
        } else if (url == 3) {
            return "redirect:/food_details?id=" + foodId;
        }
        // Redirect to the food details page after adding to the cart
        return null;
    }

    @GetMapping("/cart_items")
    public String getCartItems(Model model, HttpSession session) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userService.findByEmail(email);
        Integer userId = user.getUserId();

        Integer cartId = cartService.findCartIdByUserId(user.getUserId());


        Integer totalOrderPricefinal = cartService.getTotalFoodPriceByCartId(cartId);
        int totalOrderPrice = (totalOrderPricefinal != null) ? totalOrderPricefinal : 0;

        model.addAttribute("totalOrderPrice", totalOrderPrice);

        // Lấy các sản phẩm trong giỏ hàng của người dùng
        List<CartItem> cartItems = cartItemService.getCartItemsByUserId(userId);

        // Đưa dữ liệu giỏ hàng vào model
        model.addAttribute("cartItems", cartItems);

        // Trả về trang HTML
        return "cart/cart";
    }


    @PostMapping("/remove_cartItem")
    public String removeCartItem(@RequestParam("cartItemId") Integer cartItemId) {
        cartService.removeCartItem(cartItemId);
        return "redirect:/cart_items";
    }


    @GetMapping("/update_cart_quantity")
    public ResponseEntity<String> updateCartQuantity(@RequestParam("cartItemId") Integer cartItemId,
                                                     @RequestParam("quantity") int quantity
                                                     ) {

       CartItem cart= cartItemRepository.getCartItemByCartItemId(cartItemId);
        int foodQuantity =cart.getFood().getFoodQuantity();
        try {
            if (quantity<foodQuantity) {
                cartItemService.updateCartItemQuantity(cartItemId, quantity);
                return ResponseEntity.ok("Quantity updated successfully");
            }else
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating quantity");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating quantity");
        }


    }
}

