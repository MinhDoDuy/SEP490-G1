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
            model.addAttribute("success", success);
            model.addAttribute("error", error);
            return "staff-management/edit-profile-canteen"; // Thymeleaf template name
        } else {
            model.addAttribute("error", "Canteen not found");
            return "error"; // Error page template
        }
    }

    @PostMapping("/update-profile-canteen")
    public String updateProfileCanteen(@ModelAttribute("canteen") Canteen canteen, BindingResult result,
                                       @RequestParam("imageProfileInput") MultipartFile imageProfileInput, Model model)
            throws SpringBootFileUploadException, IOException {
        if (result.hasErrors()) {
            return "redirect:/canteen/edit-profile-canteen/" + canteen.getCanteenId() + "?error=Validation error";
        }

        Canteen existingCanteen = canteenService.loadCanteenId(canteen.getCanteenId());
        if (existingCanteen == null) {
            return "redirect:/canteen/edit-profile-canteen/" + canteen.getCanteenId() + "?error=Canteen not found";
        }

        if (imageProfileInput != null && !imageProfileInput.isEmpty()) {
            String avatarURL = fileS3Service.uploadFile(imageProfileInput);
            canteen.setCanteenImg(avatarURL);
        } else {
            canteen.setCanteenImg(existingCanteen.getCanteenImg());
        }

        canteenService.updateCanteen(canteen);
        return "redirect:/canteen/edit-profile-canteen/" + canteen.getCanteenId() + "?success=Cập nhật thông tin thành công";
    }
}
