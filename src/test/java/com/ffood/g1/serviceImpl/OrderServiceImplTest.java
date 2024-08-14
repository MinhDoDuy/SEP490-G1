package com.ffood.g1.serviceImpl;

import com.ffood.g1.entity.*;
import com.ffood.g1.enum_pay.OrderStatus;
import com.ffood.g1.enum_pay.OrderType;
import com.ffood.g1.enum_pay.PaymentMethod;
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

    // Kiểm thử chức năng tìm tổng số đơn hàng đã hoàn thành
    @Test
    void testFindTotalCompletedOrders_Normal() {
        when(orderRepository.findTotalCompletedOrders()).thenReturn(100L);

        Long result = orderService.findTotalCompletedOrders();

        assertEquals(100L, result);
        verify(orderRepository, times(1)).findTotalCompletedOrders();
    }

    // Kiểm thử chức năng lấy danh sách đơn hàng theo căn tin và trạng thái
    @Test
    void testGetOrdersByCanteen_Normal() {
        Pageable pageable = PageRequest.of(0, 10);
        Order order1 = new Order();
        Order order2 = new Order();

        Page<Order> page = new PageImpl<>(Arrays.asList(order1, order2), pageable, 2);

        when(orderRepository.findOrdersByCanteenIdAndStatuses(1, Collections.singletonList(OrderStatus.COMPLETE), pageable)).thenReturn(page);

        Page<Order> result = orderService.getOrdersByCanteen(1, Collections.singletonList(OrderStatus.COMPLETE), pageable);

        assertEquals(2, result.getTotalElements());
        assertEquals(2, result.getContent().size());
        verify(orderRepository, times(1)).findOrdersByCanteenIdAndStatuses(1, Collections.singletonList(OrderStatus.COMPLETE), pageable);
    }

    @Test
    void testAssignShipperAndUpdateStatus_Normal() {
        // Tạo đối tượng User
        User user = new User();
        user.setEmail("user@example.com");

        // Tạo đối tượng Food và Canteen
        Canteen canteen = new Canteen();
        canteen.setCanteenName("Canteen 1");

        Food food = new Food();
        food.setCanteen(canteen);

        // Tạo đối tượng OrderDetail và Order
        Order order = new Order();
        order.setUser(user);
        order.setOrderCode("ORD123");

        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setFood(food);
        orderDetail.setOrder(order);  // Liên kết OrderDetail với Order

        // Thêm OrderDetail vào danh sách trong Order
        order.setOrderDetails(Collections.singletonList(orderDetail));

        // Giả lập hành vi của orderRepository
        when(orderRepository.findById(1)).thenReturn(Optional.of(order));

        // Gọi phương thức và kiểm tra kết quả
        orderService.assignShipperAndUpdateStatus(1, 2, OrderStatus.PROGRESS, "Shipper Name");

        // Kiểm tra các giá trị sau khi cập nhật
        assertEquals(2, order.getDeliveryRoleId());
        assertEquals(OrderStatus.PROGRESS, order.getOrderStatus());
        assertEquals("Shipper Name", order.getDeliveryRoleName());

        // Kiểm tra xem phương thức save có được gọi không
        verify(orderRepository, times(1)).save(order);
    }


    // Kiểm thử chức năng từ chối đơn hàng
    @Test
    void testRejectOrder_Normal() {
        // Tạo đối tượng User
        User user = new User();
        user.setEmail("user@example.com");

        // Tạo đối tượng Food và Canteen
        Canteen canteen = new Canteen();
        canteen.setCanteenName("Canteen 1");

        Food food = new Food();
        food.setCanteen(canteen);

        // Tạo đối tượng OrderDetail và Order
        Order order = new Order();
        order.setUser(user);
        order.setOrderCode("ORD123");

        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setFood(food);
        orderDetail.setOrder(order);  // Liên kết OrderDetail với Order

        // Thêm OrderDetail vào danh sách trong Order
        order.setOrderDetails(Collections.singletonList(orderDetail));

        // Giả lập hành vi của orderRepository
        when(orderRepository.findById(1)).thenReturn(Optional.of(order));

        // Gọi phương thức rejectOrder và kiểm tra kết quả
        orderService.rejectOrder(1, "Out of stock");

        // Kiểm tra các giá trị sau khi cập nhật
        assertEquals(OrderStatus.REJECT, order.getOrderStatus());
        assertTrue(order.getNote().contains("Out of stock"));

        // Kiểm tra xem phương thức save có được gọi không
        verify(orderRepository, times(1)).save(order);
    }


    // Kiểm thử chức năng tạo đơn hàng tại quầy
    @Test
    void testCreateOrderAtCounter_Normal() {
        // Tạo đối tượng User giả lập
        User anonymousUser = new User();
        when(userRepository.findByEmail("anonymous@gmail.com")).thenReturn(anonymousUser);

        // Tạo đối tượng Food với các giá trị cần thiết
        Food food = new Food();
        food.setFoodQuantity(10);
        food.setSalesCount(0);
        food.setPrice(100);  // Đặt giá trị cho price

        when(foodRepository.findById(1)).thenReturn(Optional.of(food));

        // Thực hiện kiểm thử phương thức createOrderAtCouter
        orderService.createOrderAtCouter(1, Collections.singletonList(1), Collections.singletonList(2), "CASH", 200);

        // Kiểm tra xem các phương thức save có được gọi đúng cách hay không
        verify(orderRepository, times(1)).save(any(Order.class));
        verify(orderDetailRepository, times(1)).save(any(OrderDetail.class));
        verify(foodRepository, times(1)).save(any(Food.class));
    }


    // Kiểm thử chức năng lấy đơn hàng theo loại và ngày hiện tại
    @Test
    void testFindByOrderTypeAndCurrentDate_Normal() {
        Order order1 = new Order();
        Order order2 = new Order();

        when(orderRepository.findByOrderTypeAndCurrentDate(OrderType.ONLINE_ORDER)).thenReturn(Arrays.asList(order1, order2));

        List<Order> result = orderService.findByOrderTypeAndCurrentDate(OrderType.ONLINE_ORDER);

        assertEquals(2, result.size());
        verify(orderRepository, times(1)).findByOrderTypeAndCurrentDate(OrderType.ONLINE_ORDER);
    }

    // Kiểm thử chức năng tìm kiếm đơn hàng bị từ chối theo mã đơn hàng
    @Test
    void testSearchRejectedOrdersByOrderCode_Normal() {
        Pageable pageable = PageRequest.of(0, 10);
        Order order1 = new Order();
        Order order2 = new Order();

        Page<Order> page = new PageImpl<>(Arrays.asList(order1, order2), pageable, 2);

        when(orderRepository.searchRejectedOrdersByOrderCode(1, "ORDER123", pageable)).thenReturn(page);

        Page<Order> result = orderService.searchRejectedOrdersByOrderCode(1, "ORDER123", pageable);

        assertEquals(2, result.getTotalElements());
        verify(orderRepository, times(1)).searchRejectedOrdersByOrderCode(1, "ORDER123", pageable);
    }

    // Kiểm thử chức năng hoàn tất đơn hàng
    @Test
    void testCompleteOrder_Normal() {
        // Tạo đối tượng User giả lập
        User user = new User();

        // Tạo đối tượng Food và Canteen giả lập
        Canteen canteen = new Canteen();
        canteen.setCanteenName("Test Canteen");

        Food food = new Food();
        food.setFoodName("Test Food");
        food.setCanteen(canteen);

        // Tạo đối tượng OrderDetail với Food hợp lệ
        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setFood(food);

        // Tạo đối tượng Order với OrderDetail hợp lệ
        Order order = new Order();
        order.setUser(user);
        order.setOrderDetails(Arrays.asList(orderDetail));

        // Thiết lập order cho orderDetail để tránh lỗi NullPointerException
        orderDetail.setOrder(order);

        // Thiết lập hành vi giả lập của orderRepository
        when(orderRepository.findById(1)).thenReturn(Optional.of(order));

        // Thực hiện kiểm thử phương thức completeOrder
        orderService.completeOrder(1);

        // Kiểm tra trạng thái đơn hàng
        assertEquals(OrderStatus.COMPLETE, order.getOrderStatus());

        // Kiểm tra xem phương thức save có được gọi đúng cách hay không
        verify(orderRepository, times(1)).save(order);
    }



    // Kiểm thử chức năng kiểm tra xem shipper có đơn hàng đang hoạt động hay không
    @Test
    void testHasActiveOrders_Normal() {
        when(orderRepository.countActiveOrdersByDeliveryRoleId(2)).thenReturn(3L);

        boolean result = orderService.hasActiveOrders(2);

        assertTrue(result);
        verify(orderRepository, times(1)).countActiveOrdersByDeliveryRoleId(2);
    }

    // Kiểm thử chức năng lấy các mặt hàng bán chạy nhất theo căn tin
    @Test
    void testGetBestSellingItemsByCanteen_Normal() {
        when(orderRepository.findBestSellingItemsByCanteen(1)).thenReturn(Arrays.asList(new Object[]{"Item 1", 100}, new Object[]{"Item 2", 50}));

        List<Object[]> result = orderService.getBestSellingItemsByCanteen(1);

        assertEquals(2, result.size());
        assertEquals("Item 1", result.get(0)[0]);
        assertEquals(100, result.get(0)[1]);
        verify(orderRepository, times(1)).findBestSellingItemsByCanteen(1);
    }
}
