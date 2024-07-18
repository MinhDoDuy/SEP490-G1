package com.ffood.g1.service.impl;

import com.ffood.g1.entity.*;
import com.ffood.g1.enum_pay.OrderStatus;
import com.ffood.g1.enum_pay.PaymentStatus;
import com.ffood.g1.enum_pay.OrderType;
import com.ffood.g1.enum_pay.PaymentMethod;
import com.ffood.g1.repository.OrderRepository;
import com.ffood.g1.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.lang.reflect.Array;
import java.time.LocalDateTime;
import java.util.*;

@Service
    public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private JavaMailSender mailSender;

    public List<Object[]> getBestSellingItems() {
        return orderRepository.findBestSellingItems();
    }

    public List<Object[]> getOrderStats() {
        return orderRepository.findOrderStats();
    }


    @Override
    public List<Object[]> getRevenueDataByMonth() {
        return orderRepository.findRevenueDataByMonth();
    }

    @Override
    public List<Object[]> getRevenueDataByYear() {
        return orderRepository.findRevenueDataByYear();
    }

    @Override
    public Double calculateTotalOrder() {
        return orderRepository.findTotalOrder();
    }


    public Order createOrder(User user, String address, Integer totalOrderPrice, String note, Cart cart, OrderType orderType, PaymentMethod paymentMethod, PaymentStatus paymentStatus, String orderCode) {
        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setPaymentStatus(paymentStatus);
        order.setOrderAddress(address);
        order.setTotalOrderPrice(totalOrderPrice);
        order.setNote(note);
        order.setOrderType(orderType);
        order.setPaymentMethod(paymentMethod);
        order.setOrderCode(orderCode);
        order.setOrderStatus(OrderStatus.PENDING);


//        // Lưu Order trước để lấy ID
//        order = orderRepository.save(order);

        // Thiết lập các OrderDetail và thêm vào Order đã lưu
        List<OrderDetail> orderDetails = new ArrayList<>();
        for (CartItem cartItem : cart.getCartItems()) {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(order);
            orderDetail.setFood(cartItem.getFood());
            orderDetail.setQuantity(cartItem.getQuantity());
            orderDetail.setPrice(cartItem.getPrice());

            // Debug: In ra các thông tin của CartItem và OrderDetail
            System.out.println("CartItem ID: " + cartItem.getCartItemId());
            System.out.println("OrderDetail Food ID: " + orderDetail.getFood().getFoodId());
            System.out.println("OrderDetail Quantity: " + orderDetail.getQuantity());
            System.out.println("OrderDetail Price: " + orderDetail.getPrice());

            // Thêm OrderDetail vào List
            orderDetails.add(orderDetail);
        }
        order.setOrderDetails(orderDetails);
        // Debug: In ra số lượng OrderDetails trước khi lưu
        System.out.println("Total OrderDetails: " + orderDetails.size());
        // Lưu lại Order cùng với OrderDetail

        // Debug: In ra ID của Order sau khi lưu
        System.out.println("Order ID: " + order.getOrderId());
        return order;
    }




    public List<Order> getOrdersByUserIdAndStatus(Integer userId, PaymentStatus paymentStatus) {
        return orderRepository.findByUserUserIdAndPaymentStatus(userId, paymentStatus);
    }

    @Override
    public List<Order> getOrdersByCanteen(Integer canteenId, List<OrderStatus> statuses) {
        return orderRepository.findOrdersByCanteenIdAndStatuses(canteenId, statuses);
    }

    @Override
    public List<Order> getOrdersByCanteenAndType(Integer canteenId, List<OrderStatus> statuses, OrderType orderType) {
        return orderRepository.findOrdersByCanteenIdAndStatusesAndOrderTypePendingOnline(canteenId, statuses, orderType);
    }



    @Override
    public List<Object[]> findRevenueDataCanteenByMonth(Integer canteenId) {
        return orderRepository.findRevenueCanteenDataByMonth(canteenId);
    }

    @Override
    public List<Object[]> findRevenueDataCanteenByYear(Integer canteenId) {
        return orderRepository.findRevenueDataCanteenByYear(canteenId);
    }

    @Override
    public Integer countCompletedOrdersByCanteenId(Integer canteenId) {
        return orderRepository.countCompletedOrdersByCanteenId(canteenId);
    }


    private void sendEmail(String email, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }

    private String getOrderDetails(Order order) {
        StringBuilder details = new StringBuilder();
        details.append("Chi tiết đơn hàng:\n");
        details.append("Căn tin: ").append(order.getOrderDetails().get(0).getFood().getCanteen().getCanteenName()).append("\n");
        for (OrderDetail detail : order.getOrderDetails()) {
            details.append("Món ăn: ").append(detail.getFood().getFoodName())
                    .append(", Số lượng: ").append(detail.getQuantity())
                    .append(", Giá: ").append(detail.getPrice())
                    .append("\n");
        }
        return details.toString();
    }

    @Override
    public void updateOrderStatus(Integer orderId, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid order ID"));
        order.setOrderStatus(newStatus);
        orderRepository.save(order);

        // Lấy thông tin chi tiết đơn hàng
        String orderDetails = getOrderDetails(order);

        // Gửi email thông cho người dùng
        if (newStatus == OrderStatus.PROGRESS) {
            String subject = "Đơn hàng của bạn đã được chuẩn bị";
            String text = "Đơn hàng của bạn với mã số " + orderId + " đã được chuẩn bị và sẵn sàng để lấy. Mời bạn xuống lấy hàng.\n\n" + orderDetails;
            sendEmail(order.getUser().getEmail(), subject, text);
        } else if (newStatus == OrderStatus.COMPLETE) {
            String subject = "Đơn hàng của bạn đã hoàn thành";
            String text = "Đơn hàng của bạn với mã số " + orderId + " đã được giao thành công.\n\n" + orderDetails;
            sendEmail(order.getUser().getEmail(), subject, text);
        }
    }

    @Override
    public void assignShipperAndUpdateStatus(Integer orderId, Integer deliveryRoleId, OrderStatus newStatus, String staffName) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new IllegalStateException("Order not found"));
        order.setDeliveryRoleId(deliveryRoleId);
        order.setOrderStatus(newStatus);
        order.setDeliveryRoleName(staffName);
        orderRepository.save(order);
    }

    @Override
    public void rejectOrder(Integer orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid order ID"));
        order.setOrderStatus(OrderStatus.REJECT);
        orderRepository.save(order);

        // Lấy thông tin chi tiết đơn hàng
        String orderDetails = getOrderDetails(order);

        // Gửi email thông báo khi đơn hàng bị hủy
        String subject = "Đơn hàng của bạn đã bị hủy";
        String text = "Đơn hàng của bạn với mã số " + orderId + " đã bị hủy.\n\n" + orderDetails;
        sendEmail(order.getUser().getEmail(), subject, text);
    }

    @Override
    public List<Object[]> getOrderStatsByCanteenAndMonth(Integer canteenId) {
        return orderRepository.findOrderStatsByCanteenAndMonth(canteenId);
    }

    @Override
    public List<Object[]> getBestSellingItemsByCanteen(Integer canteenId) {
        return orderRepository.findBestSellingItemsByCanteen(canteenId);
    }

}
