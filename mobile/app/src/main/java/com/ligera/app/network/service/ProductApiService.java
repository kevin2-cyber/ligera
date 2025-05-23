package com.ligera.app.network.service;

import androidx.lifecycle.LiveData;

import com.ligera.app.network.ApiConfig;
import com.ligera.app.network.model.ApiResponse;
import com.ligera.app.network.model.request.ProductFilterRequest;
import com.ligera.app.network.model.response.CategoryResponse;
import com.ligera.app.network.model.response.ProductListResponse;
import com.ligera.app.network.model.response.ProductResponse;

import java.util.List;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * API service interface for product-related operations
 */
public interface ProductApiService {
    
    /**
     * Get a paginated list of products with optional filtering
     * 
     * @param page page number (0-based)
     * @param size page size
     * @param categoryId optional category ID filter
     * @param query optional search query
     * @param sortBy optional sort field
     * @param sortDirection optional sort direction ("asc" or "desc")
     * @return paginated product list response
     */
    @Headers({
        "Content-Type: application/json",
        "Accept: application/json"
    })
    @GET(ApiConfig.Endpoints.PRODUCTS)
    LiveData<ApiResponse<ProductListResponse>> getProducts(
            @Query("page") int page,
            @Query("size") int size,
            @Query("categoryId") Long categoryId,
            @Query("query") String query,
            @Query("sortBy") String sortBy,
            @Query("sortDirection") String sortDirection
    );
    
    /**
     * Get a product by ID
     * 
     * @param id product ID
     * @return product response
     */
    @Headers({
        "Content-Type: application/json",
        "Accept: application/json"
    })
    @GET(ApiConfig.Endpoints.PRODUCT_DETAIL)
    LiveData<ApiResponse<ProductResponse>> getProductById(@Path("id") long id);
    
    /**
     * Get featured products
     * 
     * @param limit maximum number of products to return
     * @return list of featured products
     */
    @Headers({
        "Content-Type: application/json",
        "Accept: application/json"
    })
    @GET(ApiConfig.Endpoints.FEATURED_PRODUCTS)
    LiveData<ApiResponse<List<ProductResponse>>> getFeaturedProducts(@Query("limit") int limit);
    
    /**
     * Get popular products
     * 
     * @param limit maximum number of products to return
     * @return list of popular products
     */
    @Headers({
        "Content-Type: application/json",
        "Accept: application/json"
    })
    @GET(ApiConfig.Endpoints.POPULAR_PRODUCTS)
    LiveData<ApiResponse<List<ProductResponse>>> getPopularProducts(@Query("limit") int limit);
    
    /**
     * Get products with advanced filtering
     * 
     * @param page page number (0-based)
     * @param size page size
     * @param request filter criteria
     * @return paginated product list response
     */
    @Headers({
        "Content-Type: application/json",
        "Accept: application/json"
    })
    @POST(ApiConfig.Endpoints.PRODUCTS + "/filter")
    LiveData<ApiResponse<ProductListResponse>> filterProducts(
            @Query("page") int page,
            @Query("size") int size,
            @Body ProductFilterRequest request
    );
    
    /**
     * Get all product categories
     * 
     * @return list of categories
     */
    @Headers({
        "Content-Type: application/json",
        "Accept: application/json"
    })
    @GET("categories")
    LiveData<ApiResponse<List<CategoryResponse>>> getCategories();
    
    /**
     * Get a category by ID
     * 
     * @param id category ID
     * @return category response
     */
    @Headers({
        "Content-Type: application/json",
        "Accept: application/json"
    })
    @GET("categories/{id}")
    LiveData<ApiResponse<CategoryResponse>> getCategoryById(@Path("id") long id);
    
    /**
     * Get products by category ID
     * 
     * @param categoryId category ID
     * @param page page number (0-based)
     * @param size page size
     * @return paginated product list response
     */
    @Headers({
        "Content-Type: application/json",
        "Accept: application/json"
    })
    @GET("categories/{categoryId}/products")
    LiveData<ApiResponse<ProductListResponse>> getProductsByCategory(
            @Path("categoryId") long categoryId,
            @Query("page") int page,
            @Query("size") int size
    );
    
    /**
     * Search products by keyword
     * 
     * @param query search query
     * @param page page number (0-based)
     * @param size page size
     * @return paginated product list response
     */
    @Headers({
        "Content-Type: application/json",
        "Accept: application/json"
    })
    @GET(ApiConfig.Endpoints.PRODUCTS + "/search")
    LiveData<ApiResponse<ProductListResponse>> searchProducts(
            @Query("query") String query,
            @Query("page") int page,
            @Query("size") int size
    );
}

