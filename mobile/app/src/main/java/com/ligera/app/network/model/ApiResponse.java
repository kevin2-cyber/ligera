package com.ligera.app.network.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.IOException;

import retrofit2.Response;

/**
 * A generic class that contains data and status about loading this data.
 * Wrapper for Retrofit response handling.
 */
public class ApiResponse<T> {
    public final int code;
    @Nullable
    public final T body;
    @Nullable
    public final String errorMessage;

    /**
     * Create ApiResponse from successful Retrofit response
     */
    public ApiResponse(@NonNull Response<T> response) {
        code = response.code();
        if (response.isSuccessful()) {
            body = response.body();
            errorMessage = null;
        } else {
            String message = null;
            try {
                message = response.errorBody().string();
            } catch (IOException ignored) {
                // Ignore parsing error
            }
            
            if (message == null || message.trim().isEmpty()) {
                message = response.message();
            }
            
            errorMessage = message;
            body = null;
        }
    }

    /**
     * Create ApiResponse for a network error
     */
    public ApiResponse(Throwable error) {
        code = 500;
        body = null;
        errorMessage = error.getMessage();
    }

    /**
     * Check if the response is successful
     */
    public boolean isSuccessful() {
        return code >= 200 && code < 300;
    }
}

