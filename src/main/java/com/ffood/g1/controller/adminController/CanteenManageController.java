package com.ffood.g1.controller.adminController;

import com.ffood.g1.entity.Canteen;

import com.ffood.g1.service.CanteenService;
import com.ffood.g1.service.RoleService;
import com.ffood.g1.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@Controller
public class CanteenManageController {
    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private CanteenService canteenService;


    @GetMapping("/manage-canteen")
    public String listCanteens(Model model,
                               @RequestParam(value = "page", defaultValue = "0") int page,
                               @RequestParam(value = "size", defaultValue = "10") int size) {
        Page<Canteen> canteenPage = canteenService.getAllCanteensPage(page, size);
        model.addAttribute("canteenPage", canteenPage);
        return "./admin-management/manage-canteen";
    }

    @GetMapping("/search-canteen")
    public String searchCanteens(Model model,
                                 @RequestParam(value = "page", defaultValue = "0") int page,
                                 @RequestParam(value = "size", defaultValue = "10") int size,
                                 @RequestParam(value = "keyword", required = false) String keyword) {
        Page<Canteen> canteenPage;

        if (keyword == null || keyword.isEmpty()) {
            canteenPage = canteenService.getAllCanteensPage(page, size);
        } else {
            canteenPage = canteenService.searchCanteens(keyword, page, size);
        }
        model.addAttribute("canteenPage", canteenPage);
        model.addAttribute("keyword", keyword);

        return "./admin-management/manage-canteen";
    }


    @GetMapping("/add-canteen")
    public String showAddCanteenForm(Model model) {
        model.addAttribute("canteen", new Canteen());
        model.addAttribute("managers", userService.getAllManagers());
        return "./admin-management/add-canteen";
    }

    @PostMapping("/add-canteen")
    public String addCanteen(@ModelAttribute("canteen") Canteen canteen, Model model) {
        canteenService.saveCanteen(canteen);
        return "redirect:/manage-canteen";
    }


    @GetMapping("/delete-canteen/{canteenId}")
    public String deleteCanteen(@PathVariable Integer canteenId) {
        canteenService.deleteCanteenById(canteenId);
        return "redirect:/manage-canteen";
    }



}
