package com.ffood.g1.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ErrorController {

    @RequestMapping("/403")
    public String accessDenied() {
        return "403";
    }


    @RequestMapping("/404")
    public String pageNoFound() {
        return "404";
    }
}
