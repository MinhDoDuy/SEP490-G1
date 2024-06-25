package com.ffood.g1.controller;

import com.ffood.g1.entity.Canteen;
import com.ffood.g1.entity.Category;
import com.ffood.g1.entity.Food;
import com.ffood.g1.entity.User;
import com.ffood.g1.repository.FoodRepository;
import com.ffood.g1.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Objects;

@Controller
public class CanteenController {

    @Autowired
    private UserService userService;

    @Autowired

    private FoodRepository foodRepository;

    @Autowired
    private FoodService foodService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CanteenService canteenService;

    @Autowired
    private CartService cartService;
    @GetMapping("/canteen_details")
    public String viewCanteens(@RequestParam(value = "page", defaultValue = "0") int page,
                               @RequestParam(value = "categoryId", required = false) Integer categoryId,
                               @RequestParam(value = "name", required = false) String name,
                               @RequestParam(value = "checkedCategories", required = false) List<Integer> checkedCategories,
                               @RequestParam(value = "checkedCanteens", required = false) List<Integer> checkedCanteens,
                               @RequestParam(value = "sort", required = false) String sort,
                               Model model) {
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
        Pageable pageable = PageRequest.of(page, 9, getSortDirection(sort));
        Page<Food> foods;

        if ((checkedCategories != null && !checkedCategories.isEmpty()) || (checkedCanteens != null && !checkedCanteens.isEmpty()) || name != null) {
            foods = foodService.getFilteredFoods(checkedCategories, checkedCanteens, name, pageable);
        } else {
            foods = foodService.getAllFood(pageable);
        }

        model.addAttribute("categoryId", categoryId);
        model.addAttribute("name", name);
        model.addAttribute("checkedCategories", checkedCategories);
        model.addAttribute("checkedCanteens", checkedCanteens);
        model.addAttribute("sort", sort);

        model.addAttribute("items_home", foods.getContent());
        model.addAttribute("totalPages", foods.getTotalPages());
        model.addAttribute("currentPage", page);

        // Get all categories
        List<Category> categories = categoryService.getAllCategories();
        model.addAttribute("categories", categories);

        // Get all canteens
        List<Canteen> canteens = canteenService.getAllCanteens();
        model.addAttribute("canteens", canteens);

        return "canteens";
    }

    private Sort getSortDirection(String sort) {
        if (sort == null) return Sort.unsorted();
        switch (sort) {
            case "popularity":
                return Sort.by(Sort.Direction.DESC, "saleCounts");
            case "priceDesc":
                return Sort.by(Sort.Direction.DESC, "price");
            case "priceAsc":
                return Sort.by(Sort.Direction.ASC, "price");
            default:
                return Sort.unsorted();
        }
    }
}