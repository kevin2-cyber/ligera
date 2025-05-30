package com.ligera.app.network.model.response;

import com.google.gson.annotations.SerializedName;

/**
 * Response model for authentication operations
 */
public class AuthResponse {
    @SerializedName("status")
    private String status;
    
    @SerializedName("token")
    private String token;
    
    @SerializedName("expiresIn")
    private long expiresIn;
    
    @SerializedName("user")
    private UserResponse user;
    
    @SerializedName("message")
    private String message;
    
    public AuthResponse() {
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
    
    public long getExpiresIn() {
        return expiresIn;
    }
    
    public void setExpiresIn(long expiresIn) {
        this.expiresIn = expiresIn;
    }
    
    public UserResponse getUser() {
        return user;
    }
    
    public void setUser(UserResponse user) {
        this.user = user;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public boolean isSuccess() {
        return "success".equalsIgnoreCase(status);
    }
}

