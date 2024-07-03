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
@RequestMapping("canteen")
public class ProfileCanteenController {

    @Autowired
    private CanteenService canteenService;

    @Autowired
    private FileS3Service fileS3Service;

    @GetMapping("/view-profile/{canteenId}")
    public String viewProfileCanteen(@PathVariable Integer canteenId, Model model) {
        Canteen canteen = canteenService.loadCanteenId(canteenId);
        if (canteen != null) {
            model.addAttribute("canteen", canteen);
            return "./staff-management/edit-profile-canteen"; // Thymeleaf template name
        } else {
            model.addAttribute("error", "Canteen not found");
            return "error"; // Error page template
        }
    }

    @GetMapping("/edit-profile-canteen/{canteenId}")
    public String editProfileCanteen(@PathVariable Integer canteenId, Model model) {
        Canteen canteen = canteenService.loadCanteenId(canteenId);
        if (canteen != null) {
            model.addAttribute("canteen", canteen);
            return "./staff-management/edit-profile-canteen"; // Thymeleaf template name
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
            return "redirect:/staff-management/edit-profile-canteen"; // Return to edit page if there are validation errors
        }

        // Check if a new image is uploaded
        // Fetch the existing canteen data from the database
        Canteen existingCanteen = canteenService.loadCanteenId(canteen.getCanteenId());
        if (existingCanteen == null) {
            model.addAttribute("error", "Canteen not found");
            return "error"; // Error page template
        }

        // Check if a new image is uploaded
        if (imageProfileInput != null && !imageProfileInput.isEmpty()) {
            String avatarURL = fileS3Service.uploadFile(imageProfileInput);
            canteen.setCanteenImg(avatarURL);
        } else {
            // Retain the existing image if no new image is uploaded
            canteen.setCanteenImg(existingCanteen.getCanteenImg());
        }

        canteenService.updateCanteen(canteen);
        return "redirect:/canteen/edit-profile-canteen/" + canteen.getCanteenId();
    }
}