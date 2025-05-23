package com.ligera.backend.controller;

import com.ligera.backend.dtos.request.LoginRequest;
import com.ligera.backend.dtos.request.PasswordChangeRequest;
import com.ligera.backend.dtos.request.RegisterRequest;
import com.ligera.backend.dtos.response.AuthResponse;
import com.ligera.backend.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Controller handling authentication-related endpoints
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication API")
public class AuthController {

    private final AuthenticationService authenticationService;

    /**
     * Register a new user
     *
     * @param registerRequest the registration request data
     * @return authentication response with token
     */
    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Create a new user account with email and password")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
        return new ResponseEntity<>(authenticationService.register(registerRequest), HttpStatus.CREATED);
    }

    /**
     * Authenticate a user
     *
     * @param loginRequest the login request data
     * @return authentication response with token
     */
    @PostMapping("/login")
    @Operation(summary = "Login a user", description = "Authenticate a user with email and password")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(authenticationService.login(loginRequest));
    }

    /**
     * Change a user's password
     *
     * @param passwordChangeRequest the password change request data
     * @return success response
     */
    @PostMapping("/change-password")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Change password", description = "Change the authenticated user's password")
    public ResponseEntity<String> changePassword(@Valid @RequestBody PasswordChangeRequest passwordChangeRequest) {
        authenticationService.changePassword(passwordChangeRequest);
        return ResponseEntity.ok("Password changed successfully");
    }
}

