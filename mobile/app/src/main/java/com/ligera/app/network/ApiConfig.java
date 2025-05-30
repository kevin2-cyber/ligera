package com.ligera.app.network;

/**
 * Configuration constants for API
 * Using BuildConfig for environment-specific settings
 */
public class ApiConfig {
    // API version constants
    public static final String API_VERSION_V1 = "v1";
    public static final String API_VERSION_V2 = "v2";
    public static final String CURRENT_API_VERSION = API_VERSION_V2;
    
    // Base URL for API - using BuildConfig for environment-specific settings
    public static final String BASE_URL = BuildConfig.API_BASE_URL;
    
    // Timeout configurations (in seconds)
    public static final int DEFAULT_CONNECT_TIMEOUT = 30;
    public static final int DEFAULT_READ_TIMEOUT = 30;
    public static final int DEFAULT_WRITE_TIMEOUT = 30;
    
    // Endpoint-specific timeout configurations (in seconds)
    public static final int AUTH_TIMEOUT = 15;           // Auth requests should be fast
    public static final int PRODUCT_LIST_TIMEOUT = 30;   // Product lists might be larger
    public static final int PRODUCT_DETAIL_TIMEOUT = 20;
    public static final int CART_TIMEOUT = 20;
    public static final int ORDER_TIMEOUT = 25;          // Orders can take longer to process
    public static final int PROFILE_TIMEOUT = 20;
    
    // Retry configuration
    public static final int MAX_RETRIES = BuildConfig.DEBUG_MODE ? 3 : 1;
    public static final long RETRY_DELAY_MS = 1000;      // 1 second
    public static final float RETRY_BACKOFF_FACTOR = 1.5f;
    
    // Logging configuration - enabled only in debug mode
    public static final boolean LOGGING_ENABLED = BuildConfig.DEBUG_MODE;
    
    // API endpoints
    public static final class Endpoints {
        // Auth endpoints
        public static final String LOGIN = "auth/login";
        public static final String REGISTER = "auth/register";
        public static final String CHANGE_PASSWORD = "auth/change-password";
        public static final String REFRESH_TOKEN = "auth/refresh"; // New endpoint for token refresh
        
        // User endpoints
        public static final String USER_PROFILE = "users/profile";
        public static final String UPDATE_PROFILE = "users/profile";
        
        // Product endpoints
        public static final String PRODUCTS = "products";
        public static final String PRODUCT_DETAIL = "products/{id}";
        public static final String FEATURED_PRODUCTS = "products/featured";
        public static final String POPULAR_PRODUCTS = "products/popular";
        
        // Cart endpoints
        public static final String CART = "cart";
        public static final String ADD_TO_CART = "cart/add";
        public static final String REMOVE_FROM_CART = "cart/{id}";
        public static final String UPDATE_CART_ITEM = "cart/{id}";
        
        // Orders endpoints
        public static final String ORDERS = "orders";
        public static final String ORDER_DETAIL = "orders/{id}";
        public static final String CREATE_ORDER = "orders";
    }
    
}
    
    // Common HTTP headers
    

