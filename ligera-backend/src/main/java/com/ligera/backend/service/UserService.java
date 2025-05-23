package com.ligera.backend.service;

import com.ligera.backend.config.CacheConfig;
import com.ligera.backend.dtos.response.UserResponse;
import com.ligera.backend.exception.AuthException;
import com.ligera.backend.models.User;
import com.ligera.backend.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service handling user operations
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final AuthenticationService authenticationService;

    /**
     * Get user by ID
     *
     * @param id user ID
     * @return user entity
     */
    @Transactional(readOnly = true)
    @Cacheable(value = CacheConfig.USER_CACHE, key = "#id")
    public User getUserById(Long id) {
        log.debug("Cache miss - Getting user by ID: {}", id);
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + id));
    }

    /**
     * Get user by email
     *
     * @param email user email
     * @return optional user
     */
    @Transactional(readOnly = true)
    @Cacheable(value = CacheConfig.USER_EMAIL_CACHE, key = "#email")
    public Optional<User> getUserByEmail(String email) {
        log.debug("Cache miss - Getting user by email: {}", email);
        return userRepository.findByEmail(email);
    }

    /**
     * Update user profile
     *
     * @param user updated user entity
     * @return updated user
     */
    @Transactional
    @Caching(evict = {
        @CacheEvict(value = CacheConfig.USER_CACHE, key = "#user.id"),
        @CacheEvict(value = CacheConfig.USER_EMAIL_CACHE, key = "#user.email"),
        @CacheEvict(value = CacheConfig.USER_PROFILE_CACHE, allEntries = true)
    })
    public User updateUser(User user) {
        log.info("Updating user profile for user: {}", user.getEmail());
        
        // Check if user exists
        if (user.getId() == null || !userRepository.existsById(user.getId())) {
            log.error("Cannot update non-existent user");
            throw new EntityNotFoundException("User not found");
        }
        
        // Check if email is already taken by another user
        if (userRepository.findByEmail(user.getEmail())
                .map(existingUser -> !existingUser.getId().equals(user.getId()))
                .orElse(false)) {
            log.error("Email already in use: {}", user.getEmail());
            throw new AuthException("Email already in use");
        }
        
        return userRepository.save(user);
    }

    /**
     * Get current user profile
     *
     * @return user response DTO
     */
    @Transactional(readOnly = true)
    @Cacheable(value = CacheConfig.USER_PROFILE_CACHE, key = "@authenticationService.getCurrentUser().getId()")
    public UserResponse getCurrentUserProfile() {
        log.debug("Cache miss - Getting current user profile");
        User currentUser = authenticationService.getCurrentUser();
        return UserResponse.fromEntity(currentUser);
    }
}

