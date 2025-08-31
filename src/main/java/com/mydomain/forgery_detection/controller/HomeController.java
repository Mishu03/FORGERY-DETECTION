package com.mydomain.forgery_detection.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        // This will look for src/main/resources/templates/index.html 
        // if you use Thymeleaf
        // OR src/main/resources/static/index.html if you just want plain HTML
        return "index";
    }
}