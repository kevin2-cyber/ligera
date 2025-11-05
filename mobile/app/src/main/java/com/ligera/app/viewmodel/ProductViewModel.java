package com.ligera.app.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.LiveDataReactiveStreams;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelKt;
import androidx.paging.PagingData;
import androidx.paging.rxjava3.PagingRx;

import com.ligera.app.model.database.AppDatabase;
import com.ligera.app.model.entity.Category;
import com.ligera.app.model.entity.Product;
import com.ligera.app.network.RetrofitClient;
import com.ligera.app.network.TokenManager;
import com.ligera.app.network.interceptor.NetworkConnectionInterceptor;
import com.ligera.app.network.model.request.ProductFilterRequest;
import com.ligera.app.network.model.response.ProductListResponse;
import com.ligera.app.network.service.ProductApiService;
import com.ligera.app.repository.ProductRepository;
import com.ligera.app.util.Resource;
import com.ligera.app.viewmodel.state.ProductState;

import java.util.List;

import io.reactivex.rxjava3.core.Flowable;
import kotlinx.coroutines.CoroutineScope;

/**
 * ViewModel for product-related operations
 */
public class ProductViewModel extends AndroidViewModel {
    private static final String TAG = "ProductViewModel";

    private final ProductRepository repository;

    // LiveData for different product states
    private final MediatorLiveData<ProductState> productState = new MediatorLiveData<>();
    private final MutableLiveData<String> searchQuery = new MutableLiveData<>();
    private final MutableLiveData<ProductFilterRequest> filterRequest = new MutableLiveData<>();
    private final MutableLiveData<Long> selectedProductId = new MutableLiveData<>();
    private final MutableLiveData<Long> selectedCategoryId = new MutableLiveData<>();

    // LiveData sources for observing repository results
    private LiveData<PagingData<Product>> productsSource;
    private LiveData<Resource<Product>> productDetailsSource;
    private LiveData<Resource<List<Product>>> featuredProductsSource;
    private LiveData<Resource<List<Product>>> popularProductsSource;
    private LiveData<Resource<List<Category>>> categoriesSource;
    private LiveData<PagingData<Product>> searchResultsSource;
    private LiveData<Resource<ProductListResponse>> filterResultsSource;

    public ProductViewModel(@NonNull Application application) {
        super(application);

        // Initialize repository
        AppDatabase database = AppDatabase.getInstance(application);
        TokenManager tokenManager = TokenManager.getInstance(application);
        ProductApiService apiService = RetrofitClient.getInstance(tokenManager, new NetworkConnectionInterceptor(application)).getClientV1().create(ProductApiService.class);
        repository = new ProductRepository(database, apiService);

        // Set initial state
        productState.setValue(ProductState.loading());
    }

    public void init(@NonNull LifecycleOwner owner) {
        // Load initial data
        loadProducts();
        loadFeaturedProducts(8);
        loadPopularProducts(8);
        loadCategories();

        // Set up search query observer
        searchQuery.observe(owner, query -> {
            if (query != null && !query.isEmpty()) {
                searchProducts(query);
            }
        });

        // Set up filter request observer
        filterRequest.observe(owner, request -> {
            if (request != null) {
                filterProducts(request, 0, 20);
            }
        });

        // Set up selected product observer
        selectedProductId.observe(owner, id -> {
            if (id != null && id > 0) {
                loadProductDetails(id);
            }
        });

        // Set up selected category observer
        selectedCategoryId.observe(owner, id -> {
            if (id != null && id > 0) {
                loadProductsByCategory(id);
            }
        });
    }

    // Public methods for UI interactions

    /**
     * Get product state
     *
     * @return LiveData of product state
     */
    public LiveData<ProductState> getProductState() {
        return productState;
    }

    /**
     * Load all products
     */
    public void loadProducts() {
        // Remove previous source
        if (productsSource != null) {
            productState.removeSource(productsSource);
        }

        // Set loading state
        productState.setValue(new ProductState.Builder().loading(true).build());

        // Get products from repository
        CoroutineScope viewModelScope = ViewModelKt.getViewModelScope(this);
        Flowable<PagingData<Product>> productsFlowable = repository.getProducts();
        productsSource = LiveDataReactiveStreams.fromPublisher(PagingRx.cachedIn(productsFlowable, viewModelScope));

        // Observe products
        productState.addSource(productsSource, pagingData -> productState.setValue(new ProductState.Builder().loading(false).products(pagingData).build()));
    }

    /**
     * Load featured products
     *
     * @param limit Maximum number of products to return
     */
    public void loadFeaturedProducts(int limit) {
        // Remove previous source
        if (featuredProductsSource != null) {
            productState.removeSource(featuredProductsSource);
        }

        // Get featured products from repository
        featuredProductsSource = repository.getFeaturedProducts(limit);

        // Observe featured products
        productState.addSource(featuredProductsSource, resource -> {
            ProductState.Builder newState = new ProductState.Builder();
            switch (resource.status) {
                case LOADING:
                    // Don't update state to loading to avoid UI flicker
                    break;
                case SUCCESS:
                    newState.featuredProducts(resource.data);
                    break;
                case ERROR:
                    Log.e(TAG, "Error loading featured products: " + resource.message);
                    // Don't update state to error to avoid disrupting the UI
                    break;
                case OFFLINE:
                    // Don't update state to offline to avoid disrupting the UI
                    break;
            }
            productState.setValue(newState.build());
        });
    }

    /**
     * Load popular products
     *
     * @param limit Maximum number of products to return
     */
    public void loadPopularProducts(int limit) {
        // Remove previous source
        if (popularProductsSource != null) {
            productState.removeSource(popularProductsSource);
        }

        // Get popular products from repository
        popularProductsSource = repository.getPopularProducts(limit);

        // Observe popular products
        productState.addSource(popularProductsSource, resource -> {
            ProductState.Builder newState = new ProductState.Builder();
            switch (resource.status) {
                case LOADING:
                    // Don't update state to loading to avoid UI flicker
                    break;
                case SUCCESS:
                    newState.popularProducts(resource.data);
                    break;
                case ERROR:
                    Log.e(TAG, "Error loading popular products: " + resource.message);
                    // Don't update state to error to avoid disrupting the UI
                    break;
                case OFFLINE:
                    // Don't update state to offline to avoid disrupting the UI
                    break;
            }
            productState.setValue(newState.build());
        });
    }

    /**
     * Load product details
     *
     * @param productId Product ID
     */
    public void loadProductDetails(long productId) {
        // Remove previous source
        if (productDetailsSource != null) {
            productState.removeSource(productDetailsSource);
        }

        // Set loading state
        productState.setValue(new ProductState.Builder().loading(true).build());

        // Get product details from repository
        productDetailsSource = repository.getProductById(productId);

        // Observe product details
        productState.addSource(productDetailsSource, resource -> {
            ProductState.Builder newState = new ProductState.Builder();
            switch (resource.status) {
                case LOADING:
                    newState.loading(true);
                    break;
                case SUCCESS:
                    if (resource.data != null) {
                        newState.loading(false).productDetails(resource.data);
                    } else {
                        newState.loading(false).error("Product not found");
                    }
                    break;
                case ERROR:
                    newState.loading(false).error(resource.message);
                    break;
                case OFFLINE:
                    newState.loading(false).offline(true);
                    break;
            }
            productState.setValue(newState.build());
        });
    }

    /**
     * Search products
     *
     * @param query Search query
     */
    public void searchProducts(@NonNull String query) {
        // Set query
        searchQuery.setValue(query);

        // Remove previous source
        if (searchResultsSource != null) {
            productState.removeSource(searchResultsSource);
        }

        // Set loading state
        productState.setValue(new ProductState.Builder().loading(true).build());

        // Get search results from repository
        CoroutineScope viewModelScope = ViewModelKt.getViewModelScope(this);
        Flowable<PagingData<Product>> productsFlowable = repository.searchProducts(query);
        searchResultsSource = LiveDataReactiveStreams.fromPublisher(PagingRx.cachedIn(productsFlowable, viewModelScope));

        // Observe search results
        productState.addSource(searchResultsSource, pagingData -> productState.setValue(new ProductState.Builder().loading(false).products(pagingData).searchQuery(query).build()));
    }

    /**
     * Clear search
     */
    public void clearSearch() {
        // Clear search query
        searchQuery.setValue(null);

        // Remove search results source
        if (searchResultsSource != null) {
            productState.removeSource(searchResultsSource);
        }

        // Load all products
        loadProducts();
    }

    /**
     * Filter products
     *
     * @param request Filter criteria
     * @param page Page number
     * @param size Page size
     */
    public void filterProducts(@NonNull ProductFilterRequest request, int page, int size) {
        // Set filter request
        filterRequest.setValue(request);

        // Remove previous source
        if (filterResultsSource != null) {
            productState.removeSource(filterResultsSource);
        }

        // Set loading state
        productState.setValue(new ProductState.Builder().loading(true).build());

        // Get filter results from repository
        filterResultsSource = repository.filterProducts(request, page, size);

        // Observe filter results
        productState.addSource(filterResultsSource, resource -> {
            ProductState.Builder newState = new ProductState.Builder();
            switch (resource.status) {
                case LOADING:
                    newState.loading(true);
                    break;
                case SUCCESS:
                    if (resource.data != null) {
                        newState.loading(false).filterResults(resource.data);
                    } else {
                        newState.loading(false).filterResults(null);
                    }
                    break;
                case ERROR:
                    newState.loading(false).error(resource.message);
                    break;
                case OFFLINE:
                    newState.loading(false).offline(true);
                    break;
            }
            productState.setValue(newState.build());
        });
    }

    /**
     * Reset filters
     */
    public void resetFilters() {
        // Clear filter request
        filterRequest.setValue(null);

        // Remove filter results source
        if (filterResultsSource != null) {
            productState.removeSource(filterResultsSource);
        }

        // Load all products
        loadProducts();
    }

    /**
     * Load categories
     */
    public void loadCategories() {
        // Remove previous source
        if (categoriesSource != null) {
            productState.removeSource(categoriesSource);
        }

        // Get categories from repository
        categoriesSource = repository.getCategories();

        // Observe categories
        productState.addSource(categoriesSource, resource -> {
            ProductState.Builder newState = new ProductState.Builder();
            switch (resource.status) {
                case LOADING:
                    // Don't update state to loading to avoid UI flicker
                    break;
                case SUCCESS:
                    newState.categories(resource.data);
                    break;
                case ERROR:
                    Log.e(TAG, "Error loading categories: " + resource.message);
                    // Don't update state to error to avoid disrupting the UI
                    break;
                case OFFLINE:
                    // Don't update state to offline to avoid disrupting the UI
                    break;
            }
            productState.setValue(newState.build());
        });
    }

    /**
     * Load products by category
     *
     * @param categoryId Category ID
     */
    public void loadProductsByCategory(long categoryId) {
        // Set selected category
        selectedCategoryId.setValue(categoryId);

        // Build filter request for category
        ProductFilterRequest request = new ProductFilterRequest.Builder()
                .categoryId(categoryId)
                .build();

        // Filter products by category
        filterProducts(request, 0, 20);
    }

    /**
     * Refresh data
     */
    public void refreshData() {
        // Get current state
        ProductState currentState = productState.getValue();
        if (currentState == null) {
            // If no current state, load initial data
            loadProducts();
            loadFeaturedProducts(8);
            loadPopularProducts(8);
            loadCategories();
            return;
        }

        // Set refreshing state
        productState.setValue(new ProductState.Builder().refreshing(true).build());

        // Refresh data based on current state
        if (currentState.getProductDetails() != null) {
            // Refresh product details
            loadProductDetails(currentState.getProductDetails().getId());
        } else if (currentState.getFilterResults() != null) {
            // Refresh filter results
            ProductFilterRequest request = filterRequest.getValue();
            if (request != null) {
                filterProducts(request, 0, 20);
            } else {
                loadProducts();
            }
        } else if (currentState.getSearchQuery() != null) {
            // Refresh search results
            searchProducts(currentState.getSearchQuery());
        } else {
            // Refresh all products
            loadProducts();
            loadFeaturedProducts(8);
            loadPopularProducts(8);
            loadCategories();
        }
    }

    /**
     * Retry last operation
     */
    public void retry() {
        // Get current state
        ProductState currentState = productState.getValue();
        if (currentState == null || currentState.getError() == null) {
            // If no current state or not in error state, do nothing
            return;
        }

        // Retry based on error context
        refreshData();
    }

    /**
     * Clean up resources
     */
    @Override
    protected void onCleared() {
        super.onCleared();

        // Remove all sources
        if (productsSource != null) {
            productState.removeSource(productsSource);
        }
        if (productDetailsSource != null) {
            productState.removeSource(productDetailsSource);
        }
        if (featuredProductsSource != null) {
            productState.removeSource(featuredProductsSource);
        }
        if (popularProductsSource != null) {
            productState.removeSource(popularProductsSource);
        }
        if (categoriesSource != null) {
            productState.removeSource(categoriesSource);
        }
        if (searchResultsSource != null) {
            productState.removeSource(searchResultsSource);
        }
        if (filterResultsSource != null) {
            productState.removeSource(filterResultsSource);
        }
    }
}
