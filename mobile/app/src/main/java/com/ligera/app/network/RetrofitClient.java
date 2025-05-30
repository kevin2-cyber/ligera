package com.ligera.app.network;

import androidx.annotation.NonNull;
import androidx.collection.LruCache;

import com.ligera.app.network.interceptor.ApiVersionInterceptor;
import com.ligera.app.network.interceptor.AuthInterceptor;
import com.ligera.app.network.interceptor.ErrorHandlingInterceptor;
import com.ligera.app.network.interceptor.NetworkConnectionInterceptor;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;

/**
 * Singleton class for configuring and providing Retrofit client instances
 */
public class RetrofitClient {
    private static final String TAG = "RetrofitClient";
    private static final int MAX_RETRY_COUNT = 3;
    private static final int MEMORY_CACHE_SIZE = 10 * 1024 * 1024; // 10MB
    
    // Singleton instance
    private static RetrofitClient instance;
    
    // Retrofit instances for different API versions
    private Retrofit retrofitV1;
    private Retrofit retrofitV2;
    
    // Memory cache for API responses
    private final LruCache<String, Object> responseCache;
    
    // Dependencies
    private final TokenManager tokenManager;
    private final NetworkConnectionInterceptor networkConnectionInterceptor;
    private final OkHttpClient.Builder httpClientBuilder;
    
    private RetrofitClient(TokenManager tokenManager, NetworkConnectionInterceptor networkConnectionInterceptor) {
        this.tokenManager = tokenManager;
        this.networkConnectionInterceptor = networkConnectionInterceptor;
        
        // Initialize the memory cache for API responses
        responseCache = new LruCache<String, Object>(MEMORY_CACHE_SIZE);
        
        // Create logging interceptor based on configuration
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        if (ApiConfig.LOGGING_ENABLED) {
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        } else {
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.NONE);
        }
        
        // Create auth interceptor
        AuthInterceptor authInterceptor = new AuthInterceptor(tokenManager);
        
        // Create error handling interceptor
        ErrorHandlingInterceptor errorHandlingInterceptor = new ErrorHandlingInterceptor();
        
        // Base HTTP client builder with common configurations
        httpClientBuilder = new OkHttpClient.Builder()
                .connectTimeout(ApiConfig.DEFAULT_CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(ApiConfig.DEFAULT_READ_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(ApiConfig.DEFAULT_WRITE_TIMEOUT, TimeUnit.SECONDS)
                .addInterceptor(networkConnectionInterceptor)
                .add                .retryOnConnectionFailure(true);
    }
    
    /**
     * Determines if an exception is retryable
     * 
     * @param e The exception to check
     * @return true if the request should be retried, false otherwise
     */
    private boolean isRetryable(IOException e) {
        // Connection timeouts, socket timeouts, and certain other network errors are retryable
        String message = e.getMessage();
        if (message == null) return false;
        
        return message.contains("timeout") 
                || message.contains("connection")
                || message.contains("refused")
                || message.contains("reset")
                || message.contains("unreachable");
    }
    
    /**
     * Add an item to the memory cache
     * 
     * @param key Cache key
     * @param value Object to cache
     */
    public void addToCache(String key, Object value) {
        if (key != null && value != null) {
            responseCache.put(key, value);
            Timber.d("Added item to cache with key: %s", key);
        }
    }
    
    /**
     * Get an item from the memory cache
     * 
     * @param key Cache key
     * @return Cached object or null if not found
     */
    public Object getFromCache(String key) {
        Object value = responseCache.get(key);
        if (value != null) {
            Timber.d("Cache hit for key: %s", key);
        } else {
            Timber.d("Cache miss for key: %s", key);
        }
        return value;
    }
    
    /**
     * Remove an item from the memory cache
     * 
     * @param key Cache key
     */
    public void removeFromCache(String key) {
        responseCache.remove(key);
        Timber.d("Removed item from cache with key: %s", key);
    }
    
    /**
     * Clear the entire memory cache
     */
    public void clearCache() {
        responseCache.evictAll();
        Timber.d("Cache cleared");
    }
    
    /**
     * Get singleton instance of RetrofitClient
     * 
     * @param tokenManager token manager instance
     * @param networkConnectionInterceptor network connection interceptor
     * @return RetrofitClient instance
     */
    public static synchronized RetrofitClient getInstance(
            @NonNull TokenManager tokenManager,
            @NonNull NetworkConnectionInterceptor networkConnectionInterceptor) {
        if (instance == null) {
            instance = new RetrofitClient(tokenManager, networkConnectionInterceptor);
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
            ApiVersionInterceptor versionInterceptor = new ApiVersionInterceptor(ApiConfig.API_VERSION_V1);
            
            // Build HTTP client with v1 version interceptor
            OkHttpClient httpClient = httpClientBuilder
                    .addInterceptor(versionInterceptor)
                    .connectTimeout(ApiConfig.AUTH_TIMEOUT, TimeUnit.SECONDS)
                    .readTimeout(ApiConfig.AUTH_TIMEOUT, TimeUnit.SECONDS)
                    .writeTimeout(ApiConfig.AUTH_TIMEOUT, TimeUnit.SECONDS)
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
    
    /**
     * Get Retrofit client for API v2
     * 
     * @return Retrofit instance configured for API v2
     */
    public Retrofit getClientV2() {
        if (retrofitV2 == null) {
            // Create API version interceptor for v2
            ApiVersionInterceptor versionInterceptor = new ApiVersionInterceptor(ApiConfig.API_VERSION_V2);
            
            // Build HTTP client with v2 version interceptor
            OkHttpClient httpClient = httpClientBuilder
                    .addInterceptor(versionInterceptor)
                    .connectTimeout(ApiConfig.V2_CONNECT_TIMEOUT, TimeUnit.SECONDS)
                    .readTimeout(ApiConfig.V2_READ_TIMEOUT, TimeUnit.SECONDS)
                    .writeTimeout(ApiConfig.V2_WRITE_TIMEOUT, TimeUnit.SECONDS)
                    .build();
            
            // Build Retrofit instance
            retrofitV2 = new Retrofit.Builder()
                    .baseUrl(ApiConfig.V2_BASE_URL)
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
    
    /**
     * Get a client with custom timeout settings for specific API needs
     * 
     * @param apiVersion API version to use
     * @param connectTimeout connect timeout in seconds
     * @param readTimeout read timeout in seconds
     * @param writeTimeout write timeout in seconds
     * @return Retrofit instance with custom timeout settings
     */
    public Retrofit getClientWithCustomTimeouts(String apiVersion, int connectTimeout, int readTimeout, int writeTimeout) {
        // Create API version interceptor
        ApiVersionInterceptor versionInterceptor = new ApiVersionInterceptor(apiVersion);
        
        // Build HTTP client with custom timeouts
        OkHttpClient httpClient = httpClientBuilder
                .addInterceptor(versionInterceptor)
                .connectTimeout(connectTimeout, TimeUnit.SECONDS)
                .readTimeout(readTimeout, TimeUnit.SECONDS)
                .writeTimeout(writeTimeout, TimeUnit.SECONDS)
                .build();
        
        // Build and return a new Retrofit instance
        return new Retrofit.Builder()
                .baseUrl(ApiConfig.BASE_URL)
                .client(httpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(new LiveDataCallAdapterFactory())
                .build();
    }
    
    /**
     * Get a client configured specifically for product-related API operations
     * 
     * @return Retrofit instance optimized for product operations
     */
    public Retrofit getProductClient() {
        return getClientWithCustomTimeouts(
                ApiConfig.CURRENT_API_VERSION, 
                ApiConfig.DEFAULT_CONNECT_TIMEOUT,
                ApiConfig.PRODUCT_LIST_TIMEOUT,
                ApiConfig.DEFAULT_WRITE_TIMEOUT);
    }
    
    /**
     * Get a client configured specifically for cart/checkout operations
     * 
     * @return Retrofit instance optimized for cart operations
     */
    public Retrofit getCartClient() {
        return getClientWithCustomTimeouts(
                ApiConfig.CURRENT_API_VERSION,
                ApiConfig.DEFAULT_CONNECT_TIMEOUT,
                ApiConfig.CART_TIMEOUT,
                ApiConfig.CART_TIMEOUT);
    }
    
    /**
     * Get a client configured specifically for user profile operations
     * 
     * @return Retrofit instance optimized for profile operations
     */
    public Retrofit getProfileClient() {
        return getClientWithCustomTimeouts(
                ApiConfig.CURRENT_API_VERSION,
                ApiConfig.DEFAULT_CONNECT_TIMEOUT,
                ApiConfig.PROFILE_TIMEOUT,
                ApiConfig.DEFAULT_WRITE_TIMEOUT);
    }
    
    /**
     * Get a client configured specifically for order-related operations
     * 
     * @return Retrofit instance optimized for order operations
     */
    public Retrofit getOrderClient() {
        return getClientWithCustomTimeouts(
                ApiConfig.CURRENT_API_VERSION,
                ApiConfig.DEFAULT_CONNECT_TIMEOUT,
                ApiConfig.ORDER_TIMEOUT,
                ApiConfig.DEFAULT_WRITE_TIMEOUT);
    }
}

