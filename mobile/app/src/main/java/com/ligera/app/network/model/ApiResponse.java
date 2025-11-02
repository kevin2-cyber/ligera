package com.ligera.app.network.model;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ligera.app.network.ApiConfig;

import java.io.IOException;
import java.util.Objects;

import retrofit2.Response;

/**
 * Common class used by API responses to handle success and error cases.
 * @param <T> the type of the response object
 */
public class ApiResponse<T> {
    private static final String TAG = "ApiResponse";

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
            return new ApiResponse<>(response.code(), response.body(), null);
        } else {
            String errorMsg;
            try {
                errorMsg = response.errorBody() != null ? response.errorBody().string() : null;
            } catch (IOException e) {
                Log.e(TAG, "Error while parsing response error body", e);
                errorMsg = response.message();
            }

            if (errorMsg == null || errorMsg.trim().isEmpty()) {
                errorMsg = getErrorMessageForCode(response.code());
            }

            return new ApiResponse<>(response.code(), null, errorMsg);
        }
    }

    /**
     * Create error response from a Throwable
     */
    public static <T> ApiResponse<T> create(Throwable error) {
        Log.e(TAG, "Network call failed", error);
        return new ApiResponse<>(
                ApiConfig.ErrorCodes.NETWORK_ERROR,
                null,
                error.getMessage() != null ? error.getMessage() : ApiConfig.ErrorMessages.UNKNOWN_ERROR);
    }

    /**
     * Get appropriate error message based on HTTP status code
     */
    private static String getErrorMessageForCode(int code) {
        return switch (code) {
            case ApiConfig.ErrorCodes.BAD_REQUEST -> "Bad request";
            case ApiConfig.ErrorCodes.UNAUTHORIZED -> ApiConfig.ErrorMessages.AUTHENTICATION_FAILED;
            case ApiConfig.ErrorCodes.FORBIDDEN -> "Access denied";
            case ApiConfig.ErrorCodes.NOT_FOUND -> "Resource not found";
            case ApiConfig.ErrorCodes.TIMEOUT -> ApiConfig.ErrorMessages.TIMEOUT;
            case ApiConfig.ErrorCodes.INTERNAL_SERVER_ERROR, ApiConfig.ErrorCodes.BAD_GATEWAY,
                 ApiConfig.ErrorCodes.SERVICE_UNAVAILABLE, ApiConfig.ErrorCodes.GATEWAY_TIMEOUT ->
                    ApiConfig.ErrorMessages.SERVER_ERROR;
            default -> ApiConfig.ErrorMessages.UNKNOWN_ERROR;
        };
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ApiResponse<?> that = (ApiResponse<?>) o;
        return code == that.code &&
                Objects.equals(body, that.body) &&
                Objects.equals(errorMessage, that.errorMessage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, body, errorMessage);
    }

    @NonNull
    @Override
    public String toString() {
        return "ApiResponse{" +
                "code=" + code +
                ", body=" + body +
                ", errorMessage='" + errorMessage + '\'' +
                '}';
    }
}
