package com.ffood.g1.controller.manageStaff;

import com.ffood.g1.entity.*;
import com.ffood.g1.service.CanteenService;
import com.ffood.g1.service.CategoryService;
import com.ffood.g1.service.FoodService;
import com.ffood.g1.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller

public class FoodManagementController {

    @Autowired
    private UserService userService;

    @Autowired
    private FoodService foodService;

    @Autowired
    private CanteenService canteenService;

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/manage-food")
    public String manageFood(@RequestParam("canteenId") Integer canteenId, Model model, Principal principal) {
        // Lấy thông tin người dùng hiện tại
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail = "";

        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            currentUserEmail = userDetails.getUsername();
        } else {
            currentUserEmail = authentication.getName(); // hoặc các cách khác để lấy email
        }

        User currentUser = userService.findByEmail(currentUserEmail);

        // Kiểm tra xem người dùng có vai trò là quản lý (ROLE_MANAGER) và quản lý đúng canteen không
        if (currentUser.getRole().getRoleName().equals("ROLE_MANAGER") && currentUser.getCanteen().getCanteenId().equals(canteenId)) {
            // Lấy danh sách thức ăn trong canteen mà người dùng quản lý
            List<Food> foods = foodService.findByCanteenId(canteenId);
            model.addAttribute("foods", foods);

            // Lấy tên canteen
            Canteen canteen = canteenService.getCanteenById(canteenId);
            model.addAttribute("canteenName", canteen.getCanteenName());
            model.addAttribute("canteenId", canteenId);
            return "./staff-management/manage-food";
        } else {
            // Nếu không phải quản lý hoặc quản lý sai canteen, trả về trang lỗi hoặc thông báo không có quyền
            return "error/403";
        }
    }

    @GetMapping("/edit-food/{foodId}")
    public String showEditFoodForm(@PathVariable("foodId") Integer foodId, @RequestParam("canteenId") Integer canteenId, Model model) {
        Optional<Food> foodOptional = foodService.getFoodById(foodId);
        if (foodOptional.isPresent()) {
            Food food = foodOptional.get();
            List<Category> categories = categoryService.getAllCategories();
            Canteen canteen = canteenService.getCanteenById(canteenId);

            model.addAttribute("food", food);
            model.addAttribute("categories", categories);
            model.addAttribute("canteenId", canteenId);
            model.addAttribute("canteenName", canteen.getCanteenName());

            return "./staff-management/edit-food";
        } else {
            // Nếu không tìm thấy thức ăn, trả về trang lỗi hoặc thông báo không tìm thấy
            return "error/404";
        }
    }

    @PostMapping("/edit-food")
    public String updateFood(@ModelAttribute("food") Food food,
                             @RequestParam("canteenId") Integer canteenId,
                             @RequestParam("imageFood") MultipartFile imageFood,
                             @RequestParam("imageFoodDetail1") MultipartFile imageFoodDetail1,
                             @RequestParam("imageFoodDetail2") MultipartFile imageFoodDetail2) {

        if (!imageFood.isEmpty()) {
            String imagePath = saveImage(imageFood);
            food.setImageFood(imagePath);
        }
        if (!imageFoodDetail1.isEmpty()) {
            String imagePath = saveImage(imageFoodDetail1);
            food.setImageFoodDetail1(imagePath);
        }
        if (!imageFoodDetail2.isEmpty()) {
            String imagePath = saveImage(imageFoodDetail2);
            food.setImageFoodDetail2(imagePath);
        }
        foodService.updateFood(food);
        return "redirect:/manage-food?canteenId=" + canteenId;
    }

    private String saveImage(MultipartFile file) {
        // Logic để lưu tệp hình ảnh vào đĩa và trả về đường dẫn
        // Ví dụ, lưu vào một thư mục và trả về đường dẫn tương đối
        // Thực hiện logic lưu tệp tại đây và trả về đường dẫn tệp đã lưu
        // Bạn có thể sử dụng các thư viện như Apache Commons IO hoặc Spring's FileCopyUtils
        // Đây là một ví dụ cơ bản:
        try {
            // Đường dẫn đến thư mục lưu trữ hình ảnh
            String uploadDir = "path/to/save/images/";
            String fileName = file.getOriginalFilename();
            File dest = new File(uploadDir + fileName);
            file.transferTo(dest);
            return "/images/" + fileName; // Trả về đường dẫn tương đối
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @GetMapping("/add-food")
    public String showAddFoodForm(@RequestParam("canteenId") Integer canteenId, Model model) {
        Food food = new Food();
        List<Category> categories = categoryService.getAllCategories();
        Canteen canteen = canteenService.getCanteenById(canteenId);

        model.addAttribute("food", food);
        model.addAttribute("categories", categories);
        model.addAttribute("canteenId", canteenId);
        model.addAttribute("canteenName", canteen.getCanteenName());

        return "./staff-management/add-food";
    }

    @PostMapping("/add-food")
    public String addFood(@ModelAttribute("food") Food food, @RequestParam("canteenId") Integer canteenId) {
        Canteen canteen = canteenService.getCanteenById(canteenId);
        food.setCanteen(canteen);
        foodService.addFood(food);
        return "redirect:/manage-food?canteenId=" + canteenId;
    }

}