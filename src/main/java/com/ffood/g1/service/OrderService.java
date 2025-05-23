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
import java.util.Map;
import java.util.Optional;

@Service
public interface OrderService {

    Long findTotalCompletedOrders();


    Page<Order> getOrdersByCanteen(Integer canteenId, List<OrderStatus> statuses, Pageable pageable);

    List<Object[]> findRevenueDataCanteenByMonthOnline(Integer canteenId);
    List<Object[]> findRevenueDataCanteenByYearOnline(Integer canteenId);
    List<Object[]> findRevenueDataCanteenByMonthAtCounter(Integer canteenId);
    List<Object[]> findRevenueDataCanteenByYearAtCounter(Integer canteenId);



    Page<Order> getOrdersByCanteenAndType(Integer canteenId, List<OrderStatus> statuses, OrderType orderType, Pageable pageable);


    void assignShipperAndUpdateStatus(Integer orderId, Integer shipperId, OrderStatus newStatus, String staffName);

    void rejectOrder(Integer orderId , String note);

    //đơn thành công theo Tháng của canteen màn hình dashboard
    List<Object[]> getOrderStatsByCanteenAndMonth(Integer canteenId);
    //đơn thành công theo Năm của canteen màn hình dashboard
    List<Object[]> getOrderStatsByCanteenAndYear(Integer canteenId);

    List<Object[]> getBestSellingItemsByCanteen(Integer canteenId);


    Page<Order> getCompletedOrdersByCanteenAndDateRange(Integer canteenId, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    //lấy order trạng thái progress
    Page<Order> getOrdersByCanteenAndDeliveryRole(Integer canteenId, Integer deliveryRoleId, Pageable pageable);
    //lấy order trạng thái complete
    Page<Order> getCompleteOrdersByCanteenAndDeliveryRole(Integer canteenId, Integer deliveryRoleId, Pageable pageable);

    void completeOrder(Integer orderId);


    List<Order> getOrdersByUserId(Integer userId);

    void createOrderAtCouter(Integer canteenId, List<Integer> foodIds, List<Integer> quantities, String paymentMethod,Integer totalOrderPrice);

    Order createOrder(User user, String address, String note, OrderType orderType, PaymentMethod paymentMethod, List<Integer> cartItemIds, Integer deliveryRoleId, String deliveryRoleName);

    boolean hasActiveOrders(Integer deliveryRoleId);

    List<Order> findByOrderTypeAndCurrentDate(OrderType orderType);

    Page<Order> searchRejectedOrdersByOrderCode(Integer canteenId, String keyword, Pageable pageable);

    Page<Order> searchRefundedOrdersByOrderCode(Integer canteenId, String keyword, Pageable pageable);



    byte[] generatePdfFromOrder(Integer orderId) throws Exception;
    void refundOrder(Integer orderId, String refundReason);

    String getTotalRevenueForCurrentMonthFormatted(Integer canteenId);

    List<Object[]> getRevenueDataCanteenByDayOnline(Integer canteenId);
    List<Object[]> getRevenueDataCanteenByDayAtCounter(Integer canteenId);

    void bulkAssignAndUpdateOrders(List<Integer> selectedOrders, Integer deliveryRoleId, String fullName);

    Page<Order> searchProgressOrdersByOrderCode(Integer canteenId, String keyword, Pageable pageable);
}
