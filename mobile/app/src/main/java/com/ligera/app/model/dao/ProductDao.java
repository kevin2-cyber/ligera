package com.ligera.app.model.dao;

import androidx.lifecycle.LiveData;
import androidx.paging.PagingSource;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.ligera.app.model.entity.Product;

import java.util.List;

/**
 * Room DAO for product operations
 */
@Dao
public interface ProductDao {
    /**
     * Insert products into the database
     *
     * @param products list of products to insert
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Product> products);

    /**
     * Insert a single product into the database
     *
     * @param product product to insert
     * @return the row ID of the inserted product
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Product product);

    /**
     * Update a product
     *
     * @param product product to update
     */
    @Update
    void update(Product product);

    /**
     * Delete a product
     *
     * @param product product to delete
     */
    @Delete
    void delete(Product product);

    /**
     * Get the count of all products
     *
     * @return LiveData of product count
     */
    @Query("SELECT COUNT(*) FROM products")
    LiveData<Integer> getProductCount();

    /**
     * Get all products as LiveData (simple, no paging)
     *
     * @return LiveData list of all products
     */
    @Query("SELECT * FROM products ORDER BY name ASC")
    LiveData<List<Product>> getAllProductsLiveData();

    /**
     * Get products by category as LiveData (simple, no paging)
     *
     * @param categoryId category ID
     * @return LiveData list of products in category
     */
    @Query("SELECT * FROM products WHERE category_id = :categoryId ORDER BY name ASC")
    LiveData<List<Product>> getProductsByCategoryLiveData(long categoryId);

    /**
     * Get all products with paging support
     *
     * @return PagingSource for Paging 3
     */
    @Query("SELECT * FROM products ORDER BY name ASC")
    PagingSource<Integer, Product> getAllProducts();

    /**
     * Get a product by ID
     *
     * @param id product ID
     * @return LiveData of the product
     */
    @Query("SELECT * FROM products WHERE id = :id")
    LiveData<Product> getProductById(long id);

    /**
     * Get products by category ID with paging support
     *
     * @param categoryId category ID
     * @return PagingSource for Paging 3
     */
    @Query("SELECT * FROM products WHERE category_id = :categoryId ORDER BY name ASC")
    PagingSource<Integer, Product> getProductsByCategory(long categoryId);

    /**
     * Get featured products
     *
     * @param limit maximum number of products to return
     * @return list of featured products
     */
    @Query("SELECT * FROM products WHERE featured = 1 ORDER BY id DESC LIMIT :limit")
    LiveData<List<Product>> getFeaturedProducts(int limit);

    /**
     * Get popular products
     *
     * @param limit maximum number of products to return
     * @return list of popular products
     */
    @Query("SELECT * FROM products WHERE popular = 1 ORDER BY id DESC LIMIT :limit")
    LiveData<List<Product>> getPopularProducts(int limit);

    /**
     * Get all favorite products.
     *
     * @return A LiveData list of all products marked as favorite.
     */
    @Query("SELECT * FROM products WHERE is_favorite = 1")
    LiveData<List<Product>> getFavoriteProducts();

    /**
     * Updates the favorite status of a product.
     *
     * @param productId The ID of the product to update.
     * @param isFavorite The new favorite status.
     */
    @Query("UPDATE products SET is_favorite = :isFavorite WHERE id = :productId")
    void updateFavoriteStatus(long productId, boolean isFavorite);

    /**
     * Search products by name or description with paging support
     *
     * @param query search query
     * @return PagingSource for Paging 3
     */
    @Query("SELECT * FROM products WHERE name LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%' ORDER BY name ASC")
    PagingSource<Integer, Product> searchProducts(String query);

    /**
     * Search products by name or description as LiveData (no paging)
     *
     * @param query search query
     * @return LiveData list of matching products
     */
    @Query("SELECT * FROM products WHERE name LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%' ORDER BY name ASC")
    LiveData<List<Product>> searchProductsLiveData(String query);

    /**
     * Get products by brand
     *
     * @param brand brand name
     * @return LiveData of products
     */
    @Query("SELECT * FROM products WHERE brand = :brand ORDER BY name ASC")
    LiveData<List<Product>> getProductsByBrand(String brand);

    /**
     * Delete all products
     */
    @Query("DELETE FROM products")
    void deleteAll();

    /**
     * Delete old products (refreshed more than the given time ago)
     *
     * @param timestamp timestamp in milliseconds
     */
    @Query("DELETE FROM products WHERE last_refreshed < :timestamp")
    void deleteOldProducts(long timestamp);

    /**
     * Count all products
     *
     * @return number of products
     */
    @Query("SELECT COUNT(*) FROM products")
    int countProducts();

    /**
     * Count products by category
     *
     * @param categoryId category ID
     * @return number of products
     */
    @Query("SELECT COUNT(*) FROM products WHERE category_id = :categoryId")
    int countProductsByCategory(long categoryId);

    /**
     * Update product quantity
     *
     * @param productId product ID
     * @param quantity new quantity
     */
    @Query("UPDATE products SET quantity = :quantity WHERE id = :productId")
    void updateProductQuantity(long productId, int quantity);

    /**
     * Get products in stock
     *
     * @return LiveData of in-stock products
     */
    @Query("SELECT * FROM products WHERE quantity > 0 ORDER BY name ASC")
    LiveData<List<Product>> getProductsInStock();

    /**
     * Get products with discount
     *
     * @return LiveData of discounted products
     */
    @Query("SELECT * FROM products WHERE discount_percent > 0 ORDER BY discount_percent DESC")
    LiveData<List<Product>> getProductsWithDiscount();
    
    /**
     * Get products sorted by price (low to high)
     *
     * @return LiveData of products sorted by price
     */
    @Query("SELECT * FROM products ORDER BY price ASC")
    LiveData<List<Product>> getProductsSortedByPriceLowToHigh();
    
    /**
     * Get products sorted by price (high to low)
     *
     * @return LiveData of products sorted by price
     */
    @Query("SELECT * FROM products ORDER BY price DESC")
    LiveData<List<Product>> getProductsSortedByPriceHighToLow();
    
    /**
     * Get products sorted by popularity
     *
     * @return LiveData of products sorted by popularity
     */
    @Query("SELECT * FROM products ORDER BY popularity_score DESC")
    LiveData<List<Product>> getProductsSortedByPopularity();
    
    /**
     * Get products sorted by newest first
     *
     * @return LiveData of products sorted by creation date
     */
    @Query("SELECT * FROM products ORDER BY created_at DESC")
    LiveData<List<Product>> getProductsSortedByNewest();
    
    /**
     * Get products with multiple filters
     *
     * @param categoryId Category ID (0 for all categories)
     * @param minPrice Minimum price
     * @param maxPrice Maximum price
     * @param inStockOnly Whether to include only in-stock products
     * @return LiveData of filtered products
     */
    @Query("SELECT * FROM products WHERE " +
           "(:categoryId = 0 OR category_id = :categoryId) AND " +
           "(:minPrice IS NULL OR price >= :minPrice) AND " +
           "(:maxPrice IS NULL OR price <= :maxPrice) AND " +
           "(:inStockOnly = 0 OR quantity > 0) " +
           "ORDER BY name ASC")
    LiveData<List<Product>> getFilteredProducts(long categoryId, 
                                               String minPrice,
                                               String maxPrice, 
                                               boolean inStockOnly);
    
    /**
     * Update product rating
     *
     * @param productId Product ID
     * @param rating New rating
     * @param ratingCount New rating count
     */
    @Query("UPDATE products SET rating = :rating, rating_count = :ratingCount WHERE id = :productId")
    void updateProductRating(long productId, float rating, int ratingCount);
    
    /**
     * Batch update operations in transaction
     *
     * @param products List of products to update
     */
    @Transaction
    default void updateProductsInTransaction(List<Product> products) {
        for (Product product : products) {
            update(product);
        }
    }
}
