package com.ffood.g1.controller;

import com.ffood.g1.entity.*;
import com.ffood.g1.enum_pay.PaymentStatus;
import com.ffood.g1.enum_pay.OrderType;
import com.ffood.g1.enum_pay.PaymentMethod;
import com.ffood.g1.repository.FoodRepository;
import com.ffood.g1.repository.OrderRepository;
import com.ffood.g1.repository.UserRepository;
import com.ffood.g1.service.*;
import com.ffood.g1.service.impl.VNPayService;
import com.ffood.g1.utils.RandomOrderCodeGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
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
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private FoodRepository foodRepository;;

    @Autowired
    private VNPayService vnpayService;

    @GetMapping("/checkout")
    public String showCheckoutPage(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof org.springframework.security.authentication.AnonymousAuthenticationToken)) {
            String email = authentication.getName();
            User user = userService.findByEmail(email);
            if (user != null) {
                Integer cartId = cartService.findCartIdByUserId(user.getUserId());
                Integer totalOrderPrice = (int) cartService.getTotalFoodPriceByCartId(cartId);
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


        return "order/check-out";
    }


    @PostMapping("/checkout")
    public String processCheckout(@RequestParam("address") String address,
                                  @RequestParam("note") String note,
                                  @RequestParam("paymentMethod") PaymentMethod paymentMethod,
                                  @RequestParam("orderType") OrderType orderType,
                                  HttpSession session,
                                  Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof org.springframework.security.authentication.AnonymousAuthenticationToken)) {
            String email = authentication.getName();
            User user = userService.findByEmail(email);
            if (user != null) {
                // Lấy giỏ hàng hiện tại
                Cart cart = cartService.getOrCreateCart(user);
                session.setAttribute("cart", cart);
                // Tạo và lưu đơn hàng
                Integer cartId = cartService.findCartIdByUserId(user.getUserId());
                int totalOrderPrice = cartService.getTotalFoodPriceByCartId(cartId);
                if (paymentMethod.equals(PaymentMethod.CASH)) {
                    PaymentStatus paymentStatus = PaymentStatus.PENDING_PAYMENT;
                    String orderCode=RandomOrderCodeGenerator.generateOrderCode();
                    Order order = orderService.createOrder(user, address, totalOrderPrice, note, cart, orderType, paymentMethod, paymentStatus,orderCode);
                    cart.setStatus("deactive");
                    order = orderRepository.save(order);
                    model.addAttribute("order", order);
                    // Xóa giỏ hàng
                    cartService.clearCart(cart);

                }else if(paymentMethod.equals(PaymentMethod.VNPAY)) {
                    PaymentStatus paymentStatus = PaymentStatus.PAYMENT_COMPLETE;
                    String orderCode=RandomOrderCodeGenerator.generateOrderCode();
                    Order order = orderService.createOrder(user, address, totalOrderPrice, note, cart, orderType, paymentMethod, paymentStatus,orderCode);


                    for (OrderDetail orderItem : order.getOrderDetails()) {
                        Food food = orderItem.getFood();
                        int orderedQuantity = orderItem.getQuantity();
                        System.out.println(food.getFoodQuantity());
                        // Giảm số lượng tồn kho
                        food.setFoodQuantity(food.getFoodQuantity() - orderedQuantity);
                        System.out.println(food.getFoodQuantity());
                        // Tăng số lượng đã bán
                        food.setSalesCount(food.getSalesCount() + orderedQuantity);

                        foodRepository.save(food);
                    }
                    session.setAttribute("order", order);
                    model.addAttribute("order", order);
                    return "order/create-order";
                }
            }

        }
        return "redirect:/homepage";
    }

//    @GetMapping("/vnpay_return")
//    public String vnPayReturn(@RequestParam Map<String, String> params, Model model) {
//        // Xử lý phản hồi từ VNPay và cập nhật trạng thái đơn hàng
//        // ...
//        return "payment-result";
//    }


    @GetMapping("/order-history")
    public String viewOrderHistory(Model model) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof org.springframework.security.authentication.AnonymousAuthenticationToken)) {
            String email = authentication.getName();
            User user = userService.findByEmail(email);

            Integer userId = user.getUserId();
            PaymentStatus status= PaymentStatus.PAYMENT_COMPLETE;
//            OrderStatus orderStatus = OrderStatus.valueOf(status);
            List<Order> orders = orderService.getOrdersByUserIdAndStatus(userId, status);
            model.addAttribute("orders", orders);
            model.addAttribute("status", status);
        }
        return "order/order-history";
    }

}