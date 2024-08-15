package com.ffood.g1.controller;


import com.ffood.g1.entity.Cart;
import com.ffood.g1.entity.Order;
import com.ffood.g1.entity.User;
import com.ffood.g1.enum_pay.OrderType;
import com.ffood.g1.enum_pay.PaymentMethod;
import com.ffood.g1.repository.CartRepository;
import com.ffood.g1.repository.OrderRepository;
import com.ffood.g1.service.CartItemService;
import com.ffood.g1.service.CartService;
import com.ffood.g1.service.OrderService;
import com.ffood.g1.service.impl.VNPayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;


@Controller
public class VNPayController {
    @Autowired
    private VNPayService vnPayService;

    @Autowired
    private OrderService orderService;
    @Autowired
    private CartItemService cartItemService;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private CartRepository cartRepository;

    @GetMapping("/vn")
    public String pay(){
        return "redirect:/vnpay-payment";
    }

    @PostMapping("/submitOrder")
    public String submidOrder(@RequestParam("totalOrderPrice") int totalOrderPrice,
                              @RequestParam("orderInfo") String orderInfo,
                              HttpServletRequest request , HttpServletResponse httpServletResponse){
        String urlFixed = String.valueOf(request.getRequestURL());
        String serverPath = urlFixed.replace(request.getRequestURI(), "");
        String vnpayUrl = vnPayService.createOrder(totalOrderPrice, orderInfo, serverPath);
        httpServletResponse.setHeader("Location", vnpayUrl);
        httpServletResponse.setStatus(302);
        return null;
    }

    @GetMapping("/now")
    public @ResponseBody String now(){
        return "Thời gian hiện tại của hệ thống là: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss"));
    }

    @GetMapping("/vnpay-payment")
    public String GetMapping(HttpServletRequest request, Model model, HttpSession session){
        int paymentStatus =vnPayService.orderReturn(request);

        String orderInfo = request.getParameter("vnp_OrderInfo");
        String paymentTime = request.getParameter("vnp_PayDate");
        String transactionId = request.getParameter("vnp_TransactionNo");
//        String totalOrderPrice = request.getParameter("vnp_Amount");
        model.addAttribute("orderId",  orderInfo);

        model.addAttribute("paymentTime",  paymentTime);
        model.addAttribute("transactionId",  transactionId);

        User user= (User) session.getAttribute("userOrderOn");
        String address=(String) session.getAttribute("addressOrderOn");
        String note=(String) session.getAttribute("noteOrderOn");
        OrderType orderType= (OrderType) session.getAttribute("orderTypeOrderOn");
        PaymentMethod paymentMethod=(PaymentMethod) session.getAttribute("paymentMethodOrderOn");
        List<Integer> cartItemIds  = (List<Integer>) session.getAttribute("cartItemIdsOrderOn");
        if (paymentStatus == 1) {
            Order order = orderService.createOrder(user, address, note, orderType, paymentMethod, cartItemIds,null,null);
            if (order == null) {
                return "error";
            }
            try {
                // Remove the selected cart items
                cartItemService.removeCartItemsByIds(cartItemIds);
                String orderCode= order.getOrderCode();
                int totalOrderPrice= order.getTotalOrderPrice();
                model.addAttribute("orderCode", orderCode);
                model.addAttribute("totalOrderPrice",  totalOrderPrice);
                return "order/order-success";
            } catch (Exception e) {
                return "error";
            }
        } else {
            return "order/order-fail";
        }
    }

}
