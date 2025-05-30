package com.ligera.app.viewmodel.state;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.paging.PagedList;

import com.ligera.app.db.entity.CategoryEntity;
import com.ligera.app.db.entity.ProductEntity;
import com.ligera.app.network.model.response.ProductListResponse;

import java.util.Collections;
import java.util.List;

/**
 * Immutable class representing the UI state for product-related screens.
 * This class handles different states (loading, success, error, empty) and
 * stores the relevant data for each state.
 */
public class ProductState {
    
    /**
     * Enum representing the status of the current UI state
     */
    public enum Status {
        LOADING,    // Data is being loaded
        SUCCESS,    // Data has been successfully loaded
        ERROR,      // An error occurred while loading data
        EMPTY       // No data is available
    }
    
    /**
     * Type of content currently being displayed
     */
    public enum ContentType {
        PRODUCT_LIST,       // List of all products
        FEATURED_PRODUCTS,  // Featured products
        POPULAR_PRODUCTS,   // Popular products
        PRODUCT_DETAILS,    // Details of a specific product
        CATEGORIES,         // List of categories
        SEARCH_RESULTS,     // Search results
        FILTER_RESULTS      // Filter results
    }
    
    @NonNull
    private final Status status;
    
    @NonNull
    private final ContentType contentType;
    
    @Nullable
    private final PagedList<ProductEntity> products;
    
    @Nullable
    private final List<ProductEntity> featuredProducts;
    
    @Nullable
    private final List<ProductEntity> popularProducts;
    
    @Nullable
    private final ProductEntity productDetails;
    
    @Nullable
    private final List<CategoryEntity> categories;
    
    @Nullable
    private final ProductListResponse filterResults;
    
    @Nullable
    private final String errorMessage;
    
    @Nullable
    private final Throwable error;
    
    @Nullable
    private final String searchQuery;
    
    private final boolean isRefreshing;
    
    /**
     * Private constructor to enforce the use of factory methods
     */
    private ProductState(
            @NonNull Status status,
            @NonNull ContentType contentType,
            @Nullable PagedList<ProductEntity> products,
            @Nullable List<ProductEntity> featuredProducts,
            @Nullable List<ProductEntity> popularProducts,
            @Nullable ProductEntity productDetails,
            @Nullable List<CategoryEntity> categories,
            @Nullable ProductListResponse filterResults,
            @Nullable String errorMessage,
            @Nullable Throwable error,
            @Nullable String searchQuery,
            boolean isRefreshing) {
        this.status = status;
        this.contentType = contentType;
        this.products = products;
        this.featuredProducts = featuredProducts;
        this.popularProducts = popularProducts;
        this.productDetails = productDetails;
        this.categories = categories;
        this.filterResults = filterResults;
        this.errorMessage = errorMessage;
        this.error = error;
        this.searchQuery = searchQuery;
        this.isRefreshing = isRefreshing;
    }
    
    //-------------------------------------------------------------------------------------------
    // Factory methods for creating different states
    //-------------------------------------------------------------------------------------------
    
    /**
     * Create a loading state for initial load
     *
     * @return A new loading state
     */
    public static ProductState loading() {
        return new ProductState(
                Status.LOADING,
                ContentType.PRODUCT_LIST,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                false
        );
    }
    
    /**
     * Create a loading state for a specific content type
     *
     * @param contentType Type of content being loaded
     * @return A new loading state for the specified content type
     */
    public static ProductState loading(ContentType contentType) {
        return new ProductState(
                Status.LOADING,
                contentType,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                false
        );
    }
    
    /**
     * Create a refreshing state based on current state
     *
     * @param currentState Current state
     * @return A new refreshing state preserving current data
     */
    public static ProductState refreshing(ProductState currentState) {
        return new ProductState(
                currentState.status,
                currentState.contentType,
                currentState.products,
                currentState.featuredProducts,
                currentState.popularProducts,
                currentState.productDetails,
                currentState.categories,
                currentState.filterResults,
                null,
                null,
                currentState.searchQuery,
                true
        );
    }
    
    /**
     * Create a success state for product list
     *
     * @param products List of products
     * @return A new success state for product list
     */
    public static ProductState successProducts(PagedList<ProductEntity> products) {
        boolean isEmpty = products == null || products.isEmpty();
        return new ProductState(
                isEmpty ? Status.EMPTY : Status.SUCCESS,
                ContentType.PRODUCT_LIST,
                products,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                false
        );
    }
    
    /**
     * Create a success state for featured products
     *
     * @param featuredProducts List of featured products
     * @return A new success state for featured products
     */
    public static ProductState successFeaturedProducts(List<ProductEntity> featuredProducts) {
        boolean isEmpty = featuredProducts == null || featuredProducts.isEmpty();
        return new ProductState(
                isEmpty ? Status.EMPTY : Status.SUCCESS,
                ContentType.FEATURED_PRODUCTS,
                null,
                featuredProducts,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                false
        );
    }
    
    /**
     * Create a success state for popular products
     *
     * @param popularProducts List of popular products
     * @return A new success state for popular products
     */
    public static ProductState successPopularProducts(List<ProductEntity> popularProducts) {
        boolean isEmpty = popularProducts == null || popularProducts.isEmpty();
        return new ProductState(
                isEmpty ? Status.EMPTY : Status.SUCCESS,
                ContentType.POPULAR_PRODUCTS,
                null,
                null,
                popularProducts,
                null,
                null,
                null,
                null,
                null,
                null,
                false
        );
    }
    
    /**
     * Create a success state for product details
     *
     * @param productDetails Product details
     * @return A new success state for product details
     */
    public static ProductState successProductDetails(ProductEntity productDetails) {
        return new ProductState(
                productDetails == null ? Status.EMPTY : Status.SUCCESS,
                ContentType.PRODUCT_DETAILS,
                null,
                null,
                null,
                productDetails,
                null,
                null,
                null,
                null,
                null,
                false
        );
    }
    
    /**
     * Create a success state for categories
     *
     * @param categories List of categories
     * @return A new success state for categories
     */
    public static ProductState successCategories(List<CategoryEntity> categories) {
        boolean isEmpty = categories == null || categories.isEmpty();
        return new ProductState(
                isEmpty ? Status.EMPTY : Status.SUCCESS,
                ContentType.CATEGORIES,
                null,
                null,
                null,
                null,
                categories,
                null,
                null,
                null,
                null,
                false
        );
    }
    
    /**
     * Create a success state for filter results
     *
     * @param filterResults Filter results
     * @param searchQuery Search query used for filtering
     * @return A new success state for filter results
     */
    public static ProductState successFilterResults(ProductListResponse filterResults, String searchQuery) {
        boolean isEmpty = filterResults == null || 
                          filterResults.getProducts() == null || 
                          filterResults.getProducts().isEmpty();
        return new ProductState(
                isEmpty ? Status.EMPTY : Status.SUCCESS,
                ContentType.FILTER_RESULTS,
                null,
                null,
                null,
                null,
                null,
                filterResults,
                null,
                null,
                searchQuery,
                false
        );
    }
    
    /**
     * Create a success state for search results
     *
     * @param products Search results
     * @param searchQuery Search query
     * @return A new success state for search results
     */
    public static ProductState successSearchResults(PagedList<ProductEntity> products, String searchQuery) {
        boolean isEmpty = products == null || products.isEmpty();
        return new ProductState(
                isEmpty ? Status.EMPTY : Status.SUCCESS,
                ContentType.SEARCH_RESULTS,
                products,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                searchQuery,
                false
        );
    }
    
    /**
     * Create an error state with an error message
     *
     * @param errorMessage Error message
     * @return A new error state
     */
    public static ProductState error(String errorMessage) {
        return new ProductState(
                Status.ERROR,
                ContentType.PRODUCT_LIST, // Default content type
                null,
                null,
                null,
                null,
                null,
                null,
                errorMessage,
                null,
                null,
                false
        );
    }
    
    /**
     * Create an error state with an error message and throwable
     *
     * @param errorMessage Error message
     * @param error Error throwable
     * @return A new error state
     */
    public static ProductState error(String errorMessage, Throwable error) {
        return new ProductState(
                Status.ERROR,
                ContentType.PRODUCT_LIST, // Default content type
                null,
                null,
                null,
                null,
                null,
                null,
                errorMessage,
                error,
                null,
                false
        );
    }
    
    /**
     * Create an error state for a specific content type
     *
     * @param errorMessage Error message
     * @param error Error throwable
     * @param contentType Type of content that failed to load
     * @return A new error state for the specified content type
     */
    public static ProductState error(String errorMessage, Throwable error, ContentType contentType) {
        return new ProductState(
                Status.ERROR,
                contentType,
                null,
                null,
                null,
                null,
                null,
                null,
                errorMessage,
                error,
                null,
                false
        );
    }
    
    /**
     * Create an empty state for a specific content type
     *
     * @param contentType Type of content that is empty
     * @return A new empty state for the specified content type
     */
    public static ProductState empty(ContentType contentType) {
        return new ProductState(
                Status.EMPTY,
                contentType,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                false
        );
    }
    
    //-------------------------------------------------------------------------------------------
    // Getters and helper methods
    //-------------------------------------------------------------------------------------------
    
    /**
     * Get the current status
     *
     * @return The current status
     */
    @NonNull
    public Status getStatus() {
        return status;
    }
    
    /**
     * Get the current content type
     *
     * @return The current content type
     */
    @NonNull
    public ContentType getContentType() {
        return contentType;
    }
    
    /**
     * Get the product list
     *
     * @return The product list or null if not available
     */
    @Nullable
    public PagedList<ProductEntity> getProducts() {
        return products;
    }
    
    /**
     * Get featured products
     *
     * @return List of featured products or empty list if not available
     */
    @NonNull
    public List<ProductEntity> getFeaturedProducts() {
        return featuredProducts != null ? featuredProducts : Collections.emptyList();
    }
    
    /**
     * Get popular products
     *
     * @return List of popular products or empty list if not available
     */
    @NonNull
    public List<ProductEntity> getPopularProducts() {
        return popularProducts != null ? popularProducts : Collections.emptyList();
    }
    
    /**
     * Get product details
     *
     * @return Product details or null if not available
     */
    @Nullable
    public ProductEntity getProductDetails() {
        return productDetails;
    }
    
    /**
     * Get categories
     *
     * @return List of categories or empty list if not available
     */
    @NonNull
    public List<CategoryEntity> getCategories() {
        return categories != null ? categories : Collections.emptyList();
    }
    
    /**
     * Get filter results
     *
     * @return Filter results or null if not available
     */
    @Nullable
    public ProductListResponse getFilterResults() {
        return filterResults;
    }
    
    /**
     * Get error message
     *
     * @return Error message or null if not available
     */
    @Nullable
    public String getErrorMessage() {
        return errorMessage;
    }
    
    /**
     * Get error throwable
     *
     * @return Error throwable or null if not available
     */
    @Nullable
    public Throwable getError() {
        return error;
    }
    
    /**
     * Get search query
     *
     * @return Search query or null if not available
     */
    @Nullable
    public String getSearchQuery() {
        return searchQuery;
    }
    
    /**
     * Check if the state is refreshing
     *
     * @return True if refreshing, false otherwise
     */
    public boolean isRefreshing() {
        return isRefreshing;
    }
    
    /**
     * Check if the state is loading or refreshing
     *
     * @return True if loading or refreshing, false otherwise
     */
    public boolean isLoading() {
        return status == Status.LOADING || isRefreshing;
    }
    
    /**
     * Check if the state is success
     *
     * @return True if success, false otherwise
     */
    public boolean isSuccess() {
        return status == Status.SUCCESS;
    }
    
    /**
     * Check if the state is error
     *
     * @return True if error, false otherwise
     */
    public boolean isError() {
        return status == Status.ERROR;
    }
    
    /**
     * Check if the state is empty
     *
     * @return True if empty, false otherwise
     */
    public boolean isEmpty() {
        return status == Status.EMPTY;
    }

package com.ligera.app.viewmodel.state;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.paging.PagedList;

import com.ligera.app.db.entity.CategoryEntity;
import com.ligera.app.db.entity.ProductEntity;
import com.ligera.app.network.model.response.ProductListResponse;

import java.util.Collections;
import java.util.List;

/**
 * State class for product-related UI states
 */
public class ProductState {
    // Status of the current operation
    public enum Status {
        LOADING,
        SUCCESS,
        ERROR,
        EMPTY
    }
    
    @NonNull
    private final Status status;
    
    @Nullable
    private final PagedList<ProductEntity> products;
    
    @Nullable
    private final List<ProductEntity> featuredProducts;
    
    @Nullable
    private final List<ProductEntity> popularProducts;
    
    @Nullable
    private final ProductEntity productDetails;
    
    @Nullable
    private final List<CategoryEntity> categories;
    
    @Nullable
    private final ProductListResponse filterResults;
    
    @Nullable
    private final String errorMessage;
    
    @Nullable
    private final Throwable error;
    
    @Nullable
    private final String searchQuery;
    
    private final boolean isRefreshing;
    
    private ProductState(
            @NonNull Status status,
            @Nullable PagedList<ProductEntity> products,
            @Nullable List<ProductEntity> featuredProducts,
            @Nullable List<ProductEntity> popularProducts,
            @Nullable ProductEntity productDetails,
            @Nullable List<CategoryEntity> categories,
            @Nullable ProductListResponse filterResults,
            @Nullable String errorMessage,
            @Nullable Throwable error,
            @Nullable String searchQuery,
            boolean isRefreshing) {
        this.status = status;
        this.products = products;
        this.featuredProducts = featuredProducts;
        this.popularProducts = popularProducts;
        this.productDetails = productDetails;
        this.categories = categories;
        this.filterResults = filterResults;
        this.errorMessage = errorMessage;
        this.error = error;
        this.searchQuery = searchQuery;
        this.isRefreshing = isRefreshing;
    }
    
    // Factory methods for creating different states
    
    /**
     * Create a loading state
     *
     * @return A new loading state
     */
    public static ProductState loading() {
        return new ProductState(
                Status.LOADING,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                false
        );
    }
    
    /**
     * Create a refreshing state based on current state
     *
     * @param currentState Current state
     * @return A new refreshing state
     */
    public static ProductState refreshing(ProductState currentState) {
        return new ProductState(
                currentState.status,
                currentState.products,
                currentState.featuredProducts,
                currentState.popularProducts,
                currentState.productDetails,
                currentState.categories,
                currentState.filterResults,
                null,
                null,
                currentState.searchQuery,
                true
        );
    }
    
    /**
     * Create a success state for product list
     *
     * @param products List of products
     * @return A new success state
     */
    public static ProductState successProducts(PagedList<ProductEntity> products) {
        boolean isEmpty = products == null || products.isEmpty();
        return new ProductState(
                isEmpty ? Status.EMPTY : Status.SUCCESS,
                products,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                false
        );
    }
    
    /**
     * Create a success state for featured products
     *
     * @param featuredProducts List of featured products
     * @return A new success state
     */
    public static ProductState successFeaturedProducts(List<ProductEntity> featuredProducts) {
        boolean isEmpty = featuredProducts == null || featuredProducts.isEmpty();
        return new ProductState(
                isEmpty ? Status.EMPTY : Status.SUCCESS,
                null,
                featuredProducts,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                false
        );
    }
    
    /**
     * Create a success state for popular products
     *
     * @param popularProducts List of popular products
     * @return A new success state
     */
    public static ProductState successPopularProducts(List<ProductEntity> popularProducts) {
        boolean isEmpty = popularProducts == null || popularProducts.isEmpty();
        return new ProductState(
                isEmpty ? Status.EMPTY : Status.SUCCESS,
                null,
                null,
                popularProducts,
                null,
                null,
                null,
                null,
                null,
                null,
                false
        );
    }
    
    /**
     * Create a success state for product details
     *
     * @param productDetails Product details
     * @return A new success state
     */
    public static ProductState successProductDetails(ProductEntity productDetails) {
        return new ProductState(
                Status.SUCCESS,
                null,
                null,
                null,
                productDetails,
                null,
                null,
                null,
                null,
                null,
                false
        );
    }
    
    /**
     * Create a success state for categories
     *
     * @param categories List of categories
     * @return A new success state
     */
    public static ProductState successCategories(List<CategoryEntity> categories) {
        boolean isEmpty = categories == null || categories.isEmpty();
        return new ProductState(
                isEmpty ? Status.EMPTY : Status.SUCCESS,
                null,
                null,
                null,
                null,
                categories,
                null,
                null,
                null,
                null,
                false
        );
    }
    
    /**
     * Create a success state for filter results
     *
     * @param filterResults Filter results
     * @param searchQuery Search query
     * @return A new success state
     */
    public static ProductState successFilterResults(ProductListResponse filterResults, String searchQuery) {
        boolean isEmpty = filterResults == null || filterResults.getProducts() == null || filterResults.getProducts().isEmpty();
        return new ProductState(
                isEmpty ? Status.EMPTY : Status.SUCCESS,
                null,
                null,
                null,
                null,
                null,
                filterResults,
                null,
                null,
                searchQuery,
                false
        );
    }
    
    /**
     * Create an error state
     *
     * @param errorMessage Error message
     * @param error Error throwable
     * @return A new error state
     */
    public static ProductState error(String errorMessage, Throwable error) {
        return new ProductState(
                Status.ERROR,
                null,
                null,
                null,
                null,
                null,
                null,
                errorMessage,
                error,
                null,
                false
        );
    }
    
    // Getters
    
    @NonNull
    public Status getStatus() {
        return status;
    }
    
    @Nullable
    public PagedList<ProductEntity> getProducts() {
        return products;
    }
    
    @Nullable
    public List<ProductEntity> getFeaturedProducts() {
        return featuredProducts != null ? featuredProducts : Collections.emptyList();
    }
    
    @Nullable
    public List<ProductEntity> getPopularProducts() {
        return popularProducts != null ? popularProducts : Collections.emptyList();
    }
    
    @Nullable
    public ProductEntity getProductDetails() {
        return productDetails;
    }
    
    @Nullable
    public List<CategoryEntity> getCategories() {
        return categories != null ? categories : Collections.emptyList();
    }
    
    @Nullable
    public ProductListResponse getFilterResults() {
        return filterResults;
    }
    
    @Nullable
    public String getErrorMessage() {
        return errorMessage;
    }
    
    @Nullable
    public Throwable getError() {
        return error;
    }
    
    @Nullable
    public String getSearchQuery() {
        return searchQuery;
    }
    
    public boolean isRefreshing() {
        return isRefreshing;
    }
    
    public boolean isLoading() {
        return status == Status.LOADING || isRefreshing;
    }
    
    public boolean isSuccess() {
        return status == Status.SUCCESS;
    }
    
    public boolean isError() {
        return status == Status.ERROR;
    }
    
    public boolean isEmpty() {
        return status == Status.EMPTY;
    }
}

