package com.ffood.g1.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CartController {

    @PostMapping("/add_to_cart")
    public String addToCart(@RequestParam("foodId") Long foodId, @RequestParam("quantity") int quantity, Model model) {

        // Thêm thông tin sản phẩm và số lượng vào model để hiển thị trên trang mới
        model.addAttribute("foodId", foodId);
        model.addAttribute("quantity", quantity);

        return "cart"; // Trả về tên của trang HTML để hiển thị giỏ hàng
    }

    @GetMapping("/cart")
    public String viewCart() {
        return "cart";
    }
}

