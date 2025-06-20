package com.genx.adnmanagement.controller;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {
    @GetMapping({"/", "/home"})
    public String index(HttpSession session, Model model) {
        Object user = session.getAttribute("user");
        model.addAttribute("user", user);
        return "index"; // trả về templates/index.html
    }

    @GetMapping("/booking")
    public String booking(HttpSession session, Model model) {
        Object user = session.getAttribute("user");
        model.addAttribute("user", user);
        return "booking"; // trả về templates/booking.html
    }

    @GetMapping("/services")
    public String services(HttpSession session, Model model) {
        Object user = session.getAttribute("user");
        model.addAttribute("user", user);
        return "services"; // trả về templates/services.html
    }

    @GetMapping("/knowledge")
    public String knowledge(HttpSession session, Model model) {
        Object user = session.getAttribute("user");
        model.addAttribute("user", user);
        return "knowledge"; // trả về templates/knowledge.html
    }

    @GetMapping("/sampling-guide")
    public String samplingGuide(HttpSession session, Model model) {
        Object user = session.getAttribute("user");
        model.addAttribute("user", user);
        return "sample-collection"; // trả về templates/sample-collection.html
    }
    @GetMapping("/login")
    public String logIn() {
        return "login"; // trả về templates/login.html
    }
}
