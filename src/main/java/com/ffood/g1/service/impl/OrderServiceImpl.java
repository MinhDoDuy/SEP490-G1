package com.ffood.g1.service.impl;

import com.ffood.g1.entity.*;
import com.ffood.g1.enum_pay.OrderStatus;
import com.ffood.g1.enum_pay.OrderType;
import com.ffood.g1.enum_pay.PaymentMethod;
import com.ffood.g1.enum_pay.PaymentStatus;
import com.ffood.g1.repository.FoodRepository;
import com.ffood.g1.repository.OrderDetailRepository;
import com.ffood.g1.repository.OrderRepository;
import com.ffood.g1.repository.UserRepository;
import com.ffood.g1.service.OrderService;
import com.ffood.g1.utils.RandomOrderCodeGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private FoodRepository foodRepository;
    ;
    @Autowired
    private OrderDetailRepository orderDetailRepository;
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private UserRepository userRepository;

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
       Integer canteenId= cart.getCartItems().get(0).getFood().getCanteen().getCanteenId();
        order.setCanteenId(canteenId);
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


    @Override
    public Page<Order> getOrdersByCanteen(Integer canteenId, List<OrderStatus> statuses, Pageable pageable) {
        return orderRepository.findOrdersByCanteenIdAndStatuses(canteenId, statuses, pageable);
    }

    @Override
    public Page<Order> getOrdersByCanteenAndType(Integer canteenId, List<OrderStatus> statuses, OrderType orderType, Pageable pageable) {
        return orderRepository.findOrdersByCanteenIdAndStatusesAndOrderTypePendingOnline(canteenId, statuses, orderType, pageable);
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
    public void assignShipperAndUpdateStatus(Integer orderId, Integer deliveryRoleId, OrderStatus newStatus, String staffName) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new IllegalStateException("Order not found"));
        order.setDeliveryRoleId(deliveryRoleId);
        order.setOrderStatus(newStatus);
        order.setDeliveryRoleName(staffName);
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

    @Override
    public Page<Order> getCompletedOrdersByCanteenAndDateRange(Integer canteenId, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        return orderRepository.findCompletedOrdersByCanteenAndDateRange(canteenId, startDate, endDate, pageable);
    }

    @Override
    public Page<Order> getOrdersByCanteenAndDeliveryRole(Integer canteenId, Integer deliveryRoleId, Pageable pageable) {
        return orderRepository.findByCanteenIdAndDeliveryRoleIdAndStatusProgress(canteenId, deliveryRoleId, pageable);
    }

    @Override
    public void completeOrder(Integer orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new IllegalArgumentException("Invalid order ID"));
        order.setOrderStatus(OrderStatus.COMPLETE);
        orderRepository.save(order);
    }

    @Override
    public List<Order> getOrdersByUserId(Integer userId) {
        return orderRepository.findByUserId(userId);
    }

    @Transactional
    public void createOrderAtCouter(Integer canteenId, List<Integer> foodIds, List<Integer> quantities, String paymentMethod,Integer totalOrderPrice) {
        // Create and save order
        Order order = new Order();
        order.setCanteenId(canteenId);
        if (paymentMethod.equalsIgnoreCase("CASH")) {
            order.setPaymentMethod(PaymentMethod.CASH);
        }
        if (paymentMethod.equalsIgnoreCase("VNPAY")) {
            order.setPaymentMethod(PaymentMethod.VNPAY);
        }
        order.setOrderCode(RandomOrderCodeGenerator.generateOrderCode());
        order.setOrderType(OrderType.AT_COUNTER);
        order.setOrderDate(LocalDateTime.now());
        order.setOrderStatus(OrderStatus.COMPLETE);
        order.setTotalOrderPrice(totalOrderPrice);
        User userAnonymous  = userRepository.findByEmail("anonymous@gmail.com");
        order.setUser(userAnonymous);
        orderRepository.save(order);

        // Create and save order details
        for (int i = 0; i < foodIds.size(); i++) {
            Integer foodId = foodIds.get(i);
            Integer quantity = quantities.get(i);

            // Update food quantity
            Food food = foodRepository.findById(foodId).orElseThrow(() -> new IllegalArgumentException("Invalid food ID: " + foodId));
            if (food.getFoodQuantity() < quantity) {
                throw new IllegalArgumentException("Insufficient quantity for food: " + food.getFoodName());
            }
            food.setFoodQuantity(food.getFoodQuantity() - quantity);
            foodRepository.save(food);

            // Create order detail
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(order);
            orderDetail.setFood(food);
            orderDetail.setQuantity(quantity);
            orderDetail.setPrice(food.getPrice() * quantity);
            orderDetailRepository.save(orderDetail);
        }
    }


}
