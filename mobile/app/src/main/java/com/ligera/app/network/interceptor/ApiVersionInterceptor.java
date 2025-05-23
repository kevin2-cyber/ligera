package com.ligera.app.network.interceptor;

import androidx.annotation.NonNull;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Interceptor to handle API versioning
 */
public class ApiVersionInterceptor implements Interceptor {
    private final String apiVersion;
    
    public ApiVersionInterceptor(String apiVersion) {
        this.apiVersion = apiVersion;
    }
    
    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request originalRequest = chain.request();
        
        // Get the original URL
        HttpUrl originalUrl = originalRequest.url();
        
        // Check if the URL already contains the version path segment
        if (originalUrl.toString().contains("/api/" + apiVersion + "/")) {
            return chain.proceed(originalRequest);
        }
        
        // Add API version to the URL path if it's not already included
        HttpUrl newUrl = originalUrl.newBuilder()
                .removePathSegment(0) // Remove "api" if present
                .addPathSegment("api")
                .addPathSegment(apiVersion)
                .build();
        
        // Build a new request with the versioned URL
        Request.Builder builder = originalRequest.newBuilder()
                .url(newUrl)
                .header("Content-Type", "application/json")
                .header("Accept", "application/json");
                
        // Proceed with modified request
        return chain.proceed(builder.build());
    }
}

