package com.ffood.g1.service;

import com.ffood.g1.entity.Cart;
import com.ffood.g1.entity.Order;
import com.ffood.g1.entity.User;
import com.ffood.g1.enum_pay.OrderStatus;
import com.ffood.g1.enum_pay.PaymentStatus;
import com.ffood.g1.enum_pay.OrderType;
import com.ffood.g1.enum_pay.PaymentMethod;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public interface OrderService {


    Page<Order> getOrdersByCanteen(Integer canteenId, List<OrderStatus> statuses, Pageable pageable);

    List<Object[]> findRevenueDataCanteenByMonth(Integer canteenId);
    List<Object[]> findRevenueDataCanteenByYear(Integer canteenId);

    Integer countCompletedOrdersByCanteenId(Integer canteenId);

    Page<Order> getOrdersByCanteenAndType(Integer canteenId, List<OrderStatus> statuses, OrderType orderType, Pageable pageable);


    void assignShipperAndUpdateStatus(Integer orderId, Integer shipperId, OrderStatus newStatus, String staffName);

    void rejectOrder(Integer orderId , String note);

    //đơn thành công theo Tháng của canteen màn hình dashboard
    List<Object[]> getOrderStatsByCanteenAndMonth(Integer canteenId);
    //đơn thành công theo Năm của canteen màn hình dashboard
    List<Object[]> getOrderStatsByCanteenAndYear(Integer canteenId);

    List<Object[]> getBestSellingItemsByCanteen(Integer canteenId);


    Page<Order> getCompletedOrdersByCanteenAndDateRange(Integer canteenId, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    Page<Order> getOrdersByCanteenAndDeliveryRole(Integer canteenId, Integer deliveryRoleId, Pageable pageable);

    void completeOrder(Integer orderId);


    List<Order> getOrdersByUserId(Integer userId);

    void createOrderAtCouter(Integer canteenId, List<Integer> foodIds, List<Integer> quantities, String paymentMethod,Integer totalOrderPrice);

    Order createOrder(User user, String address, String note, OrderType orderType, PaymentMethod paymentMethod, List<Integer> cartItemIds);

    boolean hasActiveOrders(Integer deliveryRoleId);

    List<Order> findByOrderTypeAndCurrentDate(OrderType orderType);
}
