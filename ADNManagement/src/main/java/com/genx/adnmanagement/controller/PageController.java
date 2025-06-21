package com.genx.adnmanagement.controller;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {
    @GetMapping({"/", "/home"})
    public String index() {
        return "index"; // trả về templates/index.html
    }

    @GetMapping("/booking")
    public String booking() {
        return "booking"; // trả về templates/booking.html
    }

    @GetMapping("/services")
    public String services() {
        return "services"; // trả về templates/services.html
    }

    @GetMapping("/knowledge")
    public String knowledge() {
        return "knowledge"; // trả về templates/knowledge.html
    }

    @GetMapping("/sampling-guide")
    public String samplingGuide() {
        return "sample-collection"; // trả về templates/sample-collection.html
    }
    @GetMapping("/login")
    public String logIn(HttpSession session) {
        if (session.getAttribute("user") != null) {
            return "redirect:/home";
        }
        return "login"; // trả về templates/login.html
    }
}
