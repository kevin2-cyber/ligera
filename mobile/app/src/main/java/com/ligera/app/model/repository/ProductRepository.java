package com.ligera.app.model.repository;

import androidx.lifecycle.LiveData;

import com.ligera.app.model.entity.Category;
import com.ligera.app.model.entity.Product;
import com.ligera.app.util.Resource;

import java.util.List;

/**
 * Repository interface for Product and Category data operations
 * This allows for better testability through dependency injection
 */
public interface ProductRepository {
    
    /**
     * Get all categories
     * @return LiveData wrapped with Resource for loading/success/error states
     */
    LiveData<Resource<List<Category>>> getCategories();
    
    /**
     * Get products by category
     * @param categoryId the category ID to filter by
     * @return LiveData wrapped with Resource for loading/success/error states
     */
    LiveData<Resource<List<Product>>> getProducts(int categoryId);
    
    /**
     * Get a specific product by ID
     * @param productId the product ID to retrieve
     * @return LiveData wrapped with Resource for loading/success/error states
     */
    LiveData<Resource<Product>> getProductById(long productId);
    
    /**
     * Search products by query string
     * @param query the search query
     * @return LiveData wrapped with Resource for loading/success/error states
     */
    LiveData<Resource<List<Product>>> searchProducts(String query);
    
    /**
     * Get featured products
     * @return LiveData wrapped with Resource for loading/success/error states
     */
    LiveData<Resource<List<Product>>> getFeaturedProducts();
    
    /**
     * Save a category to the database
     * @param category the category to save
     * @return LiveData wrapped with Resource for operation result
     */
    LiveData<Resource<Void>> saveCategory(Category category);
    
    /**
     * Save a product to the database
     * @param product the product to save
     * @return LiveData wrapped with Resource for operation result
     */
    LiveData<Resource<Void>> saveProduct(Product product);
    
    /**
     * Delete a category from the database
     * @param category the category to delete
     * @return LiveData wrapped with Resource for operation result
     */
    LiveData<Resource<Void>> deleteCategory(Category category);
    
    /**
     * Delete a product from the database
     * @param product the product to delete
     * @return LiveData wrapped with Resource for operation result
     */
    LiveData<Resource<Void>> deleteProduct(Product product);
}

