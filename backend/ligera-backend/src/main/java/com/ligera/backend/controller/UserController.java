package com.ligera.backend.controller;

import com.ligera.backend.dtos.request.UpdateProfileRequest;
import com.ligera.backend.dtos.response.UserResponse;
import com.ligera.backend.models.User;
import com.ligera.backend.service.AuthenticationService;
import com.ligera.backend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Controller handling user profile operations
 */
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
@Tag(name = "User", description = "User API")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final AuthenticationService authenticationService;
    private final UserService userService;

    /**
     * Get current user profile
     *
     * @return user profile data
     */
    @GetMapping("/me")
    @Operation(summary = "Get current user", description = "Retrieve the authenticated user's profile")
    public ResponseEntity<UserResponse> getCurrentUser() {
        return ResponseEntity.ok(userService.getCurrentUserProfile());
    }

    /**
     * Update current user profile
     *
     * @param request updated profile data
     * @return updated user profile
     */
    @PutMapping("/me")
    @Operation(summary = "Update user profile", description = "Update the authenticated user's profile information")
    public ResponseEntity<UserResponse> updateProfile(@Valid @RequestBody UpdateProfileRequest request) {
        User currentUser = authenticationService.getCurrentUser();
        
        // Update user fields
        currentUser.setName(request.getName());
        
        // Optional email update - would need additional verification in a real app
        if (request.getEmail() != null && !request.getEmail().equals(currentUser.getEmail())) {
            // In a real application, we would send a verification email
            // and only update after verification
            currentUser.setEmail(request.getEmail());
        }
        
        // Save and return updated profile
        User updatedUser = authenticationService.updateUser(currentUser);
        return ResponseEntity.ok(UserResponse.fromEntity(updatedUser));
    }
}

