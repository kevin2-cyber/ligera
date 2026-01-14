package com.ligera.app.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.ligera.app.model.database.AppDatabase;
import com.ligera.app.model.entity.Category;
import com.ligera.app.model.entity.Product;
import com.ligera.app.repository.ProductRepository;
import com.ligera.app.util.Resource;
import com.ligera.app.viewmodel.state.ProductState;

import java.util.List;

/**
 * ViewModel for product-related operations (Local database only, no RxJava/Paging)
 */
public class ProductViewModel extends AndroidViewModel {
    private static final String TAG = "ProductViewModel";

    private final ProductRepository repository;

    // LiveData for different product states
    private final MediatorLiveData<ProductState> productState = new MediatorLiveData<>();
    private final MutableLiveData<String> searchQuery = new MutableLiveData<>();
    private final MutableLiveData<Long> selectedProductId = new MutableLiveData<>();
    private final MutableLiveData<Long> selectedCategoryId = new MutableLiveData<>(-1L);

    // Products LiveData that changes based on selected category
    private final LiveData<List<Product>> products;

    // LiveData sources for observing repository results
    private LiveData<Resource<Product>> productDetailsSource;
    private LiveData<Resource<List<Product>>> featuredProductsSource;
    private LiveData<Resource<List<Product>>> popularProductsSource;
    private LiveData<Resource<List<Category>>> categoriesSource;

    public ProductViewModel(@NonNull Application application) {
        super(application);

        // Initialize repository - Local database only
        AppDatabase database = AppDatabase.getInstance(application);
        repository = new ProductRepository(database);

        // Set initial state
        productState.setValue(ProductState.loading());

        // Use Transformations.switchMap to automatically switch data source
        products = Transformations.switchMap(selectedCategoryId, categoryId -> {
            if (categoryId == null || categoryId == -1L) {
                return repository.getAllProductsSimple();
            } else {
                return repository.getProductsByCategorySimple(categoryId);
            }
        });
    }

    public void init(@NonNull LifecycleOwner owner) {
        // Load initial data
        loadFeaturedProducts(8);
        loadPopularProducts(8);
        loadCategories();

        // Observe products and update state
        products.observe(owner, productList -> {
            productState.setValue(new ProductState.Builder()
                    .loading(false)
                    .products(productList)
                    .build());
        });

        // Set up selected product observer
        selectedProductId.observe(owner, id -> {
            if (id != null && id > 0) {
                loadProductDetails(id);
            }
        });

        // Set up selected category observer
        selectedCategoryId.observe(owner, id -> {
            // Products will automatically update via switchMap
        });
    }

    // Public methods for UI interactions

    /**
     * Get product state
     */
    public LiveData<ProductState> getProductState() {
        return productState;
    }

    /**
     * Get products LiveData
     */
    public LiveData<List<Product>> getProductsLiveData() {
        return products;
    }

    /**
     * Load all products (resets category to -1)
     */
    public void loadProducts() {
        selectedCategoryId.setValue(-1L);
    }

    /**
     * Load featured products
     */
    public void loadFeaturedProducts(int limit) {
        if (featuredProductsSource != null) {
            productState.removeSource(featuredProductsSource);
        }

        featuredProductsSource = repository.getFeaturedProducts(limit);

        productState.addSource(featuredProductsSource, resource -> {
            if (resource.status == Resource.Status.SUCCESS) {
                ProductState current = productState.getValue();
                productState.setValue(new ProductState.Builder()
                        .loading(false)
                        .featuredProducts(resource.data)
                        .products(current != null ? current.getProducts() : null)
                        .build());
            }
        });
    }

    /**
     * Load popular products
     */
    public void loadPopularProducts(int limit) {
        if (popularProductsSource != null) {
            productState.removeSource(popularProductsSource);
        }

        popularProductsSource = repository.getPopularProducts(limit);

        productState.addSource(popularProductsSource, resource -> {
            if (resource.status == Resource.Status.SUCCESS) {
                ProductState current = productState.getValue();
                productState.setValue(new ProductState.Builder()
                        .loading(false)
                        .popularProducts(resource.data)
                        .products(current != null ? current.getProducts() : null)
                        .build());
            }
        });
    }

    /**
     * Load product details
     */
    public void loadProductDetails(long productId) {
        if (productDetailsSource != null) {
            productState.removeSource(productDetailsSource);
        }

        productState.setValue(new ProductState.Builder().loading(true).build());

        productDetailsSource = repository.getProductById(productId);

        productState.addSource(productDetailsSource, resource -> {
            ProductState.Builder newState = new ProductState.Builder();
            switch (resource.status) {
                case LOADING:
                    newState.loading(true);
                    break;
                case SUCCESS:
                    newState.loading(false).productDetails(resource.data);
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
     * Search products (searches locally in database)
     */
    public void searchProducts(@NonNull String query) {
        searchQuery.setValue(query);
        // For now, just reload all products - search can be implemented later
        selectedCategoryId.setValue(-1L);
    }

    /**
     * Clear search
     */
    public void clearSearch() {
        searchQuery.setValue(null);
        selectedCategoryId.setValue(-1L);
    }

    /**
     * Load categories
     */
    public void loadCategories() {
        if (categoriesSource != null) {
            productState.removeSource(categoriesSource);
        }

        categoriesSource = repository.getCategories();

        productState.addSource(categoriesSource, resource -> {
            if (resource.status == Resource.Status.SUCCESS) {
                ProductState current = productState.getValue();
                productState.setValue(new ProductState.Builder()
                        .loading(false)
                        .categories(resource.data)
                        .products(current != null ? current.getProducts() : null)
                        .build());
            }
        });
    }

    /**
     * Load products by category
     */
    public void loadProductsByCategory(long categoryId) {
        selectedCategoryId.setValue(categoryId);
    }

    /**
     * Refresh data
     */
    public void refreshData() {
        loadFeaturedProducts(8);
        loadPopularProducts(8);
        loadCategories();
        // Trigger products reload
        Long currentCategory = selectedCategoryId.getValue();
        selectedCategoryId.setValue(currentCategory != null ? currentCategory : -1L);
    }

    /**
     * Retry last operation
     */
    public void retry() {
        refreshData();
    }

    /**
     * Clean up resources
     */
    @Override
    protected void onCleared() {
        super.onCleared();

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
    }
}
