package com.ffood.g1.controller;

import com.ffood.g1.entity.Canteen;
import com.ffood.g1.entity.Food;
import com.ffood.g1.repository.FoodRepository;
import com.ffood.g1.service.CanteenService;
import com.ffood.g1.service.FoodService;
import org.springframework.beans.factory.annotation.Autowired;
import com.ffood.g1.entity.User;
import com.ffood.g1.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

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

    @GetMapping({"/homepage", ""})
    public String getCanteens(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (Objects.nonNull(authentication) && authentication.isAuthenticated()) {
            String email = authentication.getName();
            User user = userService.findByEmail(email);
            if (Objects.nonNull(user)) {
                model.addAttribute("user", user);
            }
        }
        List<Canteen> canteens = canteenService.getAllCanteens();
        model.addAttribute("canteens", canteens);

        List<Food> items_home = foodService.getRandomFood();
        model.addAttribute("items_home", items_home);

        return "homepage";
    }

    @GetMapping("/canteen_contact")
    public String getCanteenContact(Model model) {

        //get All canteens and display in homepage
        List<Canteen> canteens = canteenService.getAllCanteens();
        model.addAttribute("canteens", canteens);

        return "/canteen_contact";
    }





}
