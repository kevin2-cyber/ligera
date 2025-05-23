package com.ligera.app.network;

import androidx.annotation.NonNull;

import com.ligera.app.network.interceptor.AuthInterceptor;
import com.ligera.app.network.interceptor.ApiVersionInterceptor;
import com.ligera.app.network.LiveDataCallAdapterFactory;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Singleton class for configuring and providing Retrofit client instances
 */
public class RetrofitClient {
    private static final String TAG = "RetrofitClient";
    
    // Singleton instance
    private static RetrofitClient instance;
    
    // Retrofit instances for different API versions
    private Retrofit retrofitV1;
    private Retrofit retrofitV2;
    
    // Dependencies
    private final TokenManager tokenManager;
    private final OkHttpClient.Builder httpClientBuilder;
    
    private RetrofitClient(TokenManager tokenManager) {
        this.tokenManager = tokenManager;
        
        // Create logging interceptor
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        
        // Create auth interceptor
        AuthInterceptor authInterceptor = new AuthInterceptor(tokenManager);
        
        // Base HTTP client builder with common configurations
        httpClientBuilder = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(loggingInterceptor)
                .addInterceptor(authInterceptor);
    }
    
    /**
     * Get singleton instance of RetrofitClient
     * 
     * @param tokenManager token manager instance
     * @return RetrofitClient instance
     */
    public static synchronized RetrofitClient getInstance(@NonNull TokenManager tokenManager) {
        if (instance == null) {
            instance = new RetrofitClient(tokenManager);
        }
        return instance;
    }
    
    /**
     * Get Retrofit client for API v1
     * 
     * @return Retrofit instance configured for API v1
     */
    public Retrofit getClientV1() {
        if (retrofitV1 == null) {
            // Create API version interceptor for v1
            ApiVersionInterceptor versionInterceptor = new ApiVersionInterceptor("v1");
            
            // Build HTTP client with v1 version interceptor
            OkHttpClient httpClient = httpClientBuilder
                    .addInterceptor(versionInterceptor)
                    .build();
            
            // Build Retrofit instance
            retrofitV1 = new Retrofit.Builder()
                    .baseUrl(ApiConfig.BASE_URL)
                    .client(httpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(new LiveDataCallAdapterFactory())
                    .build();
        }
        return retrofitV1;
    }
    
    /**
     * Get Retrofit client for API v2
     * 
     * @return Retrofit instance configured for API v2
     */
    public Retrofit getClientV2() {
        if (retrofitV2 == null) {
            // Create API version interceptor for v2
            ApiVersionInterceptor versionInterceptor = new ApiVersionInterceptor("v2");
            
            // Build HTTP client with v2 version interceptor
            OkHttpClient httpClient = httpClientBuilder
                    .addInterceptor(versionInterceptor)
                    .build();
            
            // Build Retrofit instance
            retrofitV2 = new Retrofit.Builder()
                    .baseUrl(ApiConfig.BASE_URL)
                    .client(httpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(new LiveDataCallAdapterFactory())
                    .build();
        }
        return retrofitV2;
    }
    
    /**
     * Reset the Retrofit instances to force creation of new instances
     * Useful after token changes or other configuration updates
     */
    public void resetClients() {
        retrofitV1 = null;
        retrofitV2 = null;
    }
}

