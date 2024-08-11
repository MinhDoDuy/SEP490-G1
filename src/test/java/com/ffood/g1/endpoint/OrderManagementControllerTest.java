package com.ffood.g1.endpoint;

import com.ffood.g1.entity.*;
import com.ffood.g1.enum_pay.OrderStatus;
import com.ffood.g1.enum_pay.OrderType;
import com.ffood.g1.enum_pay.PaymentMethod;
import com.ffood.g1.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@AutoConfigureMockMvc
public class OrderManagementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CanteenRepository canteenRepository;

    private User manager;
    private Canteen canteen; // Use the actual Canteen entity

    @BeforeEach
    @Transactional
    public void setup() {
        // Retrieve the manager user
        manager = userRepository.findByEmail("doduyminh178@gmail.com");

        // Retrieve or create the Canteen entity
        canteen = canteenRepository.findById(2)
                .orElseThrow(() -> new RuntimeException("Canteen not found"));

        // Set up some initial orders if needed for testing
        Order order1 = Order.builder()
                .orderCode("ORD001")
                .orderStatus(OrderStatus.PENDING)
                .orderType(OrderType.ONLINE_ORDER)
                .paymentMethod(PaymentMethod.valueOf("VNPAY"))// Set the order type
                .user(manager)
                .totalOrderPrice(50000)
                .orderDate(LocalDateTime.now())
                .build();

        // Set the canteen directly
        order1.setCanteenId(2);

        orderRepository.save(order1);
    }

    @Test
    @WithUserDetails("doduyminh178@gmail.com")
    public void testManageOrders_withRealUser() throws Exception {
        mockMvc.perform(get("/order-list/" + canteen.getCanteenId())
                        .param("orderStatus", "PENDING"))
                .andExpect(status().isOk())
                .andExpect(view().name("staff-management/order-list"));
    }

    @Test
    @WithUserDetails("doduyminh178@gmail.com")
    public void testUpdateOrderStatus() throws Exception {
        mockMvc.perform(post("/update-order-status/1")
                        .param("canteenId", String.valueOf(canteen.getCanteenId()))
                        .param("deliveryRoleId", "1")
                        .param("newStatus", "COMPLETE"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/order-list/" + canteen.getCanteenId()))
                .andExpect(flash().attributeExists("message"));
    }

    @Test
    @WithUserDetails("doduyminh178@gmail.com")
    public void testBulkAssignOrders() throws Exception {
        mockMvc.perform(post("/bulk-assign-orders")
                        .param("canteenId", String.valueOf(canteen.getCanteenId()))
                        .param("bulkDeliveryRoleId", "1")
                        .param("selectedOrders", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/order-list/" + canteen.getCanteenId() + "?orderStatus=PENDING"))
                .andExpect(flash().attributeExists("message"));
    }

    @Test
    @WithUserDetails("doduyminh178@gmail.com")
    public void testRejectOrder() throws Exception {
        mockMvc.perform(post("/reject-order")
                        .param("orderId", "1")
                        .param("canteenId", String.valueOf(canteen.getCanteenId()))
                        .param("note", "Order rejected"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/order-list/" + canteen.getCanteenId()))
                .andExpect(flash().attributeExists("message"));
    }
}
