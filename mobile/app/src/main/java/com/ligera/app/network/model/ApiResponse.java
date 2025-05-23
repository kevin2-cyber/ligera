package com.ligera.app.network.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ligera.app.network.ApiConfig;

import java.io.IOException;
import java.util.Objects;

import retrofit2.Response;
import timber.log.Timber;

/**
 * Common class used by API responses to handle success and error cases.
 * @param <T> the type of the response object
 */
public class ApiResponse<T> {
    public final int code;
    @Nullable
    public final T body;
    @Nullable
    public final String errorMessage;
    
    private ApiResponse(int code, @Nullable T body, @Nullable String errorMessage) {
        this.code = code;
        this.body = body;
        this.errorMessage = errorMessage;
    }
    
    public boolean isSuccessful() {
        return code >= 200 && code < 300;
    }
    
    /**
     * Create success response from Retrofit Response
     */
    public static <T> ApiResponse<T> create(@NonNull Response<T> response) {
        if (response.isSuccessful()) {
            T body = response.body();
            return new ApiResponse<>(
                    response.code(),
                    body,
                    null
            );
        } else {
            String errorMsg;
            try {
                errorMsg = response.errorBody() != null ? response.errorBody().string() : null;
            } catch (IOException e) {
                errorMsg = response.message();
                Timber.e(e, "Error while parsing response error body");
            }
            
            if (errorMsg == null || errorMsg.trim().isEmpty()) {
                errorMsg = getErrorMessageForCode(response.code());
            }
            
            return new ApiResponse<>(
                    response.code(),
                    null,
                    errorMsg);
        }
    }
    
    /**
     * Create error response from a Throwable
     */
    public static <T> ApiResponse<T> create(Throwable error) {
        Timber.e(error, "Network call failed");
        return new ApiResponse<>(
                ApiConfig.ErrorCodes.NETWORK_ERROR,
                null,
                error.getMessage() != null ? error.getMessage() : ApiConfig.ErrorMessages.UNKNOWN_ERROR);
    }
    
    /**
     * Get appropriate error message based on HTTP status code
     */
    private static String getErrorMessageForCode(int code) {
        switch (code) {
            case ApiConfig.ErrorCodes.BAD_REQUEST:
                return "Bad request";
            case ApiConfig.ErrorCodes.UNAUTHORIZED:
                return ApiConfig.ErrorMessages.AUTHENTICATION_FAILED;
            case ApiConfig.ErrorCodes.FORBIDDEN:
                return "Access denied";
            case ApiConfig.ErrorCodes.NOT_FOUND:
                return "Resource not found";
            case ApiConfig.ErrorCodes.TIMEOUT:
                return ApiConfig.ErrorMessages.TIMEOUT;
            case ApiConfig.ErrorCodes.INTERNAL_SERVER_ERROR:
            case ApiConfig.ErrorCodes.BAD_GATEWAY:
            case ApiConfig.ErrorCodes.SERVICE_UNAVAILABLE:
            case ApiConfig.ErrorCodes.GATEWAY_TIMEOUT:
                return ApiConfig.ErrorMessages.SERVER_ERROR;
            default:
                return ApiConfig.ErrorMessages.UNKNOWN_ERROR;
        }
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ApiResponse<?> that = (ApiResponse<?>) o;
        return code ==

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

