package com.ffood.g1.controller.manageStaff;

import com.ffood.g1.entity.Canteen;
import com.ffood.g1.exception.SpringBootFileUploadException;
import com.ffood.g1.service.CanteenService;
import com.ffood.g1.service.FileS3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

@Controller
@RequestMapping("/canteen")
public class ProfileCanteenController {

    @Autowired
    private CanteenService canteenService;

    @Autowired
    private FileS3Service fileS3Service;

    @GetMapping("/edit-profile-canteen/{canteenId}")
    public String editProfileCanteen(@PathVariable Integer canteenId, Model model,
                                     @RequestParam(value = "success", required = false) String success,
                                     @RequestParam(value = "error", required = false) String error) {
        Canteen canteen = canteenService.loadCanteenId(canteenId);
        if (canteen != null) {
            model.addAttribute("canteen", canteen);
            if (success != null) {
                model.addAttribute("successMessage", success);
            }
            if (error != null) {
                model.addAttribute("errorMessage", error);
            }
            return "staff-management/edit-profile-canteen"; // Tên template Thymeleaf
        } else {
            model.addAttribute("errorMessage", "Không tìm thấy canteen.");
            return "error"; // Template trang lỗi
        }
    }

    @PostMapping("/update-profile-canteen")
    public String updateProfileCanteen(@ModelAttribute("canteen") Canteen canteen, BindingResult result,
                                       @RequestParam("imageProfileInput") MultipartFile imageProfileInput,
                                       RedirectAttributes redirectAttributes)
            throws SpringBootFileUploadException, IOException {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi xác thực.");
            return "redirect:/canteen/edit-profile-canteen/" + canteen.getCanteenId();
        }

        // Xác thực các trường của canteen
        String canteenName = canteen.getCanteenName().trim();
        String location = canteen.getLocation().trim();
        String canteenPhone = canteen.getCanteenPhone().trim();

        if (canteenName.isEmpty() || location.isEmpty() || canteenPhone.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Tất cả các trường là bắt buộc.");
            return "redirect:/canteen/edit-profile-canteen/" + canteen.getCanteenId();
        }

        // Kiểm tra số điện thoại đã tồn tại cho một canteen khác
        if (canteenService.isPhoneExist(canteenPhone) && !canteenService.loadCanteenId(canteen.getCanteenId()).getCanteenPhone().equals(canteenPhone)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Số điện thoại đã tồn tại.");
            return "redirect:/canteen/edit-profile-canteen/" + canteen.getCanteenId();
        }

        Canteen existingCanteen = canteenService.loadCanteenId(canteen.getCanteenId());
        if (existingCanteen == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy canteen.");
            return "redirect:/canteen/edit-profile-canteen/" + canteen.getCanteenId();
        }

        if (imageProfileInput != null && !imageProfileInput.isEmpty()) {
            try {
                String avatarURL = fileS3Service.uploadFile(imageProfileInput);
                canteen.setCanteenImg(avatarURL);
            } catch (SpringBootFileUploadException e) {
                redirectAttributes.addFlashAttribute("errorMessage", "Lỗi tải lên tệp.");
                return "redirect:/canteen/edit-profile-canteen/" + canteen.getCanteenId();
            }
        } else {
            canteen.setCanteenImg(existingCanteen.getCanteenImg());
        }

        canteen.setCanteenName(canteenName);
        canteen.setLocation(location);
        canteen.setCanteenPhone(canteenPhone);

        canteenService.updateCanteen(canteen);
        redirectAttributes.addFlashAttribute("successMessage", "Thay đổi thành công.");
        return "redirect:/canteen/edit-profile-canteen/" + canteen.getCanteenId();
    }
}
