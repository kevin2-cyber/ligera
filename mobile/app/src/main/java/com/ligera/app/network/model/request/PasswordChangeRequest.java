package com.ligera.app.network.model.request;

import com.google.gson.annotations.SerializedName;

/**
 * Request model for password change
 */
public class PasswordChangeRequest {
    @SerializedName("currentPassword")
    private String currentPassword;
    
    @SerializedName("newPassword")
    private String newPassword;
    
    public PasswordChangeRequest(String currentPassword, String newPassword) {
        this.currentPassword = currentPassword;
        this.newPassword = newPassword;
    }
    
    public String getCurrentPassword() {
        return currentPassword;
    }
    
    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }
    
    public String getNewPassword() {
        return newPassword;
    }
    
    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}

