package com.ffood.g1.controller.manageStaff;

import com.ffood.g1.entity.Canteen;
import com.ffood.g1.entity.Food;
import com.ffood.g1.entity.User;
import com.ffood.g1.service.CanteenService;
import com.ffood.g1.service.FoodService;
import com.ffood.g1.service.UserService;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;

@Controller

public class FoodManagementController {

    @Autowired
    private UserService userService;

    @Autowired
    private FoodService foodService;

    @Autowired
    private CanteenService canteenService;

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
}
