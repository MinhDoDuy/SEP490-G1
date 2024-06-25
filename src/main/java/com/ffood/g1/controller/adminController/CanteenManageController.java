package com.ffood.g1.controller.adminController;

import com.ffood.g1.entity.Canteen;
import com.ffood.g1.entity.User;
import com.ffood.g1.exception.SpringBootFileUploadException;
import com.ffood.g1.service.CanteenService;
import com.ffood.g1.service.FileS3Service;
import com.ffood.g1.service.RoleService;
import com.ffood.g1.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Controller
public class CanteenManageController {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

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
        return "./admin-management/manage-canteen";
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

        return "./admin-management/manage-canteen";
    }

    @GetMapping("/add-canteen")
    public String showAddCanteenForm(Model model) {
        model.addAttribute("canteen", new Canteen());
        return "./admin-management/add-canteen";
    }

    @PostMapping("/add-canteen")
    public String addCanteen(@ModelAttribute("canteen") Canteen canteen, BindingResult result,
                             @RequestParam("imageCanteenInput") MultipartFile imageCanteenInput, Model model)
            throws IOException, SpringBootFileUploadException {

        // Upload file nếu có
        if (imageCanteenInput != null && !imageCanteenInput.isEmpty()) {
            String canteenImageUrl = fileS3Service.uploadFile(imageCanteenInput);
            canteen.setCanteenImg(canteenImageUrl);
        }

        canteenService.saveCanteen(canteen);
        return "redirect:/manage-canteen";
    }

    @GetMapping("/edit-canteen/{canteenId}")
    public String editCanteen(@PathVariable Integer canteenId, Model model) {
        Canteen canteen = canteenService.getCanteenById(canteenId);

        model.addAttribute("canteen", canteen);

        return "./admin-management/edit-canteen";
    }

    @PostMapping("/edit-canteen")
    public String updateCanteen(@ModelAttribute("canteen") Canteen canteen, BindingResult result,
                                @RequestParam("imageCanteenInput") MultipartFile imageCanteenInput, Model model)
            throws IOException, SpringBootFileUploadException {


        // Upload file nếu có
        if (imageCanteenInput != null && !imageCanteenInput.isEmpty()) {
            String canteenImageUrl = fileS3Service.uploadFile(imageCanteenInput);
            canteen.setCanteenImg(canteenImageUrl);
        }

        canteenService.updateCanteen(canteen);
        return "redirect:/manage-canteen";
    }

    @GetMapping("/delete-canteen/{canteenId}")
    public String deleteCanteen(@PathVariable Integer canteenId) {
        canteenService.deleteCanteenById(canteenId);
        return "redirect:/manage-canteen";
    }
}
