
package com.ptit.identity_service.controller;

import com.ptit.identity_service.dto.LoginRequest;
import com.ptit.identity_service.dto.LoginResponse;
import com.ptit.identity_service.service.AuthService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ptit.identity_service.dto.ChangePasswordRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @RequestBody LoginRequest request) {

        LoginResponse response = authService.login(request);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/change-password")
    public ResponseEntity<LoginResponse> changePassword(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody ChangePasswordRequest request) {

        LoginResponse response = authService.changePassword(
                jwt.getSubject(),
                request);

        return ResponseEntity.ok(response);
    }

}
