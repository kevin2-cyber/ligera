package com.ligera.app.network;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import java.io.IOException;
import java.security.GeneralSecurityException;

/**
 * Manager class for handling JWT tokens
 * Uses EncryptedSharedPreferences for secure token storage
 */
public class TokenManager {
    private static final String TAG = "TokenManager";
    
    // Shared preferences keys
    private static final String PREF_NAME = "ligera_auth_prefs";
    private static final String KEY_JWT_TOKEN = "jwt_token";
    private static final String KEY_TOKEN_EXPIRY = "token_expiry";
    
    // Singleton instance
    private static TokenManager instance;
    
    // Shared preferences for storing token securely
    private final SharedPreferences preferences;
    
    private TokenManager(Context context) {
        SharedPreferences securePreferences;
        
        try {
            // Create or retrieve the master key for encryption/decryption
            MasterKey masterKey = new MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();
            
            // Create encrypted shared preferences
            securePreferences = EncryptedSharedPreferences.create(
                    context,
                    PREF_NAME,
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (GeneralSecurityException | IOException e) {
            Log.e(TAG, "Error creating encrypted shared preferences: " + e.getMessage());
            // Fallback to regular shared preferences if encryption fails
            securePreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        }
        
        this.preferences = securePreferences;
    }
    
    /**
     * Get singleton instance of TokenManager
     * 
     * @param context application context
     * @return TokenManager instance
     */
    public static synchronized TokenManager getInstance(@NonNull Context context) {
        if (instance == null) {
            instance = new TokenManager(context.getApplicationContext());
        }
        return instance;
    }
    
    /**
     * Save JWT token and its expiry time
     * 
     * @param token JWT token
     * @param expiryTimeInSeconds token expiry time in seconds
     */
    public void saveToken(String token, long expiryTimeInSeconds) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY_JWT_TOKEN, token);
        
        // Calculate expiry time in milliseconds
        long expiryTimeMillis = System.currentTimeMillis() + (expiryTimeInSeconds * 1000);
        editor.putLong(KEY_TOKEN_EXPIRY, expiryTimeMillis);
        
        editor.apply();
    }
    
    /**
     * Get stored JWT token
     * 
     * @return JWT token or null if not available
     */
    public String getToken() {
        // Check if token is expired
        if (isTokenExpired()) {
            // Clear expired token
            clearToken();
            return null;
        }
        
        return preferences.getString(KEY_JWT_TOKEN, null);
    }
    
    /**
     * Check if token exists and is valid
     * 
     * @return true if token exists and is not expired
     */
    public boolean hasValidToken() {
        String token = preferences.getString(KEY_JWT_TOKEN, null);
        return token != null && !token.isEmpty() && !isTokenExpired();
    }
    
    /**
     * Check if stored token is expired
     * 
     * @return true if token is expired or missing
     */
    public boolean isTokenExpired() {
        long expiryTime = preferences.getLong(KEY_TOKEN_EXPIRY, 0);
        return System.currentTimeMillis() > expiryTime;
    }
    
    /**
     * Clear stored token and expiry time
     */
    public void clearToken() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(KEY_JWT_TOKEN);
        editor.remove(KEY_TOKEN_EXPIRY);
        editor.apply();
    }
}

