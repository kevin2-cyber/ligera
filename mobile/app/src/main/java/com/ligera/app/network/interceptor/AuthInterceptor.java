package com.ligera.app.network.interceptor;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.ligera.app.network.ApiConfig;
import com.ligera.app.network.TokenManager;
import com.ligera.app.network.model.request.RefreshTokenRequest;
import com.ligera.app.network.model.response.AuthResponse;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Interceptor to add JWT authorization header to requests
 */
public class AuthInterceptor implements Interceptor {
    private static final String TAG = "AuthInterceptor";
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    
    private final TokenManager tokenManager;
    private final ReentrantLock refreshTokenLock = new ReentrantLock();
    private final Gson gson = new Gson();
    private final OkHttpClient refreshClient;
    
    public AuthInterceptor(TokenManager tokenManager) {
        this.tokenManager = tokenManager;
        
        // Create a dedicated client for refresh token requests to avoid infinite loops
        this.refreshClient = new OkHttpClient.Builder()
                .connectTimeout(ApiConfig.AUTH_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(ApiConfig.AUTH_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(ApiConfig.AUTH_TIMEOUT, TimeUnit.SECONDS)
                .build();
    }
    
    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request originalRequest = chain.request();
        
        // Skip authentication for auth endpoints (login, register)
        if (originalRequest.url().toString().contains("/auth/login") || 
            originalRequest.url().toString().contains("/auth/register")) {
            return chain.proceed(originalRequest);
        }
        
        // Skip authentication for refresh token endpoint to avoid infinite loops
        if (originalRequest.url().toString().contains("/auth/refresh")) {
            return chain.proceed(originalRequest);
        }
        
        // Get JWT token from token manager
        String token = tokenManager.getAccessToken();
        
        // If no token is available, proceed with the original request
        if (token == null || token.isEmpty()) {
            return chain.proceed(originalRequest);
        }
        
        // Add JWT token to request header
        Request authorizedRequest = originalRequest.newBuilder()
                .header("Authorization", "Bearer " + token)
                .method(originalRequest.method(), originalRequest.body())
                .build();
                
        // Proceed with the authorized request
        Response response = chain.proceed(authorizedRequest);
        
        // If the response is 401 Unauthorized, attempt to refresh the token
        if (response.code() == 401) {
            Log.d(TAG, "Received 401 response, attempting to refresh token");
            
            // Close the body of the error response
            if (response.body() != null) {
                response.close();
            }
            
            // Attempt to refresh the token
            String newToken = refreshToken();
            
            // If token refresh was successful, retry the original request with the new token
            if (newToken != null && !newToken.isEmpty()) {
                Log.d(TAG, "Token refresh successful, retrying original request");
                Request newRequest = originalRequest.newBuilder()
                        .header("Authorization", "Bearer " + newToken)
                        .method(originalRequest.method(), originalRequest.body())
                        .build();
                
                // Close the original response before making a new request
                response.close();
                
                // Retry the original request with the new token
                return chain.proceed(newRequest);
            } else {
                // If token refresh failed, proceed with the 401 response
                Log.w(TAG, "Token refresh failed, returning 401 response");
                return response;
            }
        }
        
        // Return the original response for non-401 responses
        return response;
    }
    
    /**
     * Refresh the access token using the refresh token
     * This method is synchronized to prevent multiple simultaneous refresh attempts
     * 
     * @return The new access token or null if refresh failed
     */
    private String refreshToken() {
        // Get the refresh token
        String refreshToken = tokenManager.getRefreshToken();
        
        // If no refresh token is available, return null
        if (refreshToken == null || refreshToken.isEmpty()) {
            Log.w(TAG, "No refresh token available, cannot refresh");
            return null;
        }
        
        // Acquire the lock to ensure only one thread attempts to refresh at a time
        boolean lockAcquired = false;
        try {
            // Try to acquire the lock with a timeout
            lockAcquired = refreshTokenLock.tryLock(10, TimeUnit.SECONDS);
            
            if (!lockAcquired) {
                Log.w(TAG, "Failed to acquire refresh token lock, another thread may be refreshing");
                return null;
            }
            
            // Before making the request, check if token has already been refreshed by another thread
            String currentToken = tokenManager.getAccessToken();
            if (currentToken != null && !currentToken.isEmpty() && !tokenManager.isTokenExpired()) {
                Log.d(TAG, "Token already refreshed by another thread");
                return currentToken;
            }
            
            // Create the refresh token request
            RefreshTokenRequest refreshRequest = new RefreshTokenRequest(refreshToken);
            RequestBody requestBody = RequestBody.create(
                    gson.toJson(refreshRequest), JSON);
            
            // Create the request
            Request request = new Request.Builder()
                    .url(ApiConfig.BASE_URL + ApiConfig.Endpoints.REFRESH_TOKEN)
                    .post(requestBody)
                    .build();
            
            // Execute the request
            try {
                Response response = refreshClient.newCall(request).execute();
                
                if (response.isSuccessful() && response.body() != null) {
                    // Parse the response
                    String responseBody = response.body().string();
                    AuthResponse authResponse = gson.fromJson(responseBody, AuthResponse.class);
                    
                    if (authResponse != null && authResponse.isSuccess() && authResponse.getToken() != null) {
                        // Save the new tokens
                        tokenManager.saveTokens(
                                authResponse.getToken(), 
                                refreshToken, // Keep the same refresh token
                                System.currentTimeMillis() + (authResponse.getExpiresIn() * 1000)
                        );
                        
                        Log.d(TAG, "Token refreshed successfully");
                        return authResponse.getToken();
                    }
                } else {
                    // Log the failure
                    String errorBody = response.body() != null ? response.body().string() : "No response body";
                    Log.e(TAG, "Token refresh failed: " + response.code() + ", " + errorBody);
                    
                    // If refresh token is invalid (401), clear all tokens to force re-login
                    if (response.code() == 401) {
                        Log.w(TAG, "Refresh token is invalid, clearing all tokens");
                        tokenManager.clearTokens();
                    }
                }
                
                response.close();
            } catch (IOException e) {
                Log.e(TAG, "Error refreshing token: " + e.getMessage(), e);
            }
            
            return null;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            Log.e(TAG, "Thread interrupted while waiting for refresh token lock", e);
            return null;
        } finally {
            // Always release the lock if we acquired it
            if (lockAcquired) {
                refreshTokenLock.unlock();
            }
        }
    }
}
