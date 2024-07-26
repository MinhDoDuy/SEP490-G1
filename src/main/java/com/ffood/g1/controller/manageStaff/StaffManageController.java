package com.ffood.g1.controller.manageStaff;

import com.ffood.g1.entity.Canteen;
import com.ffood.g1.entity.Role;
import com.ffood.g1.entity.User;
import com.ffood.g1.service.CanteenService;
import com.ffood.g1.service.RoleService;
import com.ffood.g1.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.Console;
import java.util.List;

@Controller
public class StaffManageController {


    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private CanteenService canteenService;

    //--------------------------------------------STAFF MANAGEMENT--------------------------------------------

    @GetMapping("/manage-staff")
    public String listStaff(Model model,
                            @RequestParam(value = "page", defaultValue = "0") int page,
                            @RequestParam(value = "size", defaultValue = "10") int size,
                            @RequestParam(value = "canteenId") Integer canteenId,
                            @RequestParam(value = "success", required = false) String success) {
        page = Math.max(page, 0);
        Page<User> staffPage = userService.getAllStaff(page, size, canteenId);
        Canteen canteen = canteenService.getCanteenById(canteenId); // Retrieve the canteen object
        model.addAttribute("staffPage", staffPage);
        model.addAttribute("canteenId", canteenId);
        model.addAttribute("canteen", canteen); // Add canteen to the model

        // Get canteen name
        model.addAttribute("canteenName", canteen.getCanteenName());
        model.addAttribute("canteenId", canteenId);

        if ("add".equals(success)) {
            model.addAttribute("successMessage", "Nhân viên đã được thêm thành công!");
        } else if ("edit".equals(success)) {
            model.addAttribute("successMessage", "Nhân viên đã được chỉnh sửa thành công!");
        }
        return "staff-management/manage-staff";
    }
    @GetMapping("/search-staff")
    public String searchStaff(Model model,
                              @RequestParam(value = "page", defaultValue = "0") int page,
                              @RequestParam(value = "size", defaultValue = "10") int size,
                              @RequestParam(value = "keyword", required = false) String keyword,
                              @RequestParam(value = "canteenId") Integer canteenId) {
        Page<User> staffPage;
        if (keyword == null || keyword.isEmpty()) {
            staffPage = userService.getStaffUsers(page, size);
        } else {
            staffPage = userService.searchStaff(keyword, page, size);
        }
        Canteen canteen = canteenService.getCanteenById(canteenId); // Retrieve the canteen object

        model.addAttribute("staffPage", staffPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("canteenId", canteenId);
        model.addAttribute("canteen", canteen); // Add canteen to the model

        return "staff-management/manage-staff";
    }



    @GetMapping("/edit-staff/{userId}")
    public String editStaffForm(@PathVariable Integer userId, Model model,
                                @RequestParam(value = "canteenId") Integer canteenId) {
        User user = userService.getUserById(userId);
        List<Role> roles = roleService.getAllRoles(); // Assume you have a method to get all roles
        Canteen canteen = canteenService.getCanteenById(canteenId); // Retrieve the canteen object

        model.addAttribute("userId", userId);
        model.addAttribute("canteenId", canteenId);
        model.addAttribute("user", user);
        model.addAttribute("roles", roles);
        model.addAttribute("canteen", canteen); // Add canteen to the model
        return "staff-management/edit-staff";
    }


    @PostMapping("/edit-staff")
    public String updateStaffRole(@RequestParam Integer userId,
                                  @RequestParam("isActive") Boolean isActive,
                                  @RequestParam(value = "canteenId") Integer canteenId,
                                  RedirectAttributes redirectAttributes) {
        User user = userService.getUserById(userId);
        user.setIsActive(isActive);  // Cập nhật trạng thái hoạt động của người dùng
        userService.saveUser(user);  // Lưu thay đổi

        redirectAttributes.addFlashAttribute("successMessage", "Nhân viên  đã được cập nhật thành công!");
        return "redirect:/manage-staff?canteenId=" + canteenId;
    }


    @GetMapping("/add-staff-form")
    public String showAddStaffForm(Model model, @RequestParam(value = "canteenId") Integer canteenId) {
        User user = new User();
        Role staffRole = roleService.getRoleByName("ROLE_STAFF");
        user.setRole(staffRole);
        Canteen canteen = canteenService.getCanteenById(canteenId);

        user.setCanteen(canteen);

        model.addAttribute("user", user);
        model.addAttribute("canteenId", canteenId);
        model.addAttribute("canteenName", canteen.getCanteenName());
        return "staff-management/add-staff";
    }

    @PostMapping("/add-staff")
    public String addStaff(@ModelAttribute("user") User user,
                           @RequestParam("canteenId") Integer canteenId) {
        Canteen canteen = canteenService.getCanteenById(canteenId);
        user.setCanteen(canteen);
        userService.saveUser(user);
        return "redirect:/manage-staff?canteenId=" + canteenId + "&success=add";
    }

    @GetMapping("/assign-staff-form")
    public String showAssignStaffForm(@RequestParam("canteenId") Integer canteenId, Model model) {
        model.addAttribute("canteenId", canteenId);
        return "staff-management/assign-staff";
    }

    @PostMapping("/check-email")
    public String checkEmail(@RequestParam("email") String email,
                             @RequestParam("canteenId") Integer canteenId,
                             HttpServletRequest request,
                             RedirectAttributes redirectAttributes) {
        try {
            userService.sendAssignStaffEmail(email, request, canteenId);
            redirectAttributes.addFlashAttribute("successMessage", "Token đã được gửi tới email!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/assign-staff-form?canteenId=" + canteenId;
    }

    @GetMapping("/assign-confirm")
    public String confirmAssign(@RequestParam("token") String token,
                                @RequestParam("canteenId") Integer canteenId,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        try {
            userService.confirmAssignStaff(token, canteenId);
            Canteen canteen = canteenService.getCanteenById(canteenId);
            model.addAttribute("canteenName", canteen.getCanteenName());
            return "staff-management/assign-success";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/manage-staff?canteenId=" + canteenId;
        }
    }

}