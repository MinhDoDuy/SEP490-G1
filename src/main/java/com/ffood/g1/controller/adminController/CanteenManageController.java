package com.ffood.g1.controller.adminController;

import com.ffood.g1.entity.Canteen;
import com.ffood.g1.exception.SpringBootFileUploadException;
import com.ffood.g1.service.CanteenService;
import com.ffood.g1.service.FileS3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

@Controller
public class CanteenManageController {

    @Autowired
    private CanteenService canteenService;

    @Autowired
    private FileS3Service fileS3Service;

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

        // Kiểm tra các trường không được rỗng
        if (canteen.getCanteenName() == null || canteen.getCanteenName().isEmpty()) {
            model.addAttribute("canteenNameError", "Canteen name cannot be empty.");
            hasErrors = true;
        } else if (canteenService.isCanteenNameExist(canteen.getCanteenName())) {
            model.addAttribute("canteenNameError", "Canteen name already exists.");
            hasErrors = true;
        }

        if (canteen.getCanteenPhone() == null || canteen.getCanteenPhone().isEmpty()) {
            model.addAttribute("phoneError", "Phone number cannot be empty.");
            hasErrors = true;
        } else if (canteenService.isPhoneExist(canteen.getCanteenPhone())) {
            model.addAttribute("phoneError", "Phone number already exists.");
            hasErrors = true;
        }

        if (canteen.getLocation() == null || canteen.getLocation().isEmpty()) {
            model.addAttribute("locationError", "Location cannot be empty.");
            hasErrors = true;
        }

        if (canteen.getOpeningHours() == null || canteen.getOpeningHours().isEmpty()) {
            model.addAttribute("openingHoursError", "Opening hours cannot be empty.");
            hasErrors = true;
        }

        if (hasErrors) {
            model.addAttribute("canteen", canteen);
            return "./admin-management/add-canteen"; // Change to your actual form view name
        }

        if (imageCanteenInput != null && !imageCanteenInput.isEmpty()) {
            String canteenImageUrl = fileS3Service.uploadFile(imageCanteenInput);
            canteen.setCanteenImg(canteenImageUrl);
        }

        canteenService.saveCanteen(canteen);
        redirectAttributes.addFlashAttribute("successMessage", "Canteen added successfully");

        return "redirect:/manage-canteen";
    }

    @GetMapping("/edit-canteen/{canteenId}")
    public String editCanteen(@PathVariable Integer canteenId, Model model) {
        Canteen canteen = canteenService.getCanteenById(canteenId);
        model.addAttribute("canteen", canteen);
        return "admin-management/edit-canteen";
    }

    @PostMapping("/edit-canteen")
    public String updateCanteen(@RequestParam("userId") Canteen canteen, BindingResult result, Model model,
                                @RequestParam("imageCanteenInput") MultipartFile imageCanteenInput,
                                RedirectAttributes redirectAttributes) throws IOException, SpringBootFileUploadException {
        boolean hasErrors = false;

        // Lấy thông tin canteen hiện tại từ database
        Canteen existingCanteen = canteenService.getCanteenById(canteen.getCanteenId());

        // Kiểm tra nếu số điện thoại thay đổi và đã tồn tại trong database
        if (!existingCanteen.getCanteenPhone().equals(canteen.getCanteenPhone()) && canteenService.isPhoneExist(canteen.getCanteenPhone())) {
            model.addAttribute("phoneError", "Phone number already exists.");
            hasErrors = true;
        }

        // Kiểm tra nếu tên canteen thay đổi và đã tồn tại trong database
        if (!existingCanteen.getCanteenName().equals(canteen.getCanteenName()) && canteenService.isCanteenNameExist(canteen.getCanteenName())) {
            model.addAttribute("canteenNameError", "Canteen name already exists.");
            hasErrors = true;
        }

        // Nếu có lỗi, trả về form chỉnh sửa với các thông báo lỗi
        if (hasErrors) {
            model.addAttribute("canteen", canteen);
            return "admin-management/edit-canteen";
        }

        // Xử lý upload ảnh nếu có
        if (imageCanteenInput != null && !imageCanteenInput.isEmpty()) {
            String canteenImageUrl = fileS3Service.uploadFile(imageCanteenInput);
            canteen.setCanteenImg(canteenImageUrl);
        } else {
            canteen.setCanteenImg(existingCanteen.getCanteenImg());
        }

        // Cập nhật thông tin canteen
        canteenService.updateCanteen(canteen);
        redirectAttributes.addFlashAttribute("successMessage", "Canteen updated successfully");

        return "redirect:/manage-canteen/";
    }
}