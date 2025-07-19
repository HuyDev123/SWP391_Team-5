package com.genx.adnmanagement.controller;

import com.genx.adnmanagement.entity.User;
import com.genx.adnmanagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpSession;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/register")
public class RegisterController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("")
    public String showNewProfile(Model model, HttpSession session) {
        String email = (String) session.getAttribute("register_email");
        if (email == null || email.isEmpty()) {
            // Nếu không có email xác thực Google, chuyển hướng về trang đăng nhập
            return "redirect:/login";
        }
        model.addAttribute("email", email);
        return "/newprofile";
    }

    @PostMapping("/complete")
    public String completeProfile(@RequestParam String fullname,
                                  Model model,
                                  HttpSession session) {
        String email = (String) session.getAttribute("register_email");
        if (email == null || email.isEmpty()) {
            model.addAttribute("error", "Thiếu email đã xác thực Google. Vui lòng đăng nhập lại bằng Google.");
            return "newprofile";
        }
        try {
            User user = new User(fullname, email);
            user.setCreatedAt(java.time.LocalDateTime.now());
            userRepository.save(user);
            // Xóa session ngay sau khi đăng ký thành công
            session.removeAttribute("register_email");
            // Đăng nhập luôn: lưu user vào session
            session.setAttribute("user", user);
            return "redirect:/home";
        } catch (Exception e) {
            model.addAttribute("error", "Đăng ký thất bại: " + e.getMessage());
            model.addAttribute("email", email);
            return "newprofile";
        }
    }

}
