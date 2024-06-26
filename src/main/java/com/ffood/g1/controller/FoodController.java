package com.ffood.g1.controller;

import com.ffood.g1.entity.Category;
import com.ffood.g1.entity.Food;
import com.ffood.g1.entity.User;
import com.ffood.g1.repository.CartRepository;
import com.ffood.g1.repository.UserRepository;
import com.ffood.g1.service.CartService;
import com.ffood.g1.service.CategoryService;
import com.ffood.g1.service.FoodService;
import com.ffood.g1.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Objects;
import java.util.Optional;


@Controller
public class FoodController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private FoodService foodService;
    @Autowired
    private UserService userService;
    @Autowired
    private CartService cartService;
    @Controller
    @RequestMapping("/categories")
    public class CategoryController {


        @GetMapping
        public ModelAndView getAllCategories(Model model) {
            List<Category> categories = categoryService.getAllCategories();
            model.addAttribute("categories", categories);
            return new ModelAndView("categories");
        }

    }

    @GetMapping("/food_details")
    public String viewFoodDetails(@RequestParam("id") Integer id, Model model) {
        Optional<Food> foodOptional = foodService.getFoodByIdFoodDetails(id);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (Objects.nonNull(authentication) && authentication.isAuthenticated()) {
            String email = authentication.getName();

            User user = userService.findByEmail(email);
            if (Objects.nonNull(user)) {
                model.addAttribute("user", user);
                int totalQuantity = cartService.getTotalQuantityByUser(user);
                model.addAttribute("totalQuantity", totalQuantity);
            }
        }
        if (foodOptional.isPresent()) {
            Food food = foodOptional.get();
            model.addAttribute("food", food);

            // Lấy các sản phẩm cùng danh mục
            List<Food> relatedFoods = foodService.getFoodsByCategory(food.getCategory().getCategoryId());
            model.addAttribute("relatedFoods", relatedFoods);



            // get random 12 items and display in homepage
            List<Food> items_home = foodService.getRandomFood();
            model.addAttribute("items_home", items_home);

            return "food_details";
        } else {
            // Handle case when food is not found, e.g., redirect to an error page or show a message
            return "error";
        }
    }
}
