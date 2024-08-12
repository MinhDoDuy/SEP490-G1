package com.ffood.g1.controller.manageStaff;

import com.ffood.g1.entity.Canteen;
import com.ffood.g1.entity.Role;
import com.ffood.g1.entity.User;
import com.ffood.g1.service.CanteenService;
import com.ffood.g1.service.OrderService;
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
import java.security.Principal;
import java.util.List;

@Controller
public class StaffManageController {


    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private CanteenService canteenService;

    @Autowired
    private OrderService orderService;

    //--------------------------------------------STAFF MANAGEMENT--------------------------------------------

    @GetMapping("/manage-staff")
    public String listStaff(Model model,
                            @RequestParam(value = "page", defaultValue = "0") int page,
                            @RequestParam(value = "size", defaultValue = "10") int size,
                            @RequestParam(value = "canteenId") Integer canteenId,
                            @RequestParam(value = "success", required = false) String success,
                            Principal principal, RedirectAttributes redirectAttributes) {

        // Lấy thông tin người dùng hiện tại từ Principal
        User currentUser = userService.findByEmail(principal.getName());

        // Kiểm tra xem `canteenId` có khớp với `canteenId` của người dùng hiện tại
        if (!currentUser.getCanteen().getCanteenId().equals(canteenId)) {
            redirectAttributes.addFlashAttribute("error", "Bạn không có quyền truy cập nhân viên của canteen khác.");
            return "redirect:/manage-staff?canteenId=" + currentUser.getCanteen().getCanteenId(); // Chuyển hướng về danh sách nhân viên của canteen mà người dùng thuộc về
        }

        page = Math.max(page, 0);
        Page<User> staffPage = userService.getAllStaff(page, size, canteenId);
        Canteen canteen = canteenService.getCanteenById(canteenId); // Lấy đối tượng canteen

        model.addAttribute("staffPage", staffPage);
        model.addAttribute("canteenId", canteenId);
        model.addAttribute("canteen", canteen); // Thêm đối tượng canteen vào model
        model.addAttribute("canteenName", canteen.getCanteenName());

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
        int roleId = 2; // Assuming 2 is the roleId for staff
        Page<User> staffPage;
        if (keyword == null || keyword.isEmpty()) {
            staffPage = userService.getStaffUsers(page, size, roleId, canteenId);
        } else {
            staffPage = userService.searchStaff(keyword, page, size, roleId, canteenId);
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
        Canteen canteen = canteenService.getCanteenById(canteenId); // Retrieve the canteen object

        model.addAttribute("userId", userId);
        model.addAttribute("canteenId", canteenId);
        model.addAttribute("user", user);
        model.addAttribute("canteen", canteen); // Add canteen to the model
        return "staff-management/edit-staff";
    }

    @PostMapping("/edit-staff")
    public String fireStaff(@RequestParam Integer userId,
                            @RequestParam(value = "canteenId") Integer canteenId,
                            RedirectAttributes redirectAttributes) {
        if (orderService.hasActiveOrders(userId)) { // Here deliveryRoleId is actually userId
            redirectAttributes.addFlashAttribute("errorMessage", "Nhân viên không thể bị đuổi vì đang có đơn hàng đang giao!");
            return "redirect:/manage-staff?canteenId=" + canteenId;
        }

        User user = userService.getUserById(userId);
        user.setRole(roleService.findRoleById(1)); // Set role to customer
        user.setCanteen(null); // Set canteen to null
        userService.saveUser(user); // Save changes

        redirectAttributes.addFlashAttribute("successMessage", "Nhân viên đã được đuổi thành công!");
        return "redirect:/manage-staff?canteenId=" + canteenId;
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
            redirectAttributes.addFlashAttribute("successMessage", "Lời mời đã được gửi tới email!");
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