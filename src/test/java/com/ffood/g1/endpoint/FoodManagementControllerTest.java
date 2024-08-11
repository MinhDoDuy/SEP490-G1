package com.ffood.g1.endpoint;

import com.ffood.g1.entity.Category;
import com.ffood.g1.entity.Canteen;
import com.ffood.g1.entity.Food;
import com.ffood.g1.entity.User;
import com.ffood.g1.repository.CategoryRepository;
import com.ffood.g1.repository.CanteenRepository;
import com.ffood.g1.repository.FoodRepository;
import com.ffood.g1.repository.UserRepository;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@AutoConfigureMockMvc
public class FoodManagementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private FoodRepository foodRepository;

    @Autowired
    private CanteenRepository canteenRepository;

    private User user;
    private Integer canteenId = 2;

    @BeforeEach
    @Transactional
    public void setup() {
        // Retrieve the user for testing
        user = userRepository.findByEmail("doduyminh178@gmail.com");

        // Verify that the user has the correct role and belongs to the correct canteen
        if (!user.getRole().getRoleId().equals(3)) {
            throw new RuntimeException("User does not have the correct role");
        }
        if (!user.getCanteen().getCanteenId().equals(canteenId)) {
            throw new RuntimeException("User does not belong to the correct canteen");
        }

        // Create a sample category for testing
        Category category = Category.builder()
                .categoryName("Phở")
                .description("Các loại Phở")
                .build();
        categoryRepository.save(category);

        // Retrieve the canteen object
        Canteen canteen = user.getCanteen();

        // Create a sample food item for testing
        Food food = Food.builder()
                .foodName("Phở Gà")
                .description("Phở Gà ngon")
                .foodQuantity(10)
                .price(35000)
                .category(category)
                .canteen(canteen)  // Set the canteen object
                .salesCount(5)
                .build();
        foodRepository.save(food);
    }

    @Test
    @WithUserDetails("doduyminh178@gmail.com")
    public void testManageFood_withRealUser() throws Exception {
        mockMvc.perform(get("/manage-food")
                        .param("canteenId", String.valueOf(canteenId))
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(view().name("staff-management/manage-food"));
    }

    @Test
    @WithUserDetails("doduyminh178@gmail.com")
    public void testSearchFood_withRealUser() throws Exception {
        mockMvc.perform(get("/search-food")
                        .param("canteenId", String.valueOf(canteenId))
                        .param("keyword", "Phở Gà")
                        .param("categoryId", "1")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(view().name("staff-management/manage-food"));
    }
}
