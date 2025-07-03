package com.amwojcik.newssorter.controllers;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;


@Controller
public class FrontendController {

    @GetMapping("/")
    public String home() {
        return "redirect:/ui";
    }

    @GetMapping("/ui")
    public String reactUi() {
        return "forward:/app/index.html";
    }

}