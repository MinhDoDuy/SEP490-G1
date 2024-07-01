package com.ffood.g1.controller;


import com.ffood.g1.entity.Cart;
import com.ffood.g1.entity.Order;
import com.ffood.g1.repository.OrderRepository;
import com.ffood.g1.service.CartService;
import com.ffood.g1.service.OrderService;
import com.ffood.g1.service.impl.VNPayService;
import com.google.api.client.util.store.AbstractMemoryDataStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.rsocket.PayloadInterceptorOrder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


@Controller
public class VNPayController {
    @Autowired
    private VNPayService vnPayService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private CartService cartService;
    @Autowired
    private OrderRepository orderRepository;
    @GetMapping("")
    public String pay(){
        return "redirect:/vnpay-payment";
    }

    @PostMapping("/submitOrder")
    public String submidOrder(@RequestParam("totalOrderPrice") int totalOrderPrice,
                              @RequestParam("orderInfo") String orderInfo,
                              HttpServletRequest request){
        String urlFixed = String.valueOf(request.getRequestURL());
        String serverPath = urlFixed.replace(request.getRequestURI(), "");
        String vnpayUrl = vnPayService.createOrder(totalOrderPrice, orderInfo, serverPath);
        return "redirect:" + vnpayUrl;
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
        String totalOrderPrice = request.getParameter("vnp_Amount");
        model.addAttribute("orderId",  orderInfo);
        model.addAttribute("totalOrderPrice",  totalOrderPrice);
        model.addAttribute("paymentTime",  paymentTime);
        model.addAttribute("transactionId",  transactionId);

        if (paymentStatus == 1) {
            Order order = (Order) session.getAttribute("order");
            Cart cart = (Cart) session.getAttribute("cart");
            if (order == null) {
                return "error";
            }
            try {
                orderRepository.save(order);
                cartService.clearCart(cart);
                session.removeAttribute("orders");
                session.removeAttribute("cart");
                session.removeAttribute("totalItems");
                return "orderSuccess";
            } catch (Exception e) {
                return "error";
            }
        } else {
            return "orderFail";
        }
    }

}
