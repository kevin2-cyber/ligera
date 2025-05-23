package com.ligera.app.network;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import java.io.IOException;
import java.security.GeneralSecurityException;

/**
 * Manages authentication tokens for the app
 * Uses EncryptedSharedPreferences for secure token storage
 */
public class TokenManager {
    private static final String TAG = "TokenManager";
    
    // Shared preferences keys
    private static final String PREF_NAME = "ligera_auth_prefs";
    private static final String KEY_ACCESS_TOKEN = "access_token";
    private static final String KEY_REFRESH_TOKEN = "refresh_token";
    private static final String KEY_TOKEN_EXPIRY = "token_expiry";
    private static final String KEY_JWT_TOKEN = "jwt_token"; // For backward compatibility
    
    // Singleton instance
    private static TokenManager instance;
    
    // Shared preferences for storing tokens securely
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
            Log.d(TAG, "Using encrypted shared preferences for token storage");
        } catch (GeneralSecurityException | IOException e) {
            Log.e(TAG, "Error creating encrypted shared preferences: " + e.getMessage());
            // Fallback to regular shared preferences if encryption fails
            securePreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            Log.w(TAG, "Falling back to standard shared preferences");
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
     * Save authentication tokens
     * 
     * @param accessToken the access token
     * @param refreshToken the refresh token
     * @param expiryTimeMillis the expiry time in milliseconds
     */
    public void saveTokens(String accessToken, String refreshToken, long expiryTimeMillis) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY_ACCESS_TOKEN, accessToken);
        editor.putString(KEY_REFRESH_TOKEN, refreshToken);
        editor.putLong(KEY_TOKEN_EXPIRY, expiryTimeMillis);
        editor.apply();
        
        Log.d(TAG, "Tokens saved with expiry: " + expiryTimeMillis);
    }
    
    /**
     * Get the access token if valid
     * 
     * @return the access token or null if not available or expired
     */
    @Nullable
    public String getAccessToken() {
        if (isTokenExpired()) {
            Log.d(TAG, "Token is expired, returning null");
            return null;
        }
        return preferences.getString(KEY_ACCESS_TOKEN, null);
    }
    
    /**
     * Get the refresh token
     * 
     * @return the refresh token or null if not available
     */
    @Nullable
    public String getRefreshToken() {
        return preferences.getString(KEY_REFRESH_TOKEN, null);
    }
    
    /**
     * Check if the access token is expired
     * 
     * @return true if expired, false otherwise
     */
    public boolean isTokenExpired() {
        long expiryTime = preferences.getLong(KEY_TOKEN_EXPIRY, 0);
        boolean expired = System.currentTimeMillis() >= expiryTime;
        
        if (expired) {
            Log.d(TAG, "Token is expired. Expiry time: " + expiryTime + ", Current time: " + System.currentTimeMillis());
        }
        
        return expired;
    }
    
    /**
     * Check if the user is logged in (has valid tokens)
     * 
     * @return true if logged in, false otherwise
     */
    public boolean isLoggedIn() {
        String accessToken = preferences.getString(KEY_ACCESS_TOKEN, null);
        boolean loggedIn = !TextUtils.isEmpty(accessToken) && !isTokenExpired();
        
        // Also check for JWT token for backward compatibility
        if (!loggedIn) {
            String jwtToken = preferences.getString(KEY_JWT_TOKEN, null);
            loggedIn = !TextUtils.isEmpty(jwtToken) && !isTokenExpired();
        }
        
        return loggedIn;
    }
    
    /**
     * Clear all tokens (logout)
     */
    public void clearTokens() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(KEY_ACCESS_TOKEN);
        editor.remove(KEY_REFRESH_TOKEN);
        editor.remove(KEY_TOKEN_EXPIRY);
        editor.remove(KEY_JWT_TOKEN); // Also clear JWT token for backward compatibility
        editor.apply();
        
        Log.d(TAG, "All tokens cleared");
    }
    
    /**
     * Save token with expiry time in seconds (JWT token format)
     * For backward compatibility with JWT token format
     * 
     * @param token the JWT token to save
     * @param expiryTimeInSeconds expiry time in seconds
     */
    public void saveToken(String token, long expiryTimeInSeconds) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY_JWT_TOKEN, token);
        editor.putString(KEY_ACCESS_TOKEN, token); // Also save as access token
        
        // Calculate expiry time in milliseconds
        long expiryTimeMillis = System.currentTimeMillis() + (expiryTimeInSeconds * 1000);
        editor.putLong(KEY_TOKEN_EXPIRY, expiryTimeMillis);
        
        editor.apply();
        
        Log.d(TAG, "JWT token saved with expiry in seconds: " + expiryTimeInSeconds);
    }
    
    /**
     * Get stored JWT token (for backward compatibility)
     * 
     * @return JWT token or null if not available or expired
     */
    public String getToken() {
        if (isTokenExpired()) {
            clearTokens();
            return null;
        }
        
        String token = preferences.getString(KEY_JWT_TOKEN, null);
        if (token == null) {
            // Fall back to access token if JWT token is not available
            token = getAccessToken();
        }
        
        return token;
    }
    
    /**
     * Check if token exists and is valid (for backward compatibility)
     * 
     * @return true if token exists and is not expired
     */
    public boolean hasValidToken() {
        return isLoggedIn();
    }
    
    /**
     * Clear stored token and expiry time (for backward compatibility)
     */
    public void clearToken() {
        clearTokens();
    }
}

