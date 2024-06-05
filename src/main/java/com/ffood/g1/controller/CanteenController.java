package com.ffood.g1.controller;

import com.ffood.g1.entity.Category;
import com.ffood.g1.entity.Food;
import com.ffood.g1.repository.FoodRepository;
import com.ffood.g1.service.CategoryService;
import com.ffood.g1.service.FoodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class CanteenController {

    @Autowired

    private FoodRepository foodRepository;

    @Autowired
    private FoodService foodService;

    @Autowired
    private CategoryService categoryService;


//    @GetMapping("/canteen_details")
//    public String getItems(@RequestParam(defaultValue = "0") int page, Model model) {
//
//        //get all food in canteens
//        Pageable pageable = PageRequest.of(page, 12);  // 12 items per page (3 rows x 4 items)
//        Page<Food> items = foodService.getAllFood(pageable);
//        model.addAttribute("items_home", items.getContent());
//        model.addAttribute("totalPages", items.getTotalPages());
//        model.addAttribute("currentPage", page);
//
//        //get all category
//        List<Category> categories = categoryService.getAllCategories();
//        model.addAttribute("categories", categories);
//
//        return "/canteens";
//
//    }

    @GetMapping("/canteen_details")
    public String viewCanteens(@RequestParam(value = "page", defaultValue = "0") int page,
                               @RequestParam(value = "categoryId", required = false) Integer categoryId,
                               @RequestParam(value = "name", required = false) String name,
                               Model model) {
        Pageable pageable = PageRequest.of(page, 12);  // 12 items per page (3 rows x 4 items)
        Page<Food> items;

        if (categoryId != null && name != null) {
            items = foodService.getFoodByCategoryAndName(categoryId, name, pageable);
        } else if (categoryId != null) {
            items = foodService.getFoodByCategory(categoryId, pageable);
        } else if (name != null) {
            items = foodService.getFoodByName(name, pageable);
        } else {
            items = foodService.getAllFood(pageable);
        }
        model.addAttribute("categoryId", categoryId);  // Add this line
        model.addAttribute("name", name);  // Add this line

        model.addAttribute("items_home", items.getContent());
        model.addAttribute("totalPages", items.getTotalPages());
        model.addAttribute("currentPage", page);

        // Get all categories
        List<Category> categories = categoryService.getAllCategories();
        model.addAttribute("categories", categories);


        return "canteens";
    }



    @GetMapping("/food_details")
    public String getFoodDetail( Model model) {
        List<Food> food_details = foodService.getRandomFood();
        model.addAttribute("food_details", food_details);
        return "/food_details";

    }


}
