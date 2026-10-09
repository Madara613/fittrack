package com.fittrack.controller;

import com.fittrack.dto.AuthResponse;
import com.fittrack.dto.LoginRequest;
import com.fittrack.dto.SignupRequest;
import com.fittrack.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller exposing endpoints for user registration, authentication, and token generation.
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * Registers a new user account and returns an authentication token.
     *
     * @param request signup details (name, email, password)
     * @return 201 Created with {@link AuthResponse} containing JWT token
     */
    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signup(@Valid @RequestBody SignupRequest request) {
        AuthResponse response = authService.signup(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Authenticates an existing user and returns an authentication token.
     *
     * @param request credentials (email, password)
     * @return 200 OK with {@link AuthResponse} containing JWT token
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}
