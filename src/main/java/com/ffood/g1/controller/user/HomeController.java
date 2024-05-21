package com.ffood.g1.controller.user;

import com.ffood.g1.entity.Canteen;
import com.ffood.g1.entity.Item;
import com.ffood.g1.repository.ItemRepository;
import com.ffood.g1.service.CanteenService;
import com.ffood.g1.service.ItemService;
import com.ffood.g1.service.impl.ItemServiceImpl;
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

    @GetMapping("/home")
    public String getCanteens(Model model) {
        List<Canteen> canteens = canteenService.getAllCanteens();
        model.addAttribute("canteens", canteens);

        List<Item> items_home = itemService.getRandomItems();
        model.addAttribute("items_home", items_home);


        return "/homepage";
    }
}
