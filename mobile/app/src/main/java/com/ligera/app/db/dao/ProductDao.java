package com.ligera.app.db.dao;

import androidx.lifecycle.LiveData;
import androidx.paging.DataSource;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import com.ligera.app.db.entity.ProductEntity;

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
    void insertAll(List<ProductEntity> products);

    /**
     * Insert a single product into the database
     *
     * @param product product to insert
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(ProductEntity product);

    /**
     * Get a product by ID
     *
     * @param id product ID
     * @return LiveData of the product
     */
    @Query("SELECT * FROM products WHERE id = :id")
    LiveData<ProductEntity> getProductById(long id);

    /**
     * Get all products
     *
     * @return DataSource.Factory for paging
     */
    @Query("SELECT * FROM products ORDER BY name ASC")
    DataSource.Factory<Integer, ProductEntity> getAllProducts();

    /**
     * Get products by category ID
     *
     * @param categoryId category ID
     * @return DataSource.Factory for paging
     */
    @Query("SELECT * FROM products WHERE category_id = :categoryId ORDER BY name ASC")
    DataSource.Factory<Integer, ProductEntity> getProductsByCategory(long categoryId);

    /**
     * Get featured products
     *
     * @param limit maximum number of products to return
     * @return list of featured products
     */
    @Query("SELECT * FROM products WHERE featured = 1 ORDER BY id DESC LIMIT :limit")
    LiveData<List<ProductEntity>> getFeaturedProducts(int limit);

    /**
     * Get popular products
     *
     * @param limit maximum number of products to return
     * @return list of popular products
     */
    @Query("SELECT * FROM products WHERE popular = 1 ORDER BY id DESC LIMIT :limit")
    LiveData<List<ProductEntity>> getPopularProducts(int limit);

    /**
     * Search products by name or description
     *
     * @param query search query
     * @return DataSource.Factory for paging
     */
    @Query("SELECT * FROM products WHERE name LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%' ORDER BY name ASC")
    DataSource.Factory<Integer, ProductEntity> searchProducts(String query);

    /**
     * Get products by brand
     *
     * @param brand brand name
     * @return DataSource.Factory for paging
     */
    @Query("SELECT * FROM products WHERE brand = :brand ORDER BY name ASC")
    DataSource.Factory<Integer, ProductEntity> getProductsByBrand(String brand);

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
}

