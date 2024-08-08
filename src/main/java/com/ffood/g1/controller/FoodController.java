package com.ffood.g1.controller;

import com.ffood.g1.entity.Category;
import com.ffood.g1.entity.Feedback;
import com.ffood.g1.entity.Food;
import com.ffood.g1.entity.User;
import com.ffood.g1.enum_pay.FeedbackStatus;
import com.ffood.g1.repository.FoodRepository;
import com.ffood.g1.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
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
    @Autowired
    private FoodRepository categoryRepository;

    @Autowired
    private FeedbackService feedbackService;
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


    @GetMapping("/foodByCategory/{categoryId}")
    public @ResponseBody List<Food> getFoodsByCategoryId(@PathVariable Integer categoryId) {
        return foodService.getFoodsByCategoryId(categoryId);
    }
    @GetMapping("/food_details")
    public String viewFoodDetails(@RequestParam("id") Integer foodId, Model model, RedirectAttributes redirectAttributes, HttpSession session) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = null;
        if (Objects.nonNull(authentication) && authentication.isAuthenticated()) {
            String email = authentication.getName();
            user = userService.findByEmail(email);
            if (Objects.nonNull(user)) {
                if (!user.getIsActive()) {
                    // Đăng xuất người dùng
                    SecurityContextHolder.clearContext();
                    // Thêm thông báo lỗi vào RedirectAttributes
                    redirectAttributes.addFlashAttribute("errorMessage", "Tài khoản của bạn đã bị khóa.");
                    return "redirect:/login";
                }
                model.addAttribute("user", user);
                Integer finalTotalQuantity = cartService.getTotalQuantityByUser(user);
                int totalQuantity = finalTotalQuantity != null ? finalTotalQuantity : 0;
                session.setAttribute("totalQuantity", totalQuantity);
            }
        }
        Optional<Food> foodOptional = foodService.getFoodByIdFoodDetails(foodId);
        if (foodOptional.isPresent()) {
            Food food = foodOptional.get();
            model.addAttribute("food", food);

            // Lấy các sản phẩm cùng danh mục
            List<Food> relatedFoods = foodService.getFoodsByCategory(food.getCategory().getCategoryId());
            model.addAttribute("relatedFoods", relatedFoods);

            List<Category> categories = categoryService.getAllCategories();
            model.addAttribute("categories", categories);

            // get random 12 items and display in homepage
            List<Food> items_home = foodService.getRandomFood();
            model.addAttribute("items_home", items_home);

            List<Feedback> feedbacksFood = feedbackService.getFeedbacksByFoodIdAndStatus(foodId, FeedbackStatus.COMPLETE);
            model.addAttribute("feedbacksFood", feedbacksFood);

            String messageAddFood = (String) session.getAttribute("messageAddFood");
            if (messageAddFood != null) {
                model.addAttribute("messageAddFood", messageAddFood);
                session.removeAttribute("messageAddFood");
                System.out.println("Oi caidjt con me ni"+messageAddFood);
            }
            return "canteen/food-details";
        } else {
            return "error";
        }
    }

}
