package com.ligera.backend.controller;

import com.ligera.backend.dtos.request.LoginRequest;
import com.ligera.backend.dtos.request.PasswordChangeRequest;
import com.ligera.backend.dtos.request.RegisterRequest;
import com.ligera.backend.dtos.response.AuthResponse;
import com.ligera.backend.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "API endpoints for user registration, login, and authentication management")
@com.ligera.backend.versioning.ApiVersionRequestMapping(version = com.ligera.backend.versioning.ApiVersion.V1)
public class AuthController {

    private final AuthenticationService authenticationService;

    /**
     * Register a new user
     *
     * @param registerRequest the registration request data
     * @return authentication response with token
     */
    @PostMapping("/register")
    @Operation(
        summary = "Register a new user", 
        description = "Create a new user account with email and password"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "User successfully registered",
                content = @Content(schema = @Schema(implementation = AuthResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "409", description = "Email already in use")
    })
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest) {
        AuthResponse response = authenticationService.register(registerRequest);
        return com.ligera.backend.dtos.response.ApiResponse.created(response, "User registered successfully").toResponseEntity();
    }

    /**
     * Authenticate a user
     *
     * @param loginRequest the login request data
     * @return authentication response with token
     */
    @PostMapping("/login")
    @Operation(
        summary = "Login a user", 
        description = "Authenticate a user with email and password"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Authentication successful", 
                content = @Content(schema = @Schema(implementation = AuthResponse.class))),
        @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        AuthResponse response = authenticationService.login(loginRequest);
        return com.ligera.backend.dtos.response.ApiResponse.success(response, "Authentication successful").toResponseEntity();
    }

    /**
     * Change a user's password
     *
     * @param passwordChangeRequest the password change request data
     * @return success response
     */
    @PostMapping("/change-password")
    @PreAuthorize("isAuthenticated()")
    @Operation(
        summary = "Change password", 
        description = "Change the authenticated user's password",
        security = { @SecurityRequirement(name = "bearerAuth") }
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Password changed successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid password"),
        @ApiResponse(responseCode = "401", description = "Unauthorized or invalid current password")
    })
    public ResponseEntity<?> changePassword(@Valid @RequestBody PasswordChangeRequest passwordChangeRequest) {
        authenticationService.changePassword(passwordChangeRequest);
        return com.ligera.backend.dtos.response.ApiResponse.success("Password changed successfully").toResponseEntity();
    }
    
    /**
     * Get current user profile
     *
     * @return user profile data
     */
    @GetMapping("/me")
    @Operation(
        summary = "Get current user", 
        description = "Get the profile of the currently authenticated user",
        security = { @SecurityRequirement(name = "bearerAuth") }
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User profile retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getCurrentUser() {
        return com.ligera.backend.dtos.response.ApiResponse.success(
            authenticationService.getCurrentUserProfile(), 
            "User profile retrieved successfully"
        ).toResponseEntity();
    }
}

