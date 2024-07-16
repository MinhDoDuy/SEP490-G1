package com.ffood.g1.service;

import com.ffood.g1.entity.Cart;
import com.ffood.g1.entity.Order;
import com.ffood.g1.entity.User;
import com.ffood.g1.enum_pay.OrderStatus;
import com.ffood.g1.enum_pay.PaymentStatus;
import com.ffood.g1.enum_pay.OrderType;
import com.ffood.g1.enum_pay.PaymentMethod;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface OrderService {


    List<Object[]> getBestSellingItems();

    List<Object[]> getOrderStats();



    List<Object[]> getRevenueDataByMonth();

    Order createOrder(User user, String address, Integer totalOrderPrice, String note, Cart cart, OrderType orderType, PaymentMethod paymentMethod, PaymentStatus paymentStatus, String orderCode);

    List<Object[]> getRevenueDataByYear();

    Double calculateTotalOrder();
    List<Order> getOrdersByUserIdAndStatus(Integer userId, PaymentStatus status);

    List<Order> getOrdersByCanteen(Integer canteenId, List<OrderStatus> statuses);

    List<Object[]> findRevenueDataCanteenByMonth(Integer canteenId);
    List<Object[]> findRevenueDataCanteenByYear(Integer canteenId);

    Integer countCompletedOrdersByCanteenId(Integer canteenId);


    //đặt order
    void updateOrderStatus(Integer orderId, OrderStatus newStatus);

    void cancelOrder(Integer orderId);



}
