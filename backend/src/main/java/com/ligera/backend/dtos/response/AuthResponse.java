package com.ligera.backend.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for authentication responses containing JWT token and user information
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private String token;
    
    @Builder.Default
    private String type = "Bearer";
    
    private long expiresIn;
    
    private UserResponse user;

    /**
     * Factory method to create a successful authentication response
     *
     * @param token JWT token
     * @param expiresIn token expiration time in seconds
     * @param user user information
     * @return AuthResponse object
     */
    public static AuthResponse success(String token, long expiresIn, UserResponse user) {
        return AuthResponse.builder()
                .token(token)
                .expiresIn(expiresIn)
                .user(user)
                .build();
    }
}

