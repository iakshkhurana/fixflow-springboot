package com.fixflow.controller;

import com.fixflow.dto.AuthResponse;
import com.fixflow.dto.LoginRequest;
import com.fixflow.dto.RegisterRequest;
import com.fixflow.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Handles user authentication.
 * These endpoints are public (no JWT required) — see SecurityConfig.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * POST /api/auth/login
     * Body: { email, password }
     * Returns: { token, user: { id, email, fullName, role, buildingId } }
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    /**
     * POST /api/auth/register
     * Body: { fullName, email, password, role, buildingId? }
     * Returns: same as login (auto-logs in after registration)
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }
}
