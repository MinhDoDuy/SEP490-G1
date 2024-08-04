package com.ffood.g1.controller;

import antlr.BaseAST;
import com.ffood.g1.entity.*;
import com.ffood.g1.repository.CartItemRepository;
import com.ffood.g1.repository.CartRepository;
import com.ffood.g1.repository.UserRepository;
import com.ffood.g1.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class CartController {

    @Autowired
    private CartService cartService;
    @Autowired
    UserRepository userRepository;

    @Autowired
    UserService userService;
    @Autowired
    CartItemService cartItemService;
    @Autowired
    FoodService foodService;
    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private CanteenService canteenService;
    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

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

        Optional<Food> food=foodService.getFoodById(foodId);
        int canteenId=food.get().getCanteen().getCanteenId();
        if (url == 1) {
            return "redirect:/homepage";
        } else if (url == 2) {
            return "redirect:/canteen_details";
        } else if (url == 3) {
            return "redirect:/food_details?id=" + foodId;
        } else if (url == 4) {
            return "redirect:/canteen_info?canteenId="+canteenId;
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


    @PostMapping("/update_cart_quantity")
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


    @GetMapping("/create-order-at-couter")
    public String showCreateOrderForm(Model model, @RequestParam("canteenId") Integer canteenId, HttpSession session) {
        // Delete existing provisional cart for user with ID 1
        Cart cartProvisionalPre = cartService.getCartByUserId(1);
        if (cartProvisionalPre != null) {
            cartItemRepository.deleteAll(cartProvisionalPre.getCartItems());
            cartRepository.delete(cartProvisionalPre);
        }
        // Create a new provisional cart for user with ID 1
//        User user = userService.getUserById(1);
        Cart cartProvisional = new Cart();
        cartProvisional.setUser(userService.getUserById(1));
        cartProvisional.setCreatedAt(LocalDateTime.now());
        cartProvisional.setTransactionDate(LocalDateTime.now());
        cartProvisional.setTotalAmount(0);
        cartProvisional.setStatus("provisional");
        cartProvisional = cartRepository.save(cartProvisional);

        model.addAttribute("canteens", canteenService.getCanteenById(canteenId));
        model.addAttribute("foods", foodService.findByCanteenId(canteenId));
        Canteen canteen = canteenService.getCanteenById(canteenId);
        model.addAttribute("canteenName", canteen.getCanteenName());
        session.setAttribute("cartProvisional", cartProvisional);
        return "cart/pos-screen";
    }

    @PostMapping("/cart/add-to-cart-provisional")
    public ResponseEntity<Void> addToCartProvisional(@RequestParam("foodId") Integer foodId, @RequestParam("quantity") Integer quantity, HttpSession session) {
        System.out.println("Cos thuc hien chuc nang add");
        Optional<Food> foodProvisional = foodService.getFoodById(foodId);
        Cart cartProvisional = cartService.getCartByUserId(1);
        cartService.addToCart(cartProvisional, foodId, quantity, LocalDateTime.now(), foodProvisional.get().getPrice());
        return ResponseEntity.ok().build();
    }


    @PostMapping("/cart/update-cart-item-provisional")
    public ResponseEntity<Void> updateCartItemProvisional(@RequestParam("cartItemId") Integer foodId, @RequestParam("quantity") Integer quantity) {
        Cart cartProvisional = cartService.getCartByUserId(1);
        for (CartItem cartItem : cartProvisional.getCartItems()) {
            if (Objects.equals(cartItem.getFood().getFoodId(), foodId)) {
                cartItem.setQuantity(quantity);
                cartItemRepository.save(cartItem);
                break; // Exit the loop once the item is found and updated
            }
        }
        return ResponseEntity.ok().build(); // Return a successful response
    }

    @PostMapping("/cart/remove-from-cart-provisional")
    public ResponseEntity<Void> removeFromCartProvisional(@RequestParam("cartItemId") Integer foodId) {
        Cart cartProvisional = cartService.getCartByUserId(1);
        for (CartItem cartItem : cartProvisional.getCartItems()) {
            if (Objects.equals(cartItem.getFood().getFoodId(), foodId)) {
                cartItemRepository.deleteById(cartItem.getCartItemId());
                break; // Exit the loop once the item is found and removed
            }
        }
        return ResponseEntity.ok().build(); // Return a successful response
    }


    @GetMapping("/cart/payment")
    public String showPaymentScreen(Model model, @RequestParam(value = "phone", required = false) String phone) {
        List<CartItem> cartItems = cartItemService.getCartItemsByUserId(1);
        model.addAttribute("cartItems", cartItems);
        int totalOrderPrice = 0;
        int canteenId=0;
        for (CartItem cartItem : cartItems) {
            totalOrderPrice += cartItem.getQuantity() * cartItem.getFood().getPrice();
            canteenId=cartItem.getFood().getCanteen().getCanteenId();
        }
        cartRepository.save(cartService.getCartByUserId(1));
        model.addAttribute("totalOrderPrice", totalOrderPrice);
        model.addAttribute("canteenId", canteenId);
        return "cart/payment";
    }

    
    
    @GetMapping("/cart/search-user")
    public String searchUserByPhone(@RequestParam("phone") String phone, HttpSession session, Model model) {
        List<User> listUsers = userRepository.findAll();
        System.out.println("phone " + phone);
        for (User userProvisional : listUsers) {
            String ph = userProvisional.getPhone();
            if (phone.equals(ph)) {
                System.out.println("Test: " + userProvisional);
                model.addAttribute("userProvisional", userProvisional);
                return "cart/payment :: user-info";
            }
        }
        return "cart/payment :: user-not-found";
    }

    @PostMapping("/cart/newUser")
    public String registerUser(@RequestParam("fullName") String fullName,
                               @RequestParam("email") String email,
                               @RequestParam("phone") String phone,
                               @RequestParam("password") String password,
                               Model model) {
        // Create a new user instance
        User user = new User();
        user.setFullName(fullName);
        user.setEmail(email);
        user.setPhone(phone);
        user.setPassword(passwordEncoder.encode(password));
        // Set the default role as CUSTOMER
        Role role = new Role();
        role.setRoleId(1); // Assuming role ID 1 is for "CUSTOMER"
        user.setRole(role);
        // Save the user to the database
        userRepository.save(user);
        model.addAttribute("userProvisional", user);
        return "cart/payment :: user-info";
    }




}

