package com.ffood.g1.controller.manageStaff;

import com.ffood.g1.entity.Canteen;
import com.ffood.g1.entity.Category;
import com.ffood.g1.entity.Food;
import com.ffood.g1.entity.User;
import com.ffood.g1.exception.SpringBootFileUploadException;
import com.ffood.g1.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Objects;
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

    @Autowired
    private FileS3Service fileS3Service;

    @GetMapping("/manage-food")
    public String manageFood(@RequestParam("canteenId") Integer canteenId,
                             @RequestParam(value = "success", required = false) String success,
                             @RequestParam(value = "error", required = false) String error,
                             Model model, Principal principal) {
        // Get current user details
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail = "";

        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            currentUserEmail = userDetails.getUsername();
        }

        User currentUser = userService.findByEmail(currentUserEmail);

        // Check if the user is a manager and manages the correct canteen
        if (currentUser.getRole().getRoleName().equals("ROLE_MANAGER") && currentUser.getCanteen().getCanteenId().equals(canteenId)) {
            // Get the list of foods for the canteen managed by the user
            List<Food> foods = foodService.findByCanteenId(canteenId);
            model.addAttribute("foods", foods);

            // Get canteen name
            Canteen canteen = canteenService.getCanteenById(canteenId);
            model.addAttribute("canteenName", canteen.getCanteenName());
            model.addAttribute("canteenId", canteenId);

            if ("add".equals(success)) {
                model.addAttribute("successMessage", "Đồ Ăn Đã Được Thêm Thành Công!");
            } else if ("edit".equals(success)) {
                model.addAttribute("successMessage", "Đồ Ăn Đã Được Sửa Thành Công!");
            }

            if (error != null) {
                model.addAttribute("errorMessage", error);
            }

            return "./staff-management/manage-food";
        } else {
            // If not a manager or managing the wrong canteen, return error page or unauthorized message
            return "error/403";
        }
    }

    @GetMapping("/add-food-form")
    public String showAddFoodForm(@RequestParam("canteenId") Integer canteenId, Model model) {
        model.addAttribute("food", new Food());
        model.addAttribute("canteenId", canteenId);
        model.addAttribute("categories", categoryService.getAllCategories());
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (Objects.nonNull(authentication) && authentication.isAuthenticated()) {
            String email = authentication.getName();
            User user = userService.findByEmail(email);
            if (Objects.nonNull(user)) {
                model.addAttribute("user", user);
            }
        }
        return "staff-management/add-food";
    }

    @PostMapping("/add-food")
    public String addFood(@ModelAttribute("food") Food food, BindingResult result, Model model,
                          @RequestParam("canteenId") Integer canteenId,
                          @RequestParam("imageFood") MultipartFile imageFood) {
        try {
            if (food.getFoodName().trim().isEmpty() || food.getFoodName().trim().startsWith(" ")) {
                result.rejectValue("foodName", "error.food", "Food name cannot be empty or start with a space.");
                model.addAttribute("message", "Tên món ăn không được để trống hoặc bắt đầu bằng khoảng cách.");
                model.addAttribute("messageType", "error");
                return "staff-management/add-food";
            }

            if (food.getDescription().trim().startsWith(" ")) {
                result.rejectValue("description", "error.food", "Description cannot start with a space.");
                model.addAttribute("message", "Mô tả không được bắt đầu bằng dấu cách.");
                model.addAttribute("messageType", "error");
                return "staff-management/add-food";
            }

            Canteen canteen = canteenService.getCanteenById(canteenId);
            food.setCanteen(canteen);

            if (!imageFood.isEmpty()) {
                String foodImageUrl = fileS3Service.uploadFile(imageFood);
                food.setImageFood(foodImageUrl);
            }
            foodService.save(food);
            model.addAttribute("message", "Food added successfully!");
            model.addAttribute("messageType", "success");
        } catch (SpringBootFileUploadException | IOException e) {
            model.addAttribute("message", "Error: " + e.getMessage());
            model.addAttribute("messageType", "error");
            return "staff-management/add-food";
        }
        return "redirect:/manage-food?canteenId=" + canteenId + "&success=add";
    }



    @GetMapping("/edit-food/{foodId}")
    public String showEditFoodForm(@PathVariable("foodId") Integer foodId, Model model) {
        Optional<Food> optionalFood = foodService.getFoodById(foodId);

        if (optionalFood.isPresent()) {
            Food food = optionalFood.get();
            model.addAttribute("food", food);
            model.addAttribute("canteenId", food.getCanteen().getCanteenId());
            model.addAttribute("categories", categoryService.getAllCategories());
            return "staff-management/edit-food";
        } else {
            return "redirect:/manage-food?error=Food not found";
        }
    }

    @PostMapping("/edit-food")
    public String editFood(@ModelAttribute("food") Food food, BindingResult result, Model model,
                           @RequestParam("canteenId") Integer canteenId,
                           @RequestParam("imageFood") MultipartFile imageFood) {
        Optional<Food> existingFood = foodService.getFoodById(food.getFoodId());
        if (food.getFoodName().trim().isEmpty() || food.getFoodName().trim().startsWith(" ")) {
            result.rejectValue("foodName", "error.food", "Food name cannot be empty or start with a space.");
        }

        if (food.getDescription().trim().startsWith(" ")) {
            result.rejectValue("description", "error.food", "Description cannot start with a space.");
        }

        try {
            Canteen canteen = canteenService.getCanteenById(canteenId);
            food.setCanteen(canteen);

            if (!imageFood.isEmpty()) {
                String foodImageUrl = fileS3Service.uploadFile(imageFood);
                food.setImageFood(foodImageUrl);
            }
            // Check if a new image is uploaded
            if (imageFood != null && !imageFood.isEmpty()) {
                String foodImageUrl = fileS3Service.uploadFile(imageFood);
                food.setImageFood(foodImageUrl);
            } else {
                // Retain the existing image if no new image is uploaded
                food.setImageFood(existingFood.get().getImageFood());
            }

            foodService.save(food);
        } catch (SpringBootFileUploadException | IOException e) {
            return "redirect:/edit-food-form?canteenId=" + canteenId + "&error=" + e.getMessage();
        }
        return "redirect:/manage-food?canteenId=" + canteenId + "&success=edit";
    }


    @GetMapping("/manage-category")
    public String manageCategory(Model model,
                                 @RequestParam("canteenId") Integer canteenId,
                                 @RequestParam(value = "success", required = false) String success,
                                 @RequestParam(value = "error", required = false) String error) {
        List<Category> categories = categoryService.getAllCategories();
        model.addAttribute("categories", categories);
        model.addAttribute("canteenId", canteenId);

        if ("add".equals(success)) {
            model.addAttribute("successMessage", "Sản Phẩm Đã Được Thêm Thành Công!");
        } else if ("edit".equals(success)) {
            model.addAttribute("successMessage", "Sản Phẩm Đã Được Sửa Thành Công!");
        } else if ("delete".equals(success)) {
            model.addAttribute("successMessage", "Sản Phẩm Đã Được Xóa Thành Công!");
        }

        if (error != null) {
            model.addAttribute("errorMessage", error);
        }

        return "./staff-management/manage-category";
    }

    @GetMapping("/add-category-form")
    public String showAddCategoryForm(@RequestParam("canteenId") Integer canteenId, Model model) {
        model.addAttribute("category", new Category());
        model.addAttribute("canteenId", canteenId);
        return "./staff-management/add-category";
    }

    @PostMapping("/add-category")
    public String addCategory(@Valid @ModelAttribute("category") Category category,
                              @RequestParam("canteenId") Integer canteenId, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("canteenId", canteenId);
            return "./staff-management/add-category";
        }

        // Kiểm tra xem tên danh mục có rỗng hoặc bắt đầu bằng dấu cách không
        if (category.getCategoryName().trim().isEmpty()) {
            result.rejectValue("categoryName", "error.category", "Category name cannot start with a space");
            model.addAttribute("canteenId", canteenId);
            return "./staff-management/add-category";
        }

        // Kiểm tra xem tên danh mục đã tồn tại hay chưa
        if (categoryService.existsByCategoryName(category.getCategoryName())) {
            result.rejectValue("categoryName", "error.category", "Category name already exists");
            model.addAttribute("canteenId", canteenId);
            return "./staff-management/add-category";
        }

        categoryService.saveCategory(category);
        return "redirect:/manage-category?canteenId=" + canteenId + "&success=add";
    }




    @GetMapping("/edit-category/{categoryId}")
    public String showEditCategoryForm(@PathVariable("categoryId") Integer categoryId,
                                       @RequestParam("canteenId") Integer canteenId, Model model) {
        Category category = categoryService.getCategoryById(categoryId);
        model.addAttribute("category", category);
        model.addAttribute("canteenId", canteenId);
        return "./staff-management/edit-category";
    }

    @PostMapping("/edit-category")
    public String editCategory(@ModelAttribute("category") Category category, BindingResult result,
                               @RequestParam("canteenId") Integer canteenId, Model model) {
        categoryService.saveCategory(category);
        return "redirect:/manage-category?canteenId=" + canteenId + "&success=edit";
    }

    @GetMapping("/delete-category/{categoryId}")
    public String deleteCategory(@PathVariable("categoryId") Integer categoryId,
                                 @RequestParam("canteenId") Integer canteenId, Model model) {
        try {
            categoryService.deleteCategoryById(categoryId);
            return "redirect:/manage-category?canteenId=" + canteenId + "&success=delete";
        } catch (Exception e) {
            return "redirect:/manage-category?canteenId=" + canteenId + "&error=Unable to delete category";
        }
    }

}
