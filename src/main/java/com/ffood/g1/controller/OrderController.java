package com.ffood.g1.controller;

import com.ffood.g1.entity.*;
import com.ffood.g1.enum_pay.OrderType;
import com.ffood.g1.enum_pay.PaymentMethod;
import com.ffood.g1.repository.FoodRepository;
import com.ffood.g1.repository.OrderRepository;
import com.ffood.g1.repository.UserRepository;
import com.ffood.g1.service.CartItemService;
import com.ffood.g1.service.CartService;
import com.ffood.g1.service.FoodService;
import com.ffood.g1.service.OrderService;
import com.ffood.g1.service.impl.VNPayService;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private FoodRepository foodRepository;
    ;

    @Autowired
    private VNPayService vnpayService;

    @GetMapping("/checkout")
    public String showCheckoutPage(@RequestParam("cartItemIds") List<Integer> cartItemIds, Model model, HttpSession session) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
            String email = authentication.getName();
            User user = userService.findByEmail(email);
            model.addAttribute("user", user);
        }
        // Assume a method exists to fetch cart items by their IDs
        List<CartItem> cartItems = cartItemService.getCartItemsByIds(cartItemIds);
        // Check all items are from the same canteen
        Integer canteenId = null;
        int totalOrderPrice = 0;
        for (CartItem item : cartItems) {
            totalOrderPrice += item.getFood().getPrice() * item.getQuantity();
            if (canteenId == null) {
                canteenId = item.getFood().getCanteen().getCanteenId();
            } else if (!canteenId.equals(item.getFood().getCanteen().getCanteenId())) {
                model.addAttribute("error", "Cannot checkout items from multiple canteens.");
                return "cart/cart"; // Redirect back to the cart page with an error message
            }
        }
        model.addAttribute("totalOrderPrice", totalOrderPrice);
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("cartItemIds", cartItemIds);
        return "order/check-out"; // Render the checkout page
    }


    @PostMapping("/checkout")
    public String processCheckout(@RequestParam("address") String address,
                                  @RequestParam("note") String note,
                                  @RequestParam("paymentMethod") PaymentMethod paymentMethod,
                                  @RequestParam("orderType") OrderType orderType,
                                  @RequestParam("cartItemIdsString") String cartItemIdsString,
                                  Model model, HttpSession session) {
        //Hãy thêm hàm đổi từ string sang List<Integer>
        List<Integer> cartItemIds = Arrays.stream(cartItemIdsString.replaceAll("\\[|\\]", "").split(","))
                .map(String::trim)
                .map(Integer::parseInt)
                .collect(Collectors.toList());
        int totalOrderPrice = 0;
        for (Integer cartItemId : cartItemIds) {
            CartItem cartItem = cartItemService.getCartItemById(cartItemId);
            if (cartItem != null) {
                totalOrderPrice += cartItem.getPrice() * cartItem.getQuantity();
            }
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
            String email = authentication.getName();
            User user = userService.findByEmail(email);
            if (user != null) {

                //Luu session cac phan tu cửa order:
                session.setAttribute("userOrderOn", user);
                session.setAttribute("addressOrderOn", address);
                session.setAttribute("noteOrderOn", note);
                session.setAttribute("orderTypeOrderOn", orderType);
                session.setAttribute("paymentMethodOrderOn", paymentMethod);
                session.setAttribute("cartItemIdsOrderOn", cartItemIds);

                model.addAttribute("totalOrderPrice", totalOrderPrice);
                return "order/create-order";
            }
        }
        return "redirect:/homepage";
    }


    @PostMapping("/processCheckout")
    public String processCheckout(@RequestParam("cartItemIds") List<Integer> cartItemIds, RedirectAttributes redirectAttributes) {
        try {
            // This is a placeholder for your actual checkout processing logic
            redirectAttributes.addFlashAttribute("message", "Checkout completed successfully!");
            return "redirect:/orderConfirmation"; // Redirect to an order confirmation page
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error during checkout: " + e.getMessage());
            return "redirect:/cart"; // Redirect back to cart on error
        }
    }

    @GetMapping("/orderConfirmation")
    public String orderConfirmation() {
        return "checkout/orderConfirmation"; // Show order confirmation page
    }


    @GetMapping("/order-history")
    public String getOrderHistory(Model model, Principal principal) {
        String username = principal.getName();
        User user = userService.findByEmail(username);
        List<Order> orders = orderService.getOrdersByUserId(user.getUserId());
        model.addAttribute("orders", orders);
        List<Food> foods = foodService.getRandomFood();
        model.addAttribute("foods", foods);
        return "order/order-history";
    }

    @PostMapping("/cart/submit-payment")
    public ResponseEntity<String> submitPayment(
            @RequestParam("paymentMethod") String paymentMethod,
            @RequestParam("userId") Integer userId,
            @RequestParam("deliveryRoleId") Integer deliveryRoleId) {
        OrderType orderType = OrderType.AT_COUNTER;
        List<Integer> cartItemIds = new ArrayList<>();
        // Giả sử bạn có dịch vụ để tạo đơn hàng
        User user = userRepository.getOne(userId);
        List<CartItem> listCartItems = cartItemService.getCartItemsByDeliveryRoleId(deliveryRoleId);
        for (CartItem item : listCartItems) {
            cartItemIds.add(item.getCartItemId());
        }
        PaymentMethod paymentMethodDone = null;
        if (paymentMethod.equals("qr")) {
            paymentMethodDone = PaymentMethod.VNPAY;
        } else if (paymentMethod.equals("cash")) {
            paymentMethodDone = PaymentMethod.CASH;
        }

        Order order = orderService.createOrder(user, null, null, orderType, paymentMethodDone, cartItemIds);
        cartItemService.removeCartItemsByIds(cartItemIds);
        // Xử lý logic liên quan đến thanh toán, ví dụ lưu thông tin đơn hàng vào cơ sở dữ liệu

        return ResponseEntity.ok("Thanh toán thành công!");
    }
}