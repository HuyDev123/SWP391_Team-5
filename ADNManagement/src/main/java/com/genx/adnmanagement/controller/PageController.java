package com.genx.adnmanagement.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import com.genx.adnmanagement.entity.User;

import java.util.HashMap;
import java.util.Map;

@Controller
public class PageController {

    /**
     * Phương thức trợ giúp để thêm dữ liệu người dùng vào model nếu đã đăng nhập
     */
    private void addUserDataToModel(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user != null) {
            Map<String, Object> userData = new HashMap<>();
            userData.put("id", user.getId());
            userData.put("fullName", user.getFullName());
            userData.put("email", user.getEmail());
            userData.put("roleId", user.getRoleId());

            model.addAttribute("userData", userData);
        }
    }

    private String requireLogin(Model model, HttpSession session, String viewName) {
        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }
        addUserDataToModel(model, session);
        return viewName;
    }

    @GetMapping({"/", "/home"})
    public String index(Model model, HttpSession session) {
        addUserDataToModel(model, session);
        return "index";
    }

    @GetMapping("/booking")
    public String booking(Model model, HttpSession session) {
        addUserDataToModel(model, session);
        return "booking";
    }

    @GetMapping("/services")
    public String services(Model model, HttpSession session) {
        addUserDataToModel(model, session);
        return "services";
    }

    @GetMapping("/knowledge")
    public String knowledge(Model model, HttpSession session) {
        addUserDataToModel(model, session);
        return "knowledge";
    }

    @GetMapping("/sampling-guide")
    public String samplingGuide(Model model, HttpSession session) {
        addUserDataToModel(model, session);
        return "sample-collection";
    }

    @GetMapping("/login")
    public String logIn(HttpSession session) {
        if (session.getAttribute("user") != null) {
            return "redirect:/home";
        }
        return "login";
    }

    @GetMapping("/appoinments-list")
    public String appoinments(Model model, HttpSession session) {
        return requireLogin(model, session, "appoinments");
    }

    @GetMapping("/test-history")
    public String historyTest(Model model, HttpSession session) {
        return requireLogin(model, session, "historytest");
    }
}