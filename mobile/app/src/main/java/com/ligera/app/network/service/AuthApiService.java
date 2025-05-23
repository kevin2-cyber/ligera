package com.ligera.app.network.service;

import androidx.lifecycle.LiveData;

import com.ligera.app.network.ApiConfig;
import com.ligera.app.network.model.ApiResponse;
import com.ligera.app.network.model.request.LoginRequest;
import com.ligera.app.network.model.request.PasswordChangeRequest;
import com.ligera.app.network.model.request.RegisterRequest;
import com.ligera.app.network.model.response.AuthResponse;

import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * API service interface for authentication endpoints
 */
public interface AuthApiService {
    
    /**
     * Login user with email and password
     * 
     * @param request login credentials
     * @return authentication response with JWT token
     */
    @Headers({
        "Content-Type: application/json",
        "Accept: application/json"
    })
    @POST(ApiConfig.Endpoints.LOGIN)
    LiveData<ApiResponse<AuthResponse>> login(@Body LoginRequest request);
    
    /**
     * Register new user
     * 
     * @param request registration data
     * @return authentication response with JWT token
     */
    @Headers({
        "Content-Type: application/json",
        "Accept: application/json"
    })
    @POST(ApiConfig.Endpoints.REGISTER)
    LiveData<ApiResponse<AuthResponse>> register(@Body RegisterRequest request);
    
    /**
     * Change user password
     * 
     * @param request password change data
     * @return authentication response
     */
    @Headers({
        "Content-Type: application/json",
        "Accept: application/json"
    })
    @POST(ApiConfig.Endpoints.CHANGE_PASSWORD)
    LiveData<ApiResponse<AuthResponse>> changePassword(@Body PasswordChangeRequest request);
}

