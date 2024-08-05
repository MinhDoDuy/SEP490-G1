package com.ffood.g1.serviceImpl;

import com.ffood.g1.entity.*;
import com.ffood.g1.enum_pay.OrderStatus;
import com.ffood.g1.enum_pay.OrderType;
import com.ffood.g1.repository.*;
import com.ffood.g1.service.CartItemService;
import com.ffood.g1.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceImplTest {

    @InjectMocks
    private OrderServiceImpl orderService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private FoodRepository foodRepository;

    @Mock
    private OrderDetailRepository orderDetailRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CartItemService cartItemService;

    @Mock
    private JavaMailSender mailSender;

    @Test
    void testGetOrdersByCanteen() {
        Integer canteenId = 1;
        List<OrderStatus> statuses = Arrays.asList(OrderStatus.COMPLETE, OrderStatus.PENDING , OrderStatus.PROGRESS , OrderStatus.REJECT);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Order> expectedPage = new PageImpl<>(Collections.emptyList());

        when(orderRepository.findOrdersByCanteenIdAndStatuses(canteenId, statuses, pageable)).thenReturn(expectedPage);

        Page<Order> result = orderService.getOrdersByCanteen(canteenId, statuses, pageable);

        assertEquals(expectedPage, result);
        verify(orderRepository, times(1)).findOrdersByCanteenIdAndStatuses(canteenId, statuses, pageable);
    }

    @Test
    void testAssignShipperAndUpdateStatus() {
        Integer orderId = 1;
        Integer deliveryRoleId = 2;
        OrderStatus newStatus = OrderStatus.PROGRESS;
        String staffName = "Shipper";
        Order order = mock(Order.class);
        User user = mock(User.class);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(order.getUser()).thenReturn(user);
        when(user.getEmail()).thenReturn("test@example.com");
        when(order.getOrderDetails()).thenReturn(Collections.emptyList());

        doNothing().when(orderRepository).save(order);
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        orderService.assignShipperAndUpdateStatus(orderId, deliveryRoleId, newStatus, staffName);

        verify(order).setDeliveryRoleId(deliveryRoleId);
        verify(order).setOrderStatus(newStatus);
        verify(order).setDeliveryRoleName(staffName);
        verify(orderRepository, times(1)).save(order);
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void testRejectOrder() {
        Integer orderId = 1;
        Order order = mock(Order.class);
        User user = mock(User.class);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(order.getUser()).thenReturn(user);
        when(user.getEmail()).thenReturn("test@example.com");
        when(order.getOrderDetails()).thenReturn(Collections.emptyList());

        doNothing().when(orderRepository).save(order);
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        orderService.rejectOrder(orderId);

        verify(order).setOrderStatus(OrderStatus.REJECT);
        verify(orderRepository, times(1)).save(order);
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void testCreateOrderAtCounter() {
        Integer canteenId = 1;
        List<Integer> foodIds = Arrays.asList(1, 2);
        List<Integer> quantities = Arrays.asList(2, 3);
        String paymentMethod = "CASH";
        Integer totalOrderPrice = 500;
        Food food1 = mock(Food.class);
        Food food2 = mock(Food.class);
        User user = mock(User.class);

        when(foodRepository.findById(1)).thenReturn(Optional.of(food1));
        when(foodRepository.findById(2)).thenReturn(Optional.of(food2));
        when(userRepository.findByEmail("anonymous@gmail.com")).thenReturn(user);

        doNothing().when(food1).setFoodQuantity(anyInt());
        doNothing().when(food1).setSalesCount(anyInt());
        doNothing().when(food2).setFoodQuantity(anyInt());
        doNothing().when(food2).setSalesCount(anyInt());

        orderService.createOrderAtCouter(canteenId, foodIds, quantities, paymentMethod, totalOrderPrice);

        verify(orderRepository, times(1)).save(any(Order.class));
        verify(foodRepository, times(2)).save(any(Food.class));
        verify(orderDetailRepository, times(2)).save(any(OrderDetail.class));
    }

    @Test
    void testGetOrdersByCanteenAndType() {
        Integer canteenId = 1;
        List<OrderStatus> statuses = Arrays.asList(OrderStatus.COMPLETE, OrderStatus.PENDING);
        OrderType orderType = OrderType.ONLINE_ORDER;
        Pageable pageable = PageRequest.of(0, 10);
        Page<Order> expectedPage = new PageImpl<>(Collections.emptyList());

        when(orderRepository.findOrdersByCanteenIdAndStatusesAndOrderTypePendingOnline(canteenId, statuses, orderType, pageable)).thenReturn(expectedPage);

        Page<Order> result = orderService.getOrdersByCanteenAndType(canteenId, statuses, orderType, pageable);

        assertEquals(expectedPage, result);
        verify(orderRepository, times(1)).findOrdersByCanteenIdAndStatusesAndOrderTypePendingOnline(canteenId, statuses, orderType, pageable);
    }
}
