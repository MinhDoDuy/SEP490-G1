package com.ffood.g1.controller.manageStaff;

import com.ffood.g1.entity.Canteen;
import com.ffood.g1.entity.Category;
import com.ffood.g1.entity.Food;
import com.ffood.g1.entity.User;
import com.ffood.g1.exception.SpringBootFileUploadException;
import com.ffood.g1.service.*;
import com.ffood.g1.utils.PhoneUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
                             @RequestParam(value = "page", defaultValue = "0") int page,
                             @RequestParam(value = "size", defaultValue = "10") int size,
                             Model model, Principal principal, RedirectAttributes redirectAttributes) {

        // Lấy thông tin người dùng hiện tại từ Principal
        User currentUser = userService.findByEmail(principal.getName());

        // Kiểm tra xem `canteenId` có khớp với `canteenId` của người dùng hiện tại
        if (!currentUser.getCanteen().getCanteenId().equals(canteenId)) {
            redirectAttributes.addFlashAttribute("error", "Bạn không có quyền truy cập quản lý món ăn của canteen khác.");
            return "redirect:/manage-food?canteenId=" + currentUser.getCanteen().getCanteenId(); // Chuyển hướng về danh sách món ăn của canteen mà người dùng thuộc về
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<Food> foodPage = foodService.findFoodByCanteenId(canteenId, pageable);

        model.addAttribute("foodPage", foodPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", foodPage.getTotalPages());
        model.addAttribute("canteenId", canteenId);
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("canteenName", canteenService.getCanteenById(canteenId).getCanteenName());

        return "staff-management/manage-food";
    }



    @GetMapping("/search-food")
    public String searchFood(@RequestParam("canteenId") Integer canteenId,
                             @RequestParam(value = "keyword", required = false) String keyword,
                             @RequestParam(value = "categoryId", required = false) Integer categoryId,
                             @RequestParam(value = "page", defaultValue = "0") int page,
                             @RequestParam(value = "size", defaultValue = "10") int size,
                             Model model) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Food> foodPage = foodService.searchFoods(keyword, categoryId, canteenId, pageable);

        model.addAttribute("foodPage", foodPage);

        Canteen canteen = canteenService.getCanteenById(canteenId);
        model.addAttribute("canteenName", canteen.getCanteenName());
        model.addAttribute("canteenId", canteenId);
        model.addAttribute("categories", categoryService.getAllCategories());

        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedCategoryId", categoryId);

        return "staff-management/manage-food";
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
                          @RequestParam("imageFood") MultipartFile imageFood, RedirectAttributes redirectAttributes) {
        boolean hasErrors = false;

        if (food.getFoodName().trim().isEmpty() || food.getFoodName().trim().startsWith(" ")) {
            result.rejectValue("foodName", "error.food", "Tên Đồ Ăn không được để trống hoặc không được bắt đầu ô trống");
            hasErrors = true;
        }

        if (food.getDescription().trim().isEmpty() || food.getDescription().trim().startsWith(" ")) {
            result.rejectValue("description", "error.food", "Mô tả không được bắt đầu bằng dấu cách");
            hasErrors = true;
        }

        if (hasErrors) {
            model.addAttribute("canteenId", canteenId);
            model.addAttribute("categories", categoryService.getAllCategories());
            return "staff-management/add-food";
        }

        try {
            Canteen canteen = canteenService.getCanteenById(canteenId);
            food.setCanteen(canteen);

            if (food.getFoodQuantity() == null) {
                food.setFoodQuantity(0);
            }

            if (!imageFood.isEmpty()) {
                String foodImageUrl = fileS3Service.uploadFile(imageFood);
                food.setImageFood(foodImageUrl);
            }
            food.setSalesCount(0);
            foodService.save(food);
            redirectAttributes.addFlashAttribute("successMessage", "Đồ Ăn thêm thành công vào căn tin");
            return "redirect:/manage-food?canteenId=" + canteenId;
        } catch (SpringBootFileUploadException | IOException e) {
            model.addAttribute("message", "Error: " + e.getMessage());
            model.addAttribute("messageType", "error");
            model.addAttribute("canteenId", canteenId);
            model.addAttribute("categories", categoryService.getAllCategories());
            return "staff-management/add-food";
        }
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
                           @RequestParam("imageFood") MultipartFile imageFood, RedirectAttributes redirectAttributes) {
        Optional<Food> existingFood = foodService.getFoodById(food.getFoodId());

        boolean hasErrors = false;

        if (food.getFoodName().trim().isEmpty() || food.getFoodName().trim().startsWith(" ")) {
            result.rejectValue("foodName", "error.food", "Tên Đồ Ăn không được để trống hoặc không được bắt đầu ô trống.");
            hasErrors = true;
        }

        if (food.getDescription().trim().isEmpty() || food.getDescription().trim().startsWith(" ")) {
            result.rejectValue("description", "error.food", "Mô tả không được bắt đầu bằng dấu cách.");
            hasErrors = true;
        }

        if (hasErrors) {
            model.addAttribute("canteenId", canteenId);
            model.addAttribute("categories", categoryService.getAllCategories());
            model.addAttribute("foodImage", existingFood.isPresent() ? existingFood.get().getImageFood() : null);
            return "staff-management/edit-food";
        }

        try {
            Canteen canteen = canteenService.getCanteenById(canteenId);
            food.setCanteen(canteen);

            if (food.getFoodQuantity() == null) {
                food.setFoodQuantity(0);
            }

            if (!imageFood.isEmpty()) {
                String foodImageUrl = fileS3Service.uploadFile(imageFood);
                food.setImageFood(foodImageUrl);
            } else {
                food.setImageFood(existingFood.isPresent() ? existingFood.get().getImageFood() : null);
            }

            foodService.save(food);
            redirectAttributes.addFlashAttribute("successMessage", "Món ăn đã được cập nhật thành công!");
        } catch (SpringBootFileUploadException | IOException e) {
            model.addAttribute("canteenId", canteenId);
            model.addAttribute("categories", categoryService.getAllCategories());
            model.addAttribute("foodImage", existingFood.isPresent() ? existingFood.get().getImageFood() : null);
            return "staff-management/edit-food";
        }
        return "redirect:/manage-food?canteenId=" + canteenId;
    }



    @GetMapping("/manage-category")
    public String manageCategory(Model model,
                                 @RequestParam("canteenId") Integer canteenId,
                                 @RequestParam(value = "success", required = false) String success,
                                 @RequestParam(value = "error", required = false) String error) {
        List<Category> categories = categoryService.getAllCategories();
        model.addAttribute("categories", categories);
        model.addAttribute("canteenId", canteenId);

        // Get canteen name
        Canteen canteen = canteenService.getCanteenById(canteenId);
        model.addAttribute("canteenName", canteen.getCanteenName());
        model.addAttribute("canteenId", canteenId);

        return "./staff-management/manage-category";
    }

    @GetMapping("/add-quantity/{foodId}")
    public String showAddQuantityForm(@PathVariable("foodId") Integer foodId, Model model) {
        Optional<Food> optionalFood = foodService.getFoodById(foodId);

        if (optionalFood.isPresent()) {
            Food food = optionalFood.get();
            model.addAttribute("food", food);
            model.addAttribute("canteenId", food.getCanteen().getCanteenId());
            return "staff-management/add-quantity";
        } else {
            return "redirect:/manage-food?error=Food not found";
        }
    }

    @PostMapping("/add-quantity")
    public String addQuantity(@ModelAttribute("food") Food food, BindingResult result, Model model,
                              @RequestParam("canteenId") Integer canteenId,
                              @RequestParam("newQuantity") Integer newQuantity, RedirectAttributes redirectAttributes) {
        Optional<Food> existingFood = foodService.getFoodById(food.getFoodId());

        if (existingFood.isPresent()) {
            Food updatedFood = existingFood.get();
            updatedFood.setFoodQuantity(updatedFood.getFoodQuantity() + newQuantity);
            foodService.save(updatedFood);
            redirectAttributes.addFlashAttribute("successMessage", "Số lượng đã được cập nhật thành công!");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Food not found!");
        }

        return "redirect:/manage-food?canteenId=" + canteenId;
    }

}
