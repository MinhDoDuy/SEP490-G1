package com.ffood.g1.controller;

import com.ffood.g1.entity.Canteen;
import com.ffood.g1.entity.Category;
import com.ffood.g1.entity.Food;
import com.ffood.g1.entity.User;
import com.ffood.g1.repository.CartRepository;
import com.ffood.g1.repository.FoodRepository;
import com.ffood.g1.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Objects;

@Controller
public class HomeController {

    @Autowired
    private CanteenService canteenService;
    @Autowired
    private FoodRepository foodRepository;
    @Autowired
    private FoodService foodService;
    @Autowired
    private UserService userService;
    @Autowired
    private CartService cartService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    CartRepository cartRepository;

    @GetMapping({"/", "/homepage"})
    public String getCanteens(Model model, HttpSession session) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (Objects.nonNull(authentication) && authentication.isAuthenticated()) {
            String email = authentication.getName();
            User user = userService.findByEmail(email);
            if (Objects.nonNull(user)) {
                if (!user.getIsActive()) {
                    model.addAttribute("isBanned", true);
                    return "homepage";
                }
                model.addAttribute("user", user);
                Integer finalTotalQuantity = cartService.getTotalQuantityByUser(user);
                int totalQuantity = finalTotalQuantity != null ? finalTotalQuantity : 0;
                model.addAttribute("totalQuantity", totalQuantity);
            }

        }
        List<Canteen> canteens = canteenService.getAllCanteens();
        model.addAttribute("canteens", canteens);

        List<Food> items_home = foodService.getRandomFood();
        model.addAttribute("items_home", items_home);

        List<Category> categories = categoryService.getAllCategories();
        model.addAttribute("categories", categories);

        return "homepage";
    }

    @GetMapping("/canteen_contact")
    public String getCanteenContact(Model model) {

        //get All canteens and display in homepage
        List<Canteen> canteens = canteenService.getAllCanteens();
        model.addAttribute("canteens", canteens);

        return "canteen_contact";
    }





}
