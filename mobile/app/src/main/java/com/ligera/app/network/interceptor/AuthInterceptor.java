package com.ligera.app.network.interceptor;

import androidx.annotation.NonNull;

import com.ligera.app.network.TokenManager;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Interceptor to add JWT authorization header to requests
 */
public class AuthInterceptor implements Interceptor {
    private final TokenManager tokenManager;
    
    public AuthInterceptor(TokenManager tokenManager) {
        this.tokenManager = tokenManager;
    }
    
    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request originalRequest = chain.request();
        
        // Skip authentication for auth endpoints (login, register)
        if (originalRequest.url().toString().contains("/auth/")) {
            return chain.proceed(originalRequest);
        }
        
        // Get JWT token from token manager
        String token = tokenManager.getToken();
        
        // If token is null or empty, proceed with original request
        if (token == null || token.isEmpty()) {
            return chain.proceed(originalRequest);
        }
        
        // Add JWT token to request header
        Request.Builder builder = originalRequest.newBuilder()
                .header("Authorization", "Bearer " + token)
                .method(originalRequest.method(), originalRequest.body());
                
        // Proceed with modified request
        return chain.proceed(builder.build());
    }
}

