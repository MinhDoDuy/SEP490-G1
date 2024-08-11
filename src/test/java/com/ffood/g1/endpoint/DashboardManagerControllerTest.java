package com.ffood.g1.endpoint;

import com.ffood.g1.entity.Role;
import com.ffood.g1.entity.User;
import com.ffood.g1.repository.RoleRepository;
import com.ffood.g1.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@AutoConfigureMockMvc
public class DashboardManagerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    private User user;
    private Role managerRole;
    private Integer canteenId = 2;

    @BeforeEach
    @Transactional
    public void setup() {
        // Lấy Role Manager đã tồn tại trong cơ sở dữ liệu
        managerRole = roleRepository.findById(3)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        // Lấy User Manager đã tồn tại trong cơ sở dữ liệu
        user = userRepository.findByEmail("doduyminh178@gmail.com");

        // Kiểm tra xem vai trò và canteen đã được gán đúng chưa
        if (!user.getRole().getRoleId().equals(managerRole.getRoleId())) {
            throw new RuntimeException("User does not have the correct role");
        }
        if (!user.getCanteen().getCanteenId().equals(canteenId)) {
            throw new RuntimeException("User does not belong to the correct canteen");
        }
    }

    @Test
    @WithUserDetails("doduyminh178@gmail.com")
    public void testShowDashboardAsManager() throws Exception {
        mockMvc.perform(get("/dashboard-manager")
                        .param("canteenId", String.valueOf(canteenId)))
                .andExpect(status().isOk())//  trạng thái 200 OK
                .andExpect(view().name("staff-management/dashboard-manager"))
                .andExpect(model().attributeExists("staffCount"))
                .andExpect(model().attributeExists("foodCount"))
                .andExpect(model().attributeExists("completedOrdersCount"))
                .andExpect(model().attributeExists("revenueLabelsByMonth"))
                .andExpect(model().attributeExists("revenueDataByMonth"))
                .andExpect(model().attributeExists("revenueLabelsByYear"))
                .andExpect(model().attributeExists("revenueDataByYear"))
                .andExpect(model().attributeExists("orderLabelsByMonth"))
                .andExpect(model().attributeExists("orderDataByMonth"))
                .andExpect(model().attributeExists("orderLabelsByYear"))
                .andExpect(model().attributeExists("orderDataByYear"))
                .andExpect(model().attributeExists("bestSellingLabels"))
                .andExpect(model().attributeExists("bestSellingData"))
                .andExpect(model().attribute("canteenId", canteenId));
    }
}
