package com.ffood.g1.controller;

import com.ffood.g1.entity.Canteen;
import com.ffood.g1.entity.Item;
import com.ffood.g1.repository.ItemRepository;
import com.ffood.g1.service.CanteenService;
import com.ffood.g1.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private CanteenService canteenService;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private ItemService itemService;

    @GetMapping("/homepage")
    public String getCanteens(Model model) {


        //get All canteens and display in homepage
        List<Canteen> canteens = canteenService.getAllCanteens();
        model.addAttribute("canteens", canteens);
        // get random 12 items and display in homepage
        List<Item> items_home = itemService.getRandomItems();
        model.addAttribute("items_home", items_home);


        return "/homepage";
    }
    @GetMapping("/canteen_contact")
    public String getCanteenContact(Model model) {
        //get All canteens and display in homepage
        List<Canteen> canteens = canteenService.getAllCanteens();
        model.addAttribute("canteens", canteens);
        return "/canteen_contact";
    }




}
