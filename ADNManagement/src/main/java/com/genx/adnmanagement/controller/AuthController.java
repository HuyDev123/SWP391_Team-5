package com.genx.adnmanagement.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;

import java.util.Map;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import java.util.Collections;
import java.security.GeneralSecurityException;
import java.io.IOException;

import com.genx.adnmanagement.repository.UserRepository;
import com.genx.adnmanagement.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/auth")
public class AuthController {
    private static final String CLIENT_ID = "566007622451-fopq1t8kt1ep6q48i7e6pic597pk896q.apps.googleusercontent.com";

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/google")
    public String googleLogin(@RequestParam("token") String token, HttpSession session, Model model) {
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    JacksonFactory.getDefaultInstance())
                    .setAudience(Collections.singletonList(CLIENT_ID))
                    .build();
            GoogleIdToken idToken = verifier.verify(token);
            if (idToken != null) {
                GoogleIdToken.Payload googlePayload = idToken.getPayload();
                String email = googlePayload.getEmail();
                // Kiểm tra User tồn tại chưa
                var userOptional = userRepository.findByEmail(email);
                if (userOptional.isEmpty()) {
                    // Chưa có user, lưu email vào session và chuyển đến /register
                    session.setAttribute("register_email", email);
                    return "redirect:/register";
                } else {
                    // Đã có user, lưu vào session và chuyển đến /home
                    User user = userOptional.get();
                    session.setAttribute("user", user);
                    return "redirect:/home";
                }
            } else {
                model.addAttribute("error", "Invalid ID token");
                return "login";
            }
        } catch (GeneralSecurityException | IOException e) {
            model.addAttribute("error", "Token verification failed: " + e.getMessage());
            return "login";
        }
    }
}
