package com.ligera.app.model.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.ligera.app.model.entity.Category;

import java.util.List;

/**
 * Room DAO for category operations
 */
@Dao
public interface CategoryDao {
    /**
     * Insert categories into the database
     *
     * @param categories list of categories to insert
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Category> categories);

    /**
     * Insert a single category into the database
     *
     * @param category category to insert
     * @return the row ID of the inserted category
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Category category);

    /**
     * Update a category
     *
     * @param category category to update
     */
    @Update
    void update(Category category);

    /**
     * Delete a category
     *
     * @param category category to delete
     */
    @Delete
    void delete(Category category);

    /**
     * Get all categories
     *
     * @return LiveData of all categories
     */
    @Query("SELECT * FROM categories ORDER BY name ASC")
    LiveData<List<Category>> getAllCategories();

    /**
     * Get a category by ID
     *
     * @param id category ID
     * @return LiveData of the category
     */
    @Query("SELECT * FROM categories WHERE id = :id")
    LiveData<Category> getCategoryById(long id);

    /**
     * Get child categories by parent ID
     *
     * @param parentId parent category ID
     * @return LiveData of child categories
     */
    @Query("SELECT * FROM categories WHERE parent_id = :parentId ORDER BY name ASC")
    LiveData<List<Category>> getChildCategories(Long parentId);

    /**
     * Get root categories (with no parent)
     *
     * @return LiveData of root categories
     */
    @Query("SELECT * FROM categories WHERE parent_id IS NULL ORDER BY name ASC")
    LiveData<List<Category>> getRootCategories();

    /**
     * Count all categories
     *
     * @return number of categories
     */
    @Query("SELECT COUNT(*) FROM categories")
    int countCategories();

    /**
     * Delete all categories
     */
    @Query("DELETE FROM categories")
    void deleteAll();

    /**
     * Update product count for a category
     *
     * @param categoryId category ID
     * @param count product count
     */
    @Query("UPDATE categories SET product_count = :count WHERE id = :categoryId")
    void updateProductCount(long categoryId, int count);

    /**
     * Update last refreshed timestamp for a category
     *
     * @param categoryId category ID
     * @param timestamp timestamp in milliseconds
     */
    @Query("UPDATE categories SET last_refreshed = :timestamp WHERE id = :categoryId")
    void updateLastRefreshed(long categoryId, long timestamp);
    
    /**
     * Get categories by path (hierarchical search)
     *
     * @param pathIds Category path (comma-separated category IDs)
     * @return LiveData of categories matching the path
     */
    @Query("SELECT * FROM categories WHERE id IN (:pathIds)")
    LiveData<List<Category>> getCategoriesByPath(List<Long> pathIds);
    
    /**
     * Get categories with most products
     *
     * @param limit Maximum number of categories to return
     * @return LiveData of popular categories
     */
    @Query("SELECT * FROM categories ORDER BY product_count DESC LIMIT :limit")
    LiveData<List<Category>> getPopularCategories(int limit);
    
    /**
     * Search categories by name
     *
     * @param query Search query
     * @return LiveData of matching categories
     */
    @Query("SELECT * FROM categories WHERE name LIKE '%' || :query || '%' ORDER BY name ASC")
    LiveData<List<Category>> searchCategories(String query);
    
    /**
     * Gets root categories or the direct children of a given category.
     *
     * @param rootId The parent category ID. If null, returns root categories.
     * @return LiveData of categories.
     */
    default LiveData<List<Category>> getCategoryTree(Long rootId) {
        if (rootId == null) {
            return getRootCategories();
        } else {
            return getChildCategories(rootId);
        }
    }
    
    /**
     * Batch operations in transaction
     *
     * @param categories List of categories to update
     */
    @Transaction
    default void updateCategoriesInTransaction(List<Category> categories) {
        for (Category category : categories) {
            update(category);
        }
    }
}
