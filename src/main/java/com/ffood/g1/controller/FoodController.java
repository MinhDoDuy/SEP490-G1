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
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

public class FoodController {
    @Controller
    @RequestMapping("/categories")
    public class CategoryController {

        @Autowired
        private CategoryService categoryService;

        @Autowired
        private FoodService foodService;

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
}
