package com.ffood.g1.controller;

import com.ffood.g1.entity.Category;
import com.ffood.g1.entity.Food;
import com.ffood.g1.service.CategoryService;
import com.ffood.g1.service.FoodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Optional;

public class FoodController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private FoodService foodService;


    @Controller
    @RequestMapping("/categories")
    public class CategoryController {



        @GetMapping
        public ModelAndView getAllCategories(Model model) {
            List<Category> categories = categoryService.getAllCategories();
            model.addAttribute("categories", categories);
            return new ModelAndView("categories");
        }

//        @GetMapping("/{categoryId}")
//        public ModelAndView getFoodsByCategory(@PathVariable Long categoryId, Model model) {
//            Category category = categoryService.(categoryId);
//            List<Food> foods = foodService.getFoodsByCategory(categoryId);
//            model.addAttribute("category", category);
//            model.addAttribute("foods", foods);
//            return new ModelAndView("foods"); // Thay đổi tên template
//        }
    }

    @GetMapping("/food_details")
    public String viewFoodDetails(@RequestParam("id") Integer id, Model model) {
        Optional<Food> foodOptional = foodService.getFoodById(id);
        if (foodOptional.isPresent()) {
            model.addAttribute("food", foodOptional.get());
            return "food_details";
        } else {
            // Handle case when food is not found, e.g., redirect to an error page or show a message
            return "error";
        }
    }
}
