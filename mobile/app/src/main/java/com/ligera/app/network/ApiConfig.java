package com.ligera.app.network;

/**
 * Configuration constants for API
 */
public class ApiConfig {
    // Base URL for API
    public static final String BASE_URL = "https://api.ligera.com/";
    
    // API endpoints
    public static final class Endpoints {
        // Auth endpoints
        public static final String LOGIN = "auth/login";
        public static final String REGISTER = "auth/register";
        public static final String CHANGE_PASSWORD = "auth/change-password";
        
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
    
    // HTTP error codes
    public static final class ErrorCodes {
        public static final int BAD_REQUEST = 400;
        public static final int UNAUTHORIZED = 401;
        public static final int FORBIDDEN = 403;
        public static final int NOT_FOUND = 404;
        public static final int INTERNAL_SERVER_ERROR = 500;
    }
    
    // Common HTTP headers

