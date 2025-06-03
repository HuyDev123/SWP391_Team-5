package com.genx.adnmanagement.controller;

import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @GetMapping("/google/request")
    public RedirectView googleLogin(@RequestParam String role) {
        if (!role.equals("customer") && !role.equals("internalUser")) {
            throw new IllegalArgumentException("Invalid role");
        }

        String authorizationUri = "https://accounts.google.com/o/oauth2/v2/auth";
        String clientId = "YOUR_GOOGLE_CLIENT_ID"; // Replace with actual client ID
        String redirectUri = "http://localhost:8080/login/oauth2/code/google";
        String scope = "openid profile email";

        String url = authorizationUri + "?" +
                OAuth2ParameterNames.CLIENT_ID + "=" + clientId + "&" +
                OAuth2ParameterNames.REDIRECT_URI + "=" + redirectUri + "&" +
                OAuth2ParameterNames.RESPONSE_TYPE + "=code&" +
                OAuth2ParameterNames.SCOPE + "=" + scope;

        return new RedirectView(url);
    }

    @PostMapping("/complete-profile")
    public ResponseEntity<?> completeProfile(@RequestParam String fullname) {
        // Logic to save the customer's profile
        return ResponseEntity.ok("Profile completed for: " + fullname);
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(OAuth2AuthenticationToken authentication) {
        OAuth2User user = authentication.getPrincipal();
        Map<String, Object> attributes = user.getAttributes();
        return ResponseEntity.ok(attributes);
    }
}
