package com.ffood.g1.endpoint;

import com.ffood.g1.entity.Canteen;
import com.ffood.g1.entity.Role;
import com.ffood.g1.entity.User;
import com.ffood.g1.repository.CanteenRepository;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@AutoConfigureMockMvc
public class StaffManageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private CanteenRepository canteenRepository;

    private User manager;
    private Canteen canteen;
    private User laxagaUser;

    @BeforeEach
    @Transactional
    public void setup() {
        // Fetching the existing manager
        Role managerRole = roleRepository.findById(3)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        canteen = canteenRepository.findById(2)
                .orElseThrow(() -> new RuntimeException("Canteen not found"));

        manager = userRepository.findByEmail("doduyminh178@gmail.com");

        if (!manager.getRole().getRoleId().equals(managerRole.getRoleId())) {
            throw new RuntimeException("User does not have the correct role");
        }

        if (!manager.getCanteen().getCanteenId().equals(canteen.getCanteenId())) {
            throw new RuntimeException("User does not belong to the correct canteen");
        }

    }

    @Test
    @WithUserDetails("doduyminh178@gmail.com")
    public void testListStaff() throws Exception {
        mockMvc.perform(get("/manage-staff")
                        .param("canteenId", String.valueOf(canteen.getCanteenId())))
                .andExpect(status().isOk())
                .andExpect(view().name("staff-management/manage-staff"));
    }

    @Test
    @WithUserDetails("doduyminh178@gmail.com")
    public void testSearchStaff() throws Exception {
        mockMvc.perform(get("/search-staff")
                        .param("canteenId", String.valueOf(canteen.getCanteenId()))
                        .param("keyword", "Laxaga"))
                .andExpect(status().isOk())
                .andExpect(view().name("staff-management/manage-staff"));
    }


    @Test
    @WithUserDetails("doduyminh178@gmail.com")
    public void testFireStaff() throws Exception {
        mockMvc.perform(post("/edit-staff")
                        .param("userId", String.valueOf(laxagaUser.getUserId()))
                        .param("canteenId", String.valueOf(canteen.getCanteenId())))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/manage-staff?canteenId=" + canteen.getCanteenId()))
                .andExpect(flash().attributeExists("successMessage"));
    }

}
