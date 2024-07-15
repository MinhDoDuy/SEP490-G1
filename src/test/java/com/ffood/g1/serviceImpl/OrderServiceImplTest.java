package com.ffood.g1.serviceImpl;

import com.ffood.g1.entity.*;
import com.ffood.g1.enum_pay.OrderStatus;
import com.ffood.g1.enum_pay.PaymentMethod;
import com.ffood.g1.enum_pay.PaymentStatus;
import com.ffood.g1.enum_pay.OrderType;
import com.ffood.g1.repository.OrderRepository;
import com.ffood.g1.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceImplTest {

    @InjectMocks
    private OrderServiceImpl service;

    @Mock
    private OrderRepository repository;

    @Test
    void getBestSellingItems() {
        List<Object[]> bestSellingItems = Arrays.asList(new Object[]{"Item1", 10}, new Object[]{"Item2", 5});
        when(repository.findBestSellingItems()).thenReturn(bestSellingItems);

        List<Object[]> result = service.getBestSellingItems();

        assertEquals(2, result.size());
        verify(repository, times(1)).findBestSellingItems();
    }

    @Test
    void getOrderStats() {
        List<Object[]> orderStats = Arrays.asList(new Object[]{"Canteen1", 20}, new Object[]{"Canteen2", 15});
        when(repository.findOrderStats()).thenReturn(orderStats);

        List<Object[]> result = service.getOrderStats();

        assertEquals(2, result.size());
        verify(repository, times(1)).findOrderStats();
    }

    @Test
    void getRevenueDataByMonth() {
        List<Object[]> revenueDataByMonth = Arrays.asList(new Object[]{"2023-01", 1000.0}, new Object[]{"2023-02", 1500.0});
        when(repository.findRevenueDataByMonth()).thenReturn(revenueDataByMonth);

        List<Object[]> result = service.getRevenueDataByMonth();

        assertEquals(2, result.size());
        verify(repository, times(1)).findRevenueDataByMonth();
    }

    @Test
    void getRevenueDataByYear() {
        List<Object[]> revenueDataByYear = Arrays.asList(new Object[]{"2023", 12000.0}, new Object[]{"2024", 18000.0});
        when(repository.findRevenueDataByYear()).thenReturn(revenueDataByYear);

        List<Object[]> result = service.getRevenueDataByYear();

        assertEquals(2, result.size());
        verify(repository, times(1)).findRevenueDataByYear();
    }

    @Test
    void calculateTotalOrder() {
        Double totalOrder = 50000.0;
        when(repository.findTotalOrder()).thenReturn(totalOrder);

        Double result = service.calculateTotalOrder();

        assertEquals(totalOrder, result);
        verify(repository, times(1)).findTotalOrder();
    }

    @Test
    void createOrder() {
        User user = User.builder().userId(1).build();
        CartItem cartItem1 = CartItem.builder().food(Food.builder().foodId(1).build()).quantity(2).price(100).build();
        CartItem cartItem2 = CartItem.builder().food(Food.builder().foodId(2).build()).quantity(1).price(200).build();
        Cart cart = Cart.builder().cartItems(Arrays.asList(cartItem1, cartItem2)).build();

        Order order = service.createOrder(user, "123 Main St", 400, "Note", cart, OrderType.ONLINE_ORDER, PaymentMethod.VNPAY, PaymentStatus.PAYMENT_COMPLETE, "ORDER123");

        assertEquals(user, order.getUser());
        assertEquals("123 Main St", order.getOrderAddress());
        assertEquals(400, order.getTotalOrderPrice());
        assertEquals("Note", order.getNote());
        assertEquals(OrderType.ONLINE_ORDER, order.getOrderType());
        assertEquals(PaymentMethod.VNPAY, order.getPaymentMethod());
        assertEquals(PaymentStatus.PAYMENT_COMPLETE, order.getPaymentStatus());
        assertEquals("ORDER123", order.getOrderCode());
        assertEquals(OrderStatus.READY, order.getOrderStatus());
        assertEquals(2, order.getOrderDetails().size());
        assertEquals(cartItem1.getFood(), order.getOrderDetails().get(0).getFood());
        assertEquals(cartItem1.getQuantity(), order.getOrderDetails().get(0).getQuantity());
        assertEquals(cartItem1.getPrice(), order.getOrderDetails().get(0).getPrice());
    }

    @Test
    void getOrdersByUserIdAndStatus() {
        Integer userId = 1;
        PaymentStatus paymentStatus = PaymentStatus.PAYMENT_COMPLETE;
        List<Order> orders = Arrays.asList(Order.builder().orderId(1).build(), Order.builder().orderId(2).build());

        when(repository.findByUserUserIdAndPaymentStatus(userId, paymentStatus)).thenReturn(orders);

        List<Order> result = service.getOrdersByUserIdAndStatus(userId, paymentStatus);

        assertEquals(2, result.size());
        verify(repository, times(1)).findByUserUserIdAndPaymentStatus(userId, paymentStatus);
    }

    @Test
    void getOrdersByCanteen() {
        Integer canteenId = 1;
        List<OrderStatus> statuses = Arrays.asList(OrderStatus.READY, OrderStatus.COMPLETE);
        List<Order> orders = Arrays.asList(Order.builder().orderId(1).build(), Order.builder().orderId(2).build());

        when(repository.findOrdersByCanteenIdAndStatuses(canteenId, statuses)).thenReturn(orders);

        List<Order> result = service.getOrdersByCanteen(canteenId, statuses);

        assertEquals(2, result.size());
        verify(repository, times(1)).findOrdersByCanteenIdAndStatuses(canteenId, statuses);
    }

    @Test
    void findRevenueDataCanteenByMonth() {
        Integer canteenId = 1;
        List<Object[]> revenueDataByMonth = Arrays.asList(new Object[]{"2023-01", 1000.0}, new Object[]{"2023-02", 1500.0});

        when(repository.findRevenueCanteenDataByMonth(canteenId)).thenReturn(revenueDataByMonth);

        List<Object[]> result = service.findRevenueDataCanteenByMonth(canteenId);

        assertEquals(2, result.size());
        verify(repository, times(1)).findRevenueCanteenDataByMonth(canteenId);
    }

    @Test
    void findRevenueDataCanteenByYear() {
        Integer canteenId = 1;
        List<Object[]> revenueDataByYear = Arrays.asList(new Object[]{"2023", 12000.0}, new Object[]{"2024", 18000.0});

        when(repository.findRevenueDataCanteenByYear(canteenId)).thenReturn(revenueDataByYear);

        List<Object[]> result = service.findRevenueDataCanteenByYear(canteenId);

        assertEquals(2, result.size());
        verify(repository, times(1)).findRevenueDataCanteenByYear(canteenId);
    }

    @Test
    void countCompletedOrdersByCanteenId() {
        Integer canteenId = 1;
        Integer count = 50;

        when(repository.countCompletedOrdersByCanteenId(canteenId)).thenReturn(count);

        Integer result = service.countCompletedOrdersByCanteenId(canteenId);

        assertEquals(count, result);
        verify(repository, times(1)).countCompletedOrdersByCanteenId(canteenId);
    }
}
