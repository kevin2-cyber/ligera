package com.ligera.app.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.ligera.app.model.database.AppDatabase;
import com.ligera.app.model.entity.Category;
import com.ligera.app.model.entity.Product;
import com.ligera.app.repository.ProductRepository;
import com.ligera.app.util.Resource;

import java.util.List;

/**
 * Simple ViewModel for HomeFragment - uses LiveData only, no RxJava or Paging
 * This avoids CancellationException issues
 */
public class HomeFragmentViewModel extends AndroidViewModel {
    private final ProductRepository repository;

    // Selected category ID (-1 means "All")
    private final MutableLiveData<Long> selectedCategoryId = new MutableLiveData<>(-1L);

    // Products LiveData that changes based on selected category
    private final LiveData<List<Product>> products;

    public HomeFragmentViewModel(@NonNull Application application) {
        super(application);
        AppDatabase database = AppDatabase.getInstance(application);
        repository = new ProductRepository(database);

        // Use Transformations.switchMap to automatically switch data source
        // when selectedCategoryId changes
        products = Transformations.switchMap(selectedCategoryId, categoryId -> {
            if (categoryId == null || categoryId == -1L) {
                // "All" tab - get all products
                return repository.getAllProductsSimple();
            } else {
                // Specific category - get products in that category
                return repository.getProductsByCategorySimple(categoryId);
            }
        });
    }

    /**
     * Get all categories
     */
    public LiveData<Resource<List<Category>>> getAllCategories() {
        return repository.getCategories();
    }

    /**
     * Get products (automatically updates when category changes)
     */
    public LiveData<List<Product>> getProducts() {
        return products;
    }

    /**
     * Load products by category
     * @param categoryId Category ID (-1 for all products)
     */
    public void loadProductsByCategory(long categoryId) {
        selectedCategoryId.setValue(categoryId);
    }

    /**
     * Get product count
     */
    public LiveData<Integer> getProductCount() {
        return repository.getProductCount();
    }

    /**
     * Insert multiple products
     */
    public void insertProducts(List<Product> products) {
        repository.insertProducts(products);
    }

    /**
     * Insert single product
     */
    public void addProduct(Product product) {
        repository.insertProduct(product);
    }

    /**
     * Update product
     */
    public void updateProduct(Product product) {
        repository.updateProduct(product);
    }

    /**
     * Delete product
     */
    public void deleteProduct(Product product) {
        repository.deleteProduct(product);
    }
}
