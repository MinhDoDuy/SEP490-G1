package com.ffood.g1.controller.user;

import com.ffood.g1.entity.Canteen;
import com.ffood.g1.service.CanteenService;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CanteenController {
    @Autowired
    private CanteenService canteenService;

    @GetMapping("/canteens")
    public String getCanteens(Model model) {
        List<Canteen> canteens = canteenService.getAllCanteens();
        model.addAttribute("canteens", canteens);
        return "/homepage";
    }

}

