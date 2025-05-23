package com.ligera.app.network.model.request;

import com.google.gson.annotations.SerializedName;

/**
 * Request model for token refresh
 */
public class RefreshTokenRequest {
    @SerializedName("refreshToken")
    private String refreshToken;
    
    public RefreshTokenRequest(String refreshToken) {
        this.refreshToken = refreshToken;
    }
    
    public String getRefreshToken() {
        return refreshToken;
    }
    
    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}

