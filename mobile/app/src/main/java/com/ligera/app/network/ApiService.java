package com.ligera.app.network;

import androidx.lifecycle.LiveData;

import com.ligera.app.network.model.ApiResponse;
import com.ligera.app.network.model.response.CategoryResponse;
import com.ligera.app.network.model.response.ProductResponse;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Retrofit API service definition for backend communication
 */
public interface ApiService {
    
    /**
     * Get all categories
     */
    @GET("api/v1/categories")
    LiveData<ApiResponse<List<CategoryResponse>>> getCategories();
    
    /**
     * Get all products from a specific category
     */
    @GET("api/v1/categories/{categoryId}/products")
    LiveData<ApiResponse<List<ProductResponse>>> getProductsByCategory(@Path("categoryId") int categoryId);
    
    /**
     * Get a specific product by ID
     */
    @GET("api/v1/products/{productId}")
    LiveData<ApiResponse<ProductResponse>> getProductById(@Path("productId") long productId);
    
    /**
     * Get featured products
     */
    @GET("api/v1/products/featured")
    LiveData<ApiResponse<List<ProductResponse>>> getFeaturedProducts();
    
    /**
     * Search for products
     */
    @GET("api/v1/products/search")
    LiveData<ApiResponse<List<ProductResponse>>> searchProducts(@Query("query") String query);
}

