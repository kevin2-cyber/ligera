package com.ligera.app.repository;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.paging.DataSource;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

import com.ligera.app.db.AppDatabase;
import com.ligera.app.db.dao.CategoryDao;
import com.ligera.app.db.dao.ProductDao;
import com.ligera.app.db.entity.CategoryEntity;
import com.ligera.app.db.entity.ProductEntity;
import com.ligera.app.network.model.ApiResponse;
import com.ligera.app.network.model.request.ProductFilterRequest;
import com.ligera.app.network.model.response.CategoryResponse;
import com.ligera.app.network.model.response.ProductListResponse;
import com.ligera.app.network.model.response.ProductResponse;
import com.ligera.app.network.service.ProductApiService;
import com.ligera.app.repository.mapper.CategoryMapper;
import com.ligera.app.repository.mapper.ProductMapper;
import com.ligera.app.repository.util.NetworkBoundResource;
import com.ligera.app.repository.util.Resource;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Repository for product-related operations
 */
public class ProductRepository {
    private static final String TAG = "ProductRepository";
    
    // Cache timeout in milliseconds (24 hours)
    private static final long CACHE_TIMEOUT = TimeUnit.HOURS.toMillis(24);
    
    // Page size for pagination
    private static final int PAGE_SIZE = 20;
    
    // Dependencies
    private final ProductDao productDao;
    private final CategoryDao categoryDao;
    private final ProductApiService productApiService;
    
    /**
     * Constructor
     *
     * @param database Room database
     * @param productApiService Product API service
     */
    public ProductRepository(AppDatabase database, ProductApiService productApiService) {
        this.productDao = database.productDao();
        this.categoryDao = database.categoryDao();
        this.productApiService = productApiService;
    }
    
    /**
     * Get a product by ID
     *
     * @param productId Product ID
     * @return LiveData of Resource of ProductEntity
     */
    public LiveData<Resource<ProductEntity>> getProductById(final long productId) {
        return new NetworkBoundResource<ProductEntity, ProductResponse>() {
            @Override
            protected boolean shouldFetch(@Nullable ProductEntity data) {
                return data == null || isDataStale(data.getLastRefreshed());
            }

            @Override
            protected void saveCallResult(@NonNull ProductResponse item) {
                ProductEntity entity = ProductMapper.mapResponseToEntity(item);
                AppDatabase.databaseWriteExecutor.execute(() -> productDao.insert(entity));
            }

            @NonNull
            @Override
            protected LiveData<ProductEntity> loadFromDb() {
                return productDao.getProductById(productId);
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<ProductResponse>> createCall() {
                return productApiService.getProductById(productId);
            }

            @Override
            protected void onFetchFailed() {
                Log.e(TAG, "Failed to fetch product with ID: " + productId);
            }
        }.asLiveData();
    }

    /**
     * Get all products with pagination
     *
     * @return LiveData of PagedList of ProductEntity
     */
    public LiveData<Resource<PagedList<ProductEntity>>> getProducts() {
        // Configure paging
        PagedList.Config config = new PagedList.Config.Builder()
                .setPageSize(PAGE_SIZE)
                .setInitialLoadSizeHint(PAGE_SIZE * 2)
                .setEnablePlaceholders(true)
                .build();

        // Create data source factory
        DataSource.Factory<Integer, ProductEntity> factory = productDao.getAllProducts();

        // Build paged list
        LiveData<PagedList<ProductEntity>> pagedList = new LivePagedListBuilder<>(factory, config).build();

        // Trigger network load
        fetchProducts(0, PAGE_SIZE * 2);

        // Create resource with paged list
        MediatorLiveData<Resource<PagedList<ProductEntity>>> result = new MediatorLiveData<>();
        result.addSource(pagedList, data -> {
            if (data != null && !data.isEmpty()) {
                result.setValue(Resource.success(data));
            } else {
                result.setValue(Resource.loading(data));
            }
        });

        return result;
    }

    /**
     * Fetch products from network and save to database
     *
     * @param page Page number
     * @param size Page size
     */
    private void fetchProducts(int page, int size) {
        productApiService.getProducts(page, size, null, null, null, null)
                .observeForever(response -> {
                    if (response != null && response.isSuccessful() && response.body != null) {
                        List<ProductEntity> entities = ProductMapper.mapResponseToEntity(response.body.getProducts());
                        AppDatabase.databaseWriteExecutor.execute(() -> productDao.insertAll(entities));
                    }
                });
    }

    /**
     * Get featured products
     *
     * @param limit Maximum number of products to return
     * @return LiveData of Resource of List of ProductEntity
     */
    public LiveData<Resource<List<ProductEntity>>> getFeaturedProducts(final int limit) {
        return new NetworkBoundResource<List<ProductEntity>, List<ProductResponse>>() {
            @Override
            protected boolean shouldFetch(@Nullable List<ProductEntity> data) {
                return data == null || data.isEmpty() || isDataStale(getLastRefreshTime(data));
            }

            @Override
            protected void saveCallResult(@NonNull List<ProductResponse> items) {
                List<ProductEntity> entities = ProductMapper.mapResponseToEntity(items);
                AppDatabase.databaseWriteExecutor.execute(() -> productDao.insertAll(entities));
            }

            @NonNull
            @Override
            protected LiveData<List<ProductEntity>> loadFromDb() {
                return productDao.getFeaturedProducts(limit);
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<List<ProductResponse>>> createCall() {
                return productApiService.getFeaturedProducts(limit);
            }

            @Override
            protected void onFetchFailed() {
                Log.e(TAG, "Failed to fetch featured products");
            }
        }.asLiveData();
    }

    /**
     * Get popular products
     *
     * @param limit Maximum number of products to return
     * @return LiveData of Resource of List of ProductEntity
     */
    public LiveData<Resource<List<ProductEntity>>> getPopularProducts(final int limit) {
        return new NetworkBoundResource<List<ProductEntity>, List<ProductResponse>>() {
            @Override
            protected boolean shouldFetch(@Nullable List<ProductEntity> data) {
                return data == null || data.isEmpty() || isDataStale(getLastRefreshTime(data));
            }

            @Override
            protected void saveCallResult(@NonNull List<ProductResponse> items) {
                List<ProductEntity> entities = ProductMapper.mapResponseToEntity(items);
                AppDatabase.databaseWriteExecutor.execute(() -> productDao.insertAll(entities));
            }

            @NonNull
            @Override
            protected LiveData<List<ProductEntity>> loadFromDb() {
                return productDao.getPopularProducts(limit);
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<List<ProductResponse>>> createCall() {
                return productApiService.getPopularProducts(limit);
            }

            @Override
            protected void onFetchFailed() {
                Log.e(TAG, "Failed to fetch popular products");
            }
        }.asLiveData();
    }

    /**
     * Search products
     *
     * @param query Search query
     * @return LiveData of Resource of PagedList of ProductEntity
     */
    public LiveData<Resource<PagedList<ProductEntity>>> searchProducts(final String query) {
        // Configure paging
        PagedList.Config config = new PagedList.Config.Builder()
                .setPageSize(PAGE_SIZE)
                .setInitialLoadSizeHint(PAGE_SIZE * 2)
                .setEnablePlaceholders(true)
                .build();

        // Create data source factory
        DataSource.Factory<Integer, ProductEntity> factory = productDao.searchProducts(query);

        // Build paged list
        LiveData<PagedList<ProductEntity>> pagedList = new LivePagedListBuilder<>(factory, config).build();

        // Trigger network search
        searchProductsFromNetwork(query, 0, PAGE_SIZE * 2);

        // Create resource with paged list
        MediatorLiveData<Resource<PagedList<ProductEntity>>> result = new MediatorLiveData<>();
        result.addSource(pagedList, data -> {
            if (data != null) {
                result.setValue(Resource.success(data));
            } else {
                result.setValue(Resource.loading(null));
            }
        });

        return result;
    }

    /**
     * Search products from network and save to database
     *
     * @param query Search query
     * @param page Page number
     * @param size Page size
     */
    private void searchProductsFromNetwork(String query, int page, int size) {
        productApiService.searchProducts(query, page, size)
                .observeForever(response -> {
                    if (response != null && response.isSuccessful() && response.body != null) {
                        List<ProductEntity> entities = ProductMapper.mapResponseToEntity(response.body.getProducts());
                        AppDatabase.databaseWriteExecutor.execute(() -> productDao.insertAll(entities));
                    }
                });
    }

    /**
     * Filter products with advanced criteria
     *
     * @param request Filter criteria
     * @param page Page number
     * @param size Page size
     * @return LiveData of Resource of ProductListResponse
     */
    public LiveData<Resource<ProductListResponse>> filterProducts(
            final ProductFilterRequest request, final int page, final int size) {
        // For filtered products, we directly return the API response since the filtering is complex
        // and would require complex SQL queries to replicate in the local database
        MutableLiveData<Resource<ProductListResponse>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        productApiService.filterProducts(page, size, request)
                .observeForever(response -> {
                    if (response != null) {
                        if (response.isSuccessful() && response.body != null) {
                            // Save products to database for future reference
                            List<ProductEntity> entities = ProductMapper.mapResponseToEntity(response
