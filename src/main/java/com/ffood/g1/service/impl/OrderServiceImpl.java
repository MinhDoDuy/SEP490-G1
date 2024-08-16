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
import com.ffood.g1.service.CartItemService;
import com.ffood.g1.service.OrderService;
import com.ffood.g1.utils.RandomOrderCodeGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.thymeleaf.context.Context;

import org.xhtmlrenderer.pdf.ITextRenderer;
import java.io.ByteArrayOutputStream;
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

    @Autowired
    private CartItemService cartItemService;


    @Override
    public Long findTotalCompletedOrders() {
        return orderRepository.findTotalCompletedOrders();
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
    public List<Object[]> findRevenueDataCanteenByMonthOnline(Integer canteenId) {
        return orderRepository.findRevenueCanteenDataByMonthOnline(canteenId);
    }

    @Override
    public List<Object[]> findRevenueDataCanteenByYearOnline(Integer canteenId) {
        return orderRepository.findRevenueDataCanteenByYearOnline(canteenId);
    }

    @Override
    public List<Object[]> findRevenueDataCanteenByMonthAtCounter(Integer canteenId) {
        return orderRepository.findRevenueCanteenDataByMonthAtCounter(canteenId);
    }

    @Override
    public List<Object[]> findRevenueDataCanteenByYearAtCounter(Integer canteenId) {
        return orderRepository.findRevenueDataCanteenByYearAtCounter(canteenId);
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
        //tại sao get 0
        details.append("Căn tin: ").append(order.getOrderDetails().get(0).getFood().getCanteen().getCanteenName()).append("\n");
        for (OrderDetail detail : order.getOrderDetails()) {
            details.append("Món ăn: \n").append(detail.getFood().getFoodName())
                    .append(", Mã đơn hàng: \n").append(detail.getOrder().getOrderCode())
                    .append(", Số lượng: \n").append(detail.getQuantity())
                    .append(", Tổng Giá: ").append(detail.getOrder().getTotalOrderPrice())
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
            String text = "Đơn hàng đang được làm đã được chuẩn bị.\n\n" + orderDetails;
            sendEmail(order.getUser().getEmail(), subject, text);
        }
    }

    @Override
    public void rejectOrder(Integer orderId, String rejectionReason) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid order ID"));
        order.setOrderStatus(OrderStatus.REJECT);

        // Cập nhật lý do từ chối vào trường note
        String currentNote = order.getNote();
        String updatedNote = (currentNote != null ? currentNote + " - " : "") + "Lý do từ chối: " + rejectionReason;
        order.setNote(updatedNote);

        orderRepository.save(order);

        // Lấy thông tin chi tiết đơn hàng
        String orderDetails = getOrderDetails(order);

        // Gửi email thông báo khi đơn hàng bị hủy
        String subject = "Đơn hàng của bạn đã bị hủy";
        String text = "Đơn hàng của bạn với mã số " + orderId + " đã bị hủy.\n\n" + orderDetails + "\n\nLý do từ chối: " + rejectionReason;
        sendEmail(order.getUser().getEmail(), subject, text);
    }


    @Override
    public List<Object[]> getOrderStatsByCanteenAndMonth(Integer canteenId) {
        return orderRepository.findOrderStatsByCanteenAndMonth(canteenId);
    }

    @Override
    public List<Object[]> getOrderStatsByCanteenAndYear(Integer canteenId) {
        return orderRepository.findOrderStatsByCanteenAndYear(canteenId);
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
    public Page<Order> getCompleteOrdersByCanteenAndDeliveryRole(Integer canteenId, Integer deliveryRoleId, Pageable pageable) {
        return orderRepository.findByCanteenIdAndDeliveryRoleIdAndStatusComplete(canteenId, deliveryRoleId, pageable);
    }

    @Override
    public void completeOrder(Integer orderId) {
        // Retrieve the order by its ID, or throw an exception if not found
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid order ID"));

        // cập nhật trạng thái đơn hàng thành  COMPLETE
        order.setOrderStatus(OrderStatus.COMPLETE);

        // lưu trạng thái mới cho order dùng jpa repository
        orderRepository.save(order);

        // Generate order details as a string
        String orderDetails = getOrderDetails(order);

        // Send email notification if the order status is COMPLETE
        if (order.getOrderStatus() == OrderStatus.COMPLETE) {
            String subject = "Đơn hàng của bạn đã hoàn thành";
            String text = "Đơn hàng của bạn với mã số " + orderId + " đã được giao thành công.\n\n" + orderDetails;
            sendEmail(order.getUser().getEmail(), subject, text);
        }
    }

    @Override
    public List<Order> getOrdersByUserId(Integer userId) {
        return orderRepository.findByUserId(userId);
    }

    @Transactional
    public void createOrderAtCouter(Integer canteenId, List<Integer> foodIds, List<Integer> quantities, String paymentMethod, Integer totalOrderPrice) {
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
        User userAnonymous = userRepository.findByEmail("anonymous@gmail.com");
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
            food.setSalesCount(food.getSalesCount() + quantity);
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

    public Order createOrder(User user, String address, String note, OrderType orderType, PaymentMethod paymentMethod, List<Integer> cartItemIds,Integer deliveryRoleId,String deliveryRoleName) {
        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setOrderAddress(address);
        order.setNote(note);
        order.setOrderType(orderType);
        order.setPaymentMethod(paymentMethod);
        order.setDeliveryRoleId(deliveryRoleId);
        order.setDeliveryRoleName(deliveryRoleName);
        if (orderType == OrderType.ONLINE_ORDER) {
            order.setOrderStatus(OrderStatus.PENDING);
        }else if (orderType == OrderType.AT_COUNTER) {
            order.setOrderStatus(OrderStatus.COMPLETE);
        }
        order.setPaymentStatus(PaymentStatus.PAYMENT_COMPLETE);
        order.setOrderCode(RandomOrderCodeGenerator.generateOrderCode());

        List<OrderDetail> orderDetails = new ArrayList<>();
        int totalOrderPrice = 0;

        int canteenId = 0;
        for (Integer cartItemId : cartItemIds) {
            CartItem cartItem = cartItemService.getCartItemById(cartItemId);
            canteenId = cartItem.getFood().getCanteen().getCanteenId();
            if (cartItem != null) {
                OrderDetail orderDetail = new OrderDetail();
                orderDetail.setOrder(order);
                orderDetail.setFood(cartItem.getFood());
                orderDetail.setQuantity(cartItem.getQuantity());
                orderDetail.setPrice(cartItem.getPrice());
                totalOrderPrice += cartItem.getPrice() * cartItem.getQuantity();

                orderDetails.add(orderDetail);

                // Update inventory
                Food food = cartItem.getFood();
                food.setFoodQuantity(food.getFoodQuantity() - cartItem.getQuantity());
                food.setSalesCount(food.getSalesCount() + cartItem.getQuantity());
                foodRepository.save(food);
            }
        }
        order.setCanteenId(canteenId);
        order.setTotalOrderPrice(totalOrderPrice);
        order.setOrderDetails(orderDetails);
        orderRepository.save(order);
        return order;
    }

    @Override
    public boolean hasActiveOrders(Integer deliveryRoleId) {
        return orderRepository.countActiveOrdersByDeliveryRoleId(deliveryRoleId) > 0;
    }

    @Override
    public List<Order> findByOrderTypeAndCurrentDate(OrderType orderType) {
        return orderRepository.findByOrderTypeAndCurrentDate(orderType);
    }

    @Override
    public Page<Order> searchRejectedOrdersByOrderCode(Integer canteenId, String keyword, Pageable pageable) {
        return orderRepository.searchRejectedOrdersByOrderCode(canteenId, keyword, pageable);
    }

    @Override
    public Page<Order> searchRefundedOrdersByOrderCode(Integer canteenId, String keyword, Pageable pageable) {
        return orderRepository.searchRefundedOrdersByOrderCode(canteenId, keyword, pageable);
    }

    @Override
    public void refundOrder(Integer orderId, String refundReason) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid order ID"));

        // Update order status to REFUND
        order.setOrderStatus(OrderStatus.REFUND);

        // Update the note with the refund reason
        String currentNote = order.getNote();
        String updatedNote = (currentNote != null ? currentNote + " - " : "") + "Lý do hoàn tiền: " + refundReason;
        order.setNote(updatedNote);

        // Save the updated order
        orderRepository.save(order);

        // Retrieve order details
        String orderDetails = getOrderDetails(order);

        // Send an email notification for the refund
        String subject = "Đơn hàng của bạn đã được hoàn tiền";
        String text = "Đơn hàng của bạn với mã số " + order.getOrderCode() + " đã được hoàn tiền.\n\n" + orderDetails + "\n\nLý do hoàn tiền: " + refundReason;
        sendEmail(order.getUser().getEmail(), subject, text);
    }


    @Override
    public List<Map<String, Object>> getSalesDataForTodayByOrderType(OrderType orderType) {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(LocalTime.MAX);

        List<Object[]> results = orderRepository.calculateSalesDataForTodayByOrderType(startOfDay, endOfDay, orderType);

        return results.stream()
                .map(result -> Map.of(
                        "deliveryRoleId", result[0],
                        "totalOrders", result[1],
                        "totalSalesAmount", result[2]
                ))
                .collect(Collectors.toList());
    }

    @Autowired
    private SpringTemplateEngine templateEngine;

    public byte[] generatePdfFromOrder(Integer orderId) throws Exception {
        Order order = findOrderById(orderId);

        Context context = new Context();
        context.setVariable("order", order);

        // Render HTML từ template Thymeleaf
        String htmlContent = templateEngine.process("order/order-pdf-template", context);

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(htmlContent);
            renderer.layout();
            renderer.createPDF(outputStream);
            return outputStream.toByteArray();
        }
    }
    public Order findOrderById(Integer orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }

}
