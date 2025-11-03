package com.ligera.app.network;

import com.ligera.app.BuildConfig;

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

    // Error codes
    public static final class ErrorCodes {
        public static final int BAD_REQUEST = 400;
        public static final int UNAUTHORIZED = 401;
        public static final int FORBIDDEN = 403;
        public static final int NOT_FOUND = 404;
        public static final int TIMEOUT = 408;
        public static final int INTERNAL_SERVER_ERROR = 500;
        public static final int BAD_GATEWAY = 502;
        public static final int SERVICE_UNAVAILABLE = 503;
        public static final int GATEWAY_TIMEOUT = 504;
        public static final int NETWORK_ERROR = -1; // Custom code for network-related errors
    }

    // Error messages
    public static final class ErrorMessages {
        public static final String AUTHENTICATION_FAILED = "Authentication failed. Please check your credentials.";
        public static final String TIMEOUT = "The connection has timed out. Please try again.";
        public static final String SERVER_ERROR = "We're having some trouble on our end. Please try again later.";
        public static final String UNKNOWN_ERROR = "An unknown error occurred.";
        public static final String NO_NETWORK = "No network connection. Please check your settings.";
        public static final String CONNECTION_TIMEOUT = "Could not connect to the server. Please check your internet connection.";
        public static final String WEAK_CONNECTION = "Your connection is too weak to perform this action.";
        public static final String NETWORK_ERROR = "A network error occurred. Please try again.";
        public static final String MOBILE_DATA_ERROR = "An error occurred while using mobile data. Please try again.";
        public static final String WIFI_ERROR = "An error occurred on your Wi-Fi network. Please try again.";
    }
}
