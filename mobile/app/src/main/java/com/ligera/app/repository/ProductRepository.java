package com.ligera.app.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.ligera.app.model.database.AppDatabase;
import com.ligera.app.model.dao.CategoryDao;
import com.ligera.app.model.dao.ProductDao;
import com.ligera.app.model.entity.Category;
import com.ligera.app.model.entity.Product;
import com.ligera.app.util.AppExecutors;
import com.ligera.app.util.Resource;

import java.util.List;

/**
 * Repository for product-related operations (Local database only, no RxJava)
 */
public class ProductRepository {
    private static final String TAG = "ProductRepository";


    // Dependencies
    private final ProductDao productDao;
    private final CategoryDao categoryDao;
    private final AppExecutors appExecutors;

    /**
     * Constructor - Local database only
     *
     * @param database Room database
     */
    public ProductRepository(AppDatabase database) {
        this.productDao = database.productDao();
        this.categoryDao = database.categoryDao();
        this.appExecutors = new AppExecutors();
    }

    /**
     * Get a product by ID from local database
     *
     * @param productId Product ID
     * @return LiveData of Resource of Product
     */
    public LiveData<Resource<Product>> getProductById(final long productId) {
        return Transformations.map(productDao.getProductById(productId), product -> {
            if (product != null) {
                return Resource.success(product);
            } else {
                return Resource.error("Product not found", null);
            }
        });
    }

    /**
     * Get all products as simple LiveData (no paging, no RxJava)
     *
     * @return LiveData list of all products
     */
    public LiveData<List<Product>> getAllProductsSimple() {
        return productDao.getAllProductsLiveData();
    }

    /**
     * Get products by category as simple LiveData (no paging, no RxJava)
     *
     * @param categoryId Category ID
     * @return LiveData list of products in category
     */
    public LiveData<List<Product>> getProductsByCategorySimple(long categoryId) {
        return productDao.getProductsByCategoryLiveData(categoryId);
    }


    /**
     * Get all favorite products.
     *
     * @return A LiveData list of all products marked as favorite.
     */
    public LiveData<List<Product>> getFavoriteProducts() {
        return productDao.getFavoriteProducts();
    }

    /**
     * Updates the favorite status of a product.
     *
     * @param productId The ID of the product to update.
     * @param isFavorite The new favorite status.
     */
    public void setFavorite(long productId, boolean isFavorite) {
        appExecutors.diskIO().execute(() -> productDao.updateFavoriteStatus(productId, isFavorite));
    }

    /**
     * Get featured products from local database
     *
     * @param limit Maximum number of products to return
     * @return LiveData of Resource of List of Product
     */
    public LiveData<Resource<List<Product>>> getFeaturedProducts(final int limit) {
        return Transformations.map(productDao.getFeaturedProducts(limit), products -> {
            if (products != null && !products.isEmpty()) {
                return Resource.success(products);
            } else {
                return Resource.success(products); // Return empty list, not error
            }
        });
    }

    /**
     * Get popular products from local database
     *
     * @param limit Maximum number of products to return
     * @return LiveData of Resource of List of Product
     */
    public LiveData<Resource<List<Product>>> getPopularProducts(final int limit) {
        return Transformations.map(productDao.getPopularProducts(limit), products -> {
            if (products != null && !products.isEmpty()) {
                return Resource.success(products);
            } else {
                return Resource.success(products); // Return empty list, not error
            }
        });
    }

    /**
     * Search products in local database
     *
     * @param query Search query
     * @return LiveData list of matching products
     */
    public LiveData<List<Product>> searchProducts(final String query) {
        return productDao.searchProductsLiveData(query);
    }

    /**
     * Get all categories from local database
     *
     * @return LiveData of Resource of List of Category
     */
    public LiveData<Resource<List<Category>>> getCategories() {
        return Transformations.map(categoryDao.getAllCategories(), categories -> {
            if (categories != null) {
                return Resource.success(categories);
            } else {
                return Resource.error("No categories found", null);
            }
        });
    }

    /**
     * Insert a single product
     *
     * @param product Product to insert
     */
    public void insertProduct(Product product) {
        appExecutors.diskIO().execute(() -> productDao.insert(product));
    }

    /**
     * Insert multiple products
     *
     * @param products List of products to insert
     */
    public void insertProducts(List<Product> products) {
        appExecutors.diskIO().execute(() -> {
            for (Product product : products) {
                productDao.insert(product);
            }
        });
    }

    /**
     * Get total product count
     *
     * @return LiveData of product count
     */
    public LiveData<Integer> getProductCount() {
        return productDao.getProductCount();
    }

    /**
     * Update a product
     *
     * @param product Product to update
     */
    public void updateProduct(Product product) {
        appExecutors.diskIO().execute(() -> productDao.update(product));
    }

    /**
     * Delete a product
     *
     * @param product Product to delete
     */
    public void deleteProduct(Product product) {
        appExecutors.diskIO().execute(() -> productDao.delete(product));
    }

    /**
     * Insert a category
     *
     * @param category Category to insert
     */
    public void insertCategory(Category category) {
        appExecutors.diskIO().execute(() -> categoryDao.insert(category));
    }

    /**
     * Insert multiple categories
     *
     * @param categories List of categories to insert
     */
    public void insertCategories(List<Category> categories) {
        appExecutors.diskIO().execute(() -> categoryDao.insertAll(categories));
    }
}
