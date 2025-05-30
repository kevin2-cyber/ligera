package com.ligera.backend.service;

import com.ligera.backend.dtos.request.LoginRequest;
import com.ligera.backend.dtos.request.PasswordChangeRequest;
import com.ligera.backend.dtos.request.RegisterRequest;
import com.ligera.backend.dtos.response.AuthResponse;
import com.ligera.backend.dtos.response.UserResponse;
import com.ligera.backend.enums.AccountStatus;
import com.ligera.backend.enums.Role;
import com.ligera.backend.exception.AuthException;
import com.ligera.backend.exception.PasswordValidationException;
import com.ligera.backend.models.User;
import com.ligera.backend.repositories.UserRepository;
import com.ligera.backend.security.JwtUtils;
import com.ligera.backend.security.validation.PasswordValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service handling user authentication, registration and security operations
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    private final PasswordValidator passwordValidator;

    /**
     * Register a new user
     *
     * @param request registration data
     * @return authentication response with JWT token
     */
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        log.info("Registering new user with email: {}", request.getEmail());

        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            log.warn("Email already in use: {}", request.getEmail());
            throw new AuthException("Email already in use");
        }
        
        // Validate password strength
        PasswordValidator.ValidationResult validationResult = passwordValidator.validate(request.getPassword());
        if (!validationResult.valid()) {
            log.warn("Password validation failed during registration: {}", validationResult);
            throw new PasswordValidationException(
                "Password does not meet security requirements", 
                validationResult.errors()
            );
        }

        // Create new user
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .accountStatus(AccountStatus.ACTIVE)
                .build();

        userRepository.save(user);
        log.info("User registered successfully: {}", user.getEmail());

        // Generate JWT token
        String jwt = jwtUtils.generateToken(user.getEmail());
        
        return AuthResponse.success(
                jwt,
                jwtUtils.getJwtExpirationMs() / 1000,
                UserResponse.fromEntity(user)
        );
    }

    /**
     * Authenticate a user and generate JWT token
     *
     * @param request login credentials
     * @return authentication response with JWT token
     */
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        log.info("Attempting to authenticate user: {}", request.getEmail());

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            // Set authentication in context
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Generate JWT token
            String jwt = jwtUtils.generateToken(authentication);

            // Get user details
            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            log.info("User authenticated successfully: {}", request.getEmail());

            return AuthResponse.success(
                    jwt,
                    jwtUtils.getJwtExpirationMs() / 1000,
                    UserResponse.fromEntity(user)
            );
        } catch (AuthenticationException e) {
            log.error("Authentication failed for user {}: {}", request.getEmail(), e.getMessage());
            throw new AuthException("Invalid email or password");
        }
    }

    /**
     * Change user password
     *
     * @param request password change data
     * @return true if password changed successfully
     */
    @Transactional
    public boolean changePassword(PasswordChangeRequest request) {
        User currentUser = getCurrentUser();
        log.info("Attempting to change password for user: {}", currentUser.getEmail());

        // Verify current password
        if (!passwordEncoder.matches(request.getCurrentPassword(), currentUser.getPassword())) {
            log.warn("Current password does not match for user: {}", currentUser.getEmail());
            throw new AuthException("Current password is incorrect");
        }
        
        // Validate new password strength
        PasswordValidator.ValidationResult validationResult = passwordValidator.validate(request.getNewPassword());
        if (!validationResult.valid()) {
            log.warn("Password validation failed during password change: {}", validationResult);
            throw new PasswordValidationException(
                "New password does not meet security requirements", 
                validationResult.errors()
            );
        }

        // Update password
        currentUser.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(currentUser);
        
        log.info("Password changed successfully for user: {}", currentUser.getEmail());
        return true;
    }

    /**
     * Validate JWT token
     *
     * @param token JWT token to validate
     * @return true if token is valid
     */
    public boolean validateToken(String token) {
        return jwtUtils.validateToken(token);
    }

    /**
     * Get the currently authenticated user
     *
     * @return the User entity
     */
    @Transactional(readOnly = true)
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AuthException("User not authenticated");
        }
        
        String email = authentication.getName();
        log.debug("Getting current user with email: {}", email);
        
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }

    /**
     * Get current user profile
     *
     * @return user response DTO
     */
    @Transactional(readOnly = true)
    public UserResponse getCurrentUserProfile() {
        return UserResponse.fromEntity(getCurrentUser());
    }
    
    /**
     * Update user entity
     *
     * @param user the user entity to update
     * @return updated user
     */
    @Transactional
    public User updateUser(User user) {
        log.info("Updating user: {}", user.getEmail());
        
        // Validate user
        if (user.getId() == null) {
            throw new IllegalArgumentException("User ID cannot be null for update");
        }
        
        // Check if email is already taken by another user
        if (!getCurrentUser().getEmail().equals(user.getEmail()) && 
                userRepository.findByEmail(user.getEmail()).isPresent()) {
            log.warn("Email already in use: {}", user.getEmail());
            throw new AuthException("Email already in use");
        }
        
        return userRepository.save(user);
    }
}

