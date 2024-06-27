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

//    @GetMapping("/view-profile/{canteenId}")
//    public String viewProfileCanteen(@PathVariable Integer canteenId, Model model) {
//        Canteen canteen = canteenService.loadCanteenId(canteenId);
//        if (canteen != null) {
//            model.addAttribute("canteenId", canteenId);
//            return "staff-management/view-profile-canteen"; // Thymeleaf template name
//        } else {
//            model.addAttribute("error", "Canteen not found");
//            return "error"; // Error page template
//        }
//    }

    @GetMapping("/edit-profile/{canteenId}")
    public String editProfileCanteen(@PathVariable Integer canteenId, Model model) {
        Canteen canteen = canteenService.loadCanteenId(canteenId);
        if (canteen != null) {
            model.addAttribute("canteenId", canteenId);
            return "staff-management/edit-profile-canteen"; // Thymeleaf template name
        } else {
            model.addAttribute("error", "Canteen not found");
            return "error"; // Error page template
        }
    }

    @PostMapping("/update-profile")
    public String updateProfileCanteen(@ModelAttribute Canteen canteen, BindingResult result,
                                       @RequestParam("imageProfileInput") MultipartFile imageProfileInput, Model model)
            throws SpringBootFileUploadException, IOException {
        if (result.hasErrors()) {
            return "staff-management/edit-profile-canteen"; // Return to edit page if there are validation errors
        }

        // Upload file nếu có
        if (imageProfileInput != null && !imageProfileInput.isEmpty()) {
            String avatarURL = fileS3Service.uploadFile(imageProfileInput);
            canteen.setCanteenImg(avatarURL);
        }

        canteenService.updateCanteen(canteen);
        return "redirect:/canteen/view-profile/" + canteen.getCanteenId();
    }

}
