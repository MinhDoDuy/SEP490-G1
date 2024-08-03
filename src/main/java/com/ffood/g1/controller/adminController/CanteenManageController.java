package com.ffood.g1.controller.adminController;

import com.ffood.g1.entity.Canteen;
import com.ffood.g1.exception.SpringBootFileUploadException;
import com.ffood.g1.service.CanteenService;
import com.ffood.g1.service.FileS3Service;
import com.ffood.g1.service.UserService;
import com.ffood.g1.utils.PhoneUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Controller
public class CanteenManageController {

    @Autowired
    private CanteenService canteenService;

    @Autowired
    private FileS3Service fileS3Service;

    @Autowired
    private UserService userService;

    @GetMapping("/manage-canteen")
    public String listCanteens(Model model,
                               @RequestParam(value = "page", defaultValue = "0") int page,
                               @RequestParam(value = "size", defaultValue = "10") int size) {
        Page<Canteen> canteenPage = canteenService.getAllCanteensPage(page, size);
        model.addAttribute("canteenPage", canteenPage);
        return "admin-management/manage-canteen";
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

        return "admin-management/manage-canteen";
    }

    @GetMapping("/add-canteen")
    public String showAddCanteenForm(Model model) {
        model.addAttribute("canteen", new Canteen());
        return "./admin-management/add-canteen";
    }

    @PostMapping("/add-canteen")
    public String addCanteen(@ModelAttribute("canteen") Canteen canteen, Model model,
                             @RequestParam("imageCanteenInput") MultipartFile imageCanteenInput,
                             RedirectAttributes redirectAttributes) throws IOException, SpringBootFileUploadException {
        boolean hasErrors = false;

        // Kiểm tra các trường không được rỗng và không chứa dấu cách không hợp lệ
        if (canteen.getCanteenName() == null || canteen.getCanteenName().trim().isEmpty()) {
            model.addAttribute("canteenNameError", "Tên căng tin không được để trống.");
            hasErrors = true;
        } else if (canteenService.isCanteenNameExist(canteen.getCanteenName().trim())) {
            model.addAttribute("canteenNameError", "Tên căng tin đã tồn tại.");
            hasErrors = true;
        }

        if (canteen.getCanteenPhone() == null || canteen.getCanteenPhone().isEmpty()) {
            model.addAttribute("phoneError", "Số điện thoại không được để trống.");
            hasErrors = true;
        } else if (canteen.getCanteenPhone().contains(" ")) {
            model.addAttribute("phoneError", "Số điện thoại không được chứa dấu cách.");
            hasErrors = true;
        } else {
            String phoneValidationResult = PhoneUtils.validatePhoneNumber(canteen.getCanteenPhone());
            if (phoneValidationResult != null) {
                model.addAttribute("phoneError", phoneValidationResult);
                hasErrors = true;
            } else if (canteenService.isPhoneExist(canteen.getCanteenPhone())) {
                model.addAttribute("phoneError", "Số điện thoại đã tồn tại.");
                hasErrors = true;
            }
        }

        if (canteen.getLocation() == null || canteen.getLocation().trim().isEmpty()) {
            model.addAttribute("locationError", "Địa điểm không được để trống.");
            hasErrors = true;
        }

        if (canteen.getOpeningHours() == null || canteen.getOpeningHours().trim().isEmpty()) {
            model.addAttribute("openingHoursError", "Giờ mở cửa không được để trống.");
            hasErrors = true;
        }

        // Nếu có lỗi, trả về form thêm với các thông báo lỗi
        if (hasErrors) {
            model.addAttribute("canteen", canteen);
            return "admin-management/add-canteen"; // Thay đổi thành tên view form thực tế của bạn
        }

        // Xử lý upload ảnh nếu có
        if (imageCanteenInput != null && !imageCanteenInput.isEmpty()) {
            String canteenImageUrl = fileS3Service.uploadFile(imageCanteenInput);
            canteen.setCanteenImg(canteenImageUrl);
        }

        // Lưu thông tin căng tin
        canteenService.saveCanteen(canteen);
        redirectAttributes.addFlashAttribute("successMessage", "Thêm căng tin thành công");

        return "redirect:/manage-canteen";
    }




    @GetMapping("/edit-canteen/{canteenId}")
    public String editCanteen(@PathVariable Integer canteenId, Model model) {
        Canteen canteen = canteenService.getCanteenById(canteenId);
        model.addAttribute("canteen", canteen);
        return "admin-management/edit-canteen";
    }

    @PostMapping("/edit-canteen")
    public String updateCanteen(@RequestParam("canteenId") Integer canteenId,
                                @ModelAttribute("canteen") Canteen updatedCanteen,
                                BindingResult result,
                                Model model,
                                @RequestParam("imageCanteenInput") MultipartFile imageCanteenInput,
                                RedirectAttributes redirectAttributes) throws IOException, SpringBootFileUploadException {
        boolean hasErrors = false;

        // Lấy thông tin canteen hiện tại từ database
        Canteen existingCanteen = canteenService.getCanteenById(canteenId);

        // Xác thực số điện thoại
        String phoneValidationResult = PhoneUtils.validatePhoneNumber(updatedCanteen.getCanteenPhone());
        if (phoneValidationResult != null) {
            model.addAttribute("phoneError", phoneValidationResult);
            model.addAttribute("existingImage", existingCanteen.getCanteenImg());
            return "admin-management/edit-canteen";
        }

        // Kiểm tra nếu số điện thoại thay đổi và đã tồn tại trong database
        if (!existingCanteen.getCanteenPhone().equals(updatedCanteen.getCanteenPhone())) {
            if (updatedCanteen.getCanteenPhone().contains(" ")) {
                model.addAttribute("phoneError", "Số điện thoại không được chứa dấu cách.");
                hasErrors = true;
            } else if (canteenService.isPhoneExist(updatedCanteen.getCanteenPhone())) {
                model.addAttribute("phoneError", "Số điện thoại đã tồn tại.");
                hasErrors = true;
            }
        }

        // Kiểm tra nếu tên canteen thay đổi và đã tồn tại trong database
        if (!existingCanteen.getCanteenName().equals(updatedCanteen.getCanteenName())) {
            String trimmedCanteenName = updatedCanteen.getCanteenName().trim();
            if (trimmedCanteenName.isEmpty() || canteenService.isCanteenNameExist(trimmedCanteenName)) {
                model.addAttribute("canteenNameError", "Tên căng tin không hợp lệ hoặc đã tồn tại.");
                hasErrors = true;
            }
        }

        // Kiểm tra nếu location thay đổi và không hợp lệ (chỉ chứa khoảng trắng)
        if (!existingCanteen.getLocation().equals(updatedCanteen.getLocation())) {
            String trimmedLocation = updatedCanteen.getLocation().trim();
            if (trimmedLocation.isEmpty()) {
                model.addAttribute("locationError", "Địa điểm căng tin không được để trống hoặc chỉ chứa khoảng trắng.");
                hasErrors = true;
            }
        }

        // Nếu có lỗi, trả về form chỉnh sửa với các thông báo lỗi
        if (hasErrors) {
            model.addAttribute("canteen", updatedCanteen);
            return "admin-management/edit-canteen";
        }

        // Cập nhật thông tin từ updatedCanteen sang existingCanteen
        existingCanteen.setCanteenName(updatedCanteen.getCanteenName());
        existingCanteen.setLocation(updatedCanteen.getLocation());
        existingCanteen.setOpeningDay(updatedCanteen.getOpeningDay());
        existingCanteen.setCanteenPhone(updatedCanteen.getCanteenPhone());
        existingCanteen.setOpeningHours(updatedCanteen.getOpeningHours());
        existingCanteen.setIsActive(updatedCanteen.getIsActive());

        // Xử lý upload ảnh nếu có
        if (imageCanteenInput != null && !imageCanteenInput.isEmpty()) {
            String canteenImageUrl = fileS3Service.uploadFile(imageCanteenInput);
            existingCanteen.setCanteenImg(canteenImageUrl);
        } else {
            existingCanteen.setCanteenImg(existingCanteen.getCanteenImg());
        }

        // Cập nhật thông tin canteen
        canteenService.updateCanteen(existingCanteen);
        redirectAttributes.addFlashAttribute("successMessage", "Cập nhật căng tin thành công");

        return "redirect:/manage-canteen";
    }



    @GetMapping("/assign-manager-form")
    public String showAssignManagerForm(@RequestParam("canteenId") Integer canteenId, Model model) {
        model.addAttribute("canteenId", canteenId);
        return "admin-management/assign-manager-form";
    }

    @PostMapping("/check-email-manager")
    public String checkEmailManager(@RequestParam("email") String email,
                                    @RequestParam("canteenId") Integer canteenId,
                                    HttpServletRequest request,
                                    RedirectAttributes redirectAttributes) {
        try {
            userService.sendAssignManagerEmail(email, request, canteenId);
            redirectAttributes.addFlashAttribute("successMessage", "Lời mời đã được gửi tới email!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/assign-manager-form?canteenId=" + canteenId;
    }

    @GetMapping("/assign-manager-confirm")
    public String confirmAssignManager(@RequestParam("token") String token,
                                       @RequestParam("canteenId") Integer canteenId,
                                       Model model,
                                       RedirectAttributes redirectAttributes) {
        try {
            userService.confirmAssignManager(token, canteenId);
            Canteen canteen = canteenService.getCanteenById(canteenId);
            model.addAttribute("canteenName", canteen.getCanteenName());
            return "admin-management/assign-success";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/manage-canteen?canteenId=" + canteenId;
        }
    }

}