package com.ffood.g1.serviceImpl;

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
import org.springframework.mail.javamail.JavaMailSender;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

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
    private JavaMailSender mailSender;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CartItemService cartItemService;

    // Trường hợp bình thường: Kiểm thử lấy các đơn hàng theo canteen và trạng thái
    @Test
    void testGetOrdersByCanteen_Normal() {
        Pageable pageable = PageRequest.of(0, 10);

        // Tạo các đối tượng Order sử dụng builder
        Order order1 = Order.builder().orderId(1).orderStatus(OrderStatus.COMPLETE).canteenId(1).build();
        Order order2 = Order.builder().orderId(2).orderStatus(OrderStatus.PENDING).canteenId(1).build();

        Page<Order> expectedPage = new PageImpl<>(Arrays.asList(order1, order2));

        // Giả lập phương thức findOrdersByCanteenIdAndStatuses của repository để trả về trang giả lập
        when(orderRepository.findOrdersByCanteenIdAndStatuses(eq(1), anyList(), eq(pageable))).thenReturn(expectedPage);

        // Gọi phương thức service
        Page<Order> result = orderService.getOrdersByCanteen(1, Arrays.asList(OrderStatus.COMPLETE, OrderStatus.PENDING), pageable);

        // Kiểm tra xem trang trả về có khớp với trang mong đợi hay không
        assertEquals(expectedPage, result);

        // Xác minh rằng phương thức findOrdersByCanteenIdAndStatuses của repository đã được gọi chính xác một lần
        verify(orderRepository, times(1)).findOrdersByCanteenIdAndStatuses(eq(1), anyList(), eq(pageable));
    }

    // Trường hợp bất thường: Kiểm thử khi repository ném ra ngoại lệ khi lấy các đơn hàng theo canteen
    @Test
    void testGetOrdersByCanteen_Abnormal() {
        Pageable pageable = PageRequest.of(0, 10);

        // Giả lập repository ném ra một RuntimeException khi gọi findOrdersByCanteenIdAndStatuses
        when(orderRepository.findOrdersByCanteenIdAndStatuses(eq(1), anyList(), eq(pageable))).thenThrow(new RuntimeException("Database Error"));

        // Gọi phương thức service và kiểm tra xem có ngoại lệ nào bị ném ra hay không
        Exception exception = assertThrows(RuntimeException.class, () -> {
            orderService.getOrdersByCanteen(1, Arrays.asList(OrderStatus.COMPLETE, OrderStatus.PENDING), pageable);
        });

        // Kiểm tra xem thông điệp ngoại lệ có khớp với thông điệp mong đợi hay không
        assertEquals("Database Error", exception.getMessage());

        // Xác minh rằng phương thức findOrdersByCanteenIdAndStatuses của repository đã được gọi chính xác một lần
        verify(orderRepository, times(1)).findOrdersByCanteenIdAndStatuses(eq(1), anyList(), eq(pageable));
    }

    // Trường hợp ranh giới: Kiểm thử khi không có đơn hàng nào khớp với canteen và trạng thái
    @Test
    void testGetOrdersByCanteen_Boundary() {
        Pageable pageable = PageRequest.of(0, 10);

        // Giả lập phương thức findOrdersByCanteenIdAndStatuses của repository để trả về trang rỗng
        when(orderRepository.findOrdersByCanteenIdAndStatuses(eq(1), anyList(), eq(pageable))).thenReturn(Page.empty());

        // Gọi phương thức service
        Page<Order> result = orderService.getOrdersByCanteen(1, Arrays.asList(OrderStatus.COMPLETE, OrderStatus.PENDING), pageable);

        // Kiểm tra xem trang trả về có rỗng hay không
        assertTrue(result.isEmpty());

        // Xác minh rằng phương thức findOrdersByCanteenIdAndStatuses của repository đã được gọi chính xác một lần
        verify(orderRepository, times(1)).findOrdersByCanteenIdAndStatuses(eq(1), anyList(), eq(pageable));
    }

    // Trường hợp bình thường: Kiểm thử việc tạo đơn hàng tại quầy (createOrderAtCounter)
    @Test
    void testCreateOrderAtCounter_Normal() {
        List<Integer> foodIds = Arrays.asList(1, 2);
        List<Integer> quantities = Arrays.asList(2, 3);
        String paymentMethod = "CASH";
        Integer totalOrderPrice = 50000;

        // Tạo đối tượng Food sử dụng builder
        Food food1 = Food.builder().foodId(1).foodName("Food1").foodQuantity(10).price(10000).salesCount(0).build();
        Food food2 = Food.builder().foodId(2).foodName("Food2").foodQuantity(15).price(15000).salesCount(0).build();

        // Giả lập phương thức findById của foodRepository để trả về các đối tượng Food
        when(foodRepository.findById(1)).thenReturn(Optional.of(food1));
        when(foodRepository.findById(2)).thenReturn(Optional.of(food2));

        // Giả lập phương thức findByEmail của userRepository để trả về một đối tượng User ẩn danh
        User userAnonymous = User.builder().email("anonymous@gmail.com").userId(999).build();
        when(userRepository.findByEmail("anonymous@gmail.com")).thenReturn(userAnonymous);

        // Gọi phương thức service
        orderService.createOrderAtCouter(1, foodIds, quantities, paymentMethod, totalOrderPrice);

        // Kiểm tra xem số lượng món ăn đã được cập nhật đúng hay không
        assertEquals(8, food1.getFoodQuantity());
        assertEquals(12, food2.getFoodQuantity());

        // Kiểm tra xem số lượng bán ra đã được cập nhật đúng hay không
        assertEquals(2, food1.getSalesCount());
        assertEquals(3, food2.getSalesCount());

        // Xác minh rằng phương thức save của orderRepository và foodRepository đã được gọi chính xác
        verify(orderRepository, times(1)).save(any(Order.class));
        verify(foodRepository, times(1)).save(food1);
        verify(foodRepository, times(1)).save(food2);
        verify(orderDetailRepository, times(2)).save(any(OrderDetail.class));
    }

    // Trường hợp bất thường: Kiểm thử khi số lượng món ăn không đủ trong phương thức createOrderAtCounter
    @Test
    void testCreateOrderAtCounter_Abnormal() {
        List<Integer> foodIds = Arrays.asList(1);
        List<Integer> quantities = Arrays.asList(20);
        String paymentMethod = "CASH";
        Integer totalOrderPrice = 50000;

        // Tạo đối tượng Food sử dụng builder với số lượng ít hơn yêu cầu
        Food food1 = Food.builder().foodId(1).foodName("Food1").foodQuantity(5).price(10000).salesCount(0).build();

        // Giả lập phương thức findById của foodRepository để trả về đối tượng Food
        when(foodRepository.findById(1)).thenReturn(Optional.of(food1));

        // Gọi phương thức service và kiểm tra xem có ngoại lệ nào bị ném ra hay không
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            orderService.createOrderAtCouter(1, foodIds, quantities, paymentMethod, totalOrderPrice);
        });

        // Kiểm tra xem thông điệp ngoại lệ có khớp với thông điệp mong đợi hay không
        assertEquals("Insufficient quantity for food: Food1", exception.getMessage());

        // Xác minh rằng phương thức save của orderRepository và foodRepository không được gọi
        verify(orderRepository, times(0)).save(any(Order.class));
        verify(foodRepository, times(0)).save(any(Food.class));
        verify(orderDetailRepository, times(0)).save(any(OrderDetail.class));
    }

    // Trường hợp ranh giới: Kiểm thử khi danh sách món ăn hoặc số lượng rỗng trong phương thức createOrderAtCounter
    @Test
    void testCreateOrderAtCounter_Boundary() {
        List<Integer> foodIds = Collections.emptyList();
        List<Integer> quantities = Collections.emptyList();
        String paymentMethod = "CASH";
        Integer totalOrderPrice = 50000;

        // Gọi phương thức service và kiểm tra xem không có ngoại lệ nào bị ném ra
        orderService.createOrderAtCouter(1, foodIds, quantities, paymentMethod, totalOrderPrice);

        // Xác minh rằng phương thức save của orderRepository và foodRepository không được gọi
        verify(orderRepository, times(1)).save(any(Order.class));
        verify(foodRepository, times(0)).save(any(Food.class));
        verify(orderDetailRepository, times(0)).save(any(OrderDetail.class));
    }

    // Các bài kiểm thử khác cũng tương tự như trên, sử dụng builder để tạo các đối tượng Order, Food, User và kiểm tra các phương thức khác trong OrderServiceImpl

    // Ví dụ: Trường hợp bình thường: Kiểm thử việc hoàn thành đơn hàng (completeOrder)
    @Test
    void testCompleteOrder_Normal() {
        // Tạo đối tượng Order sử dụng builder
        Order order = Order.builder().orderId(1).orderStatus(OrderStatus.PENDING).build();

        // Giả lập phương thức findById của orderRepository để trả về đối tượng Order
        when(orderRepository.findById(1)).thenReturn(Optional.of(order));

        // Gọi phương thức service
        orderService.completeOrder(1);

        // Kiểm tra xem trạng thái đơn hàng đã được cập nhật đúng hay không
        assertEquals(OrderStatus.COMPLETE, order.getOrderStatus());

        // Xác minh rằng phương thức save của orderRepository đã được gọi chính xác một lần
        verify(orderRepository, times(1)).save(order);
    }

    // Trường hợp bất thường: Kiểm thử khi orderId không hợp lệ trong phương thức completeOrder
    @Test
    void testCompleteOrder_Abnormal() {
        // Giả lập repository ném ra một IllegalArgumentException khi gọi findById
        when(orderRepository.findById(1)).thenThrow(new IllegalArgumentException("Invalid order ID"));

        // Gọi phương thức service và kiểm tra xem có ngoại lệ nào bị ném ra hay không
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            orderService.completeOrder(1);
        });

        // Kiểm tra xem thông điệp ngoại lệ có khớp với thông điệp mong đợi hay không
        assertEquals("Invalid order ID", exception.getMessage());

        // Xác minh rằng phương thức save của orderRepository không được gọi
        verify(orderRepository, times(0)).save(any(Order.class));
    }
}
