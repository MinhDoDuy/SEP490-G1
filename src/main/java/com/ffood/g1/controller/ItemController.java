package com.ffood.g1.controller;

import com.ffood.g1.entity.Item;
import com.ffood.g1.repository.ItemRepository;
import com.ffood.g1.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ItemController {

    @Autowired

    private ItemRepository itemRepository;

    @Autowired
    private ItemService itemService;

    @GetMapping("/items_in_all_shop")
    public String getItems(@RequestParam(defaultValue = "0") int page, Model model) {
        Pageable pageable = PageRequest.of(page, 12);  // 12 items per page (3 rows x 4 items)
        Page<Item> items = itemService.getAllItems(pageable);
        model.addAttribute("items_home", items.getContent());
        model.addAttribute("totalPages", items.getTotalPages());
        model.addAttribute("currentPage", page);
        return "canteens";


    }
}
