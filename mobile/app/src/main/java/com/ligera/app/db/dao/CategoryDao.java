package com.ligera.app.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.ligera.app.db.entity.CategoryEntity;

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
    void insertAll(List<CategoryEntity> categories);

    /**
     * Insert a single category into the database
     *
     * @param category category to insert
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(CategoryEntity category);

    /**
     * Get all categories
     *
     * @return LiveData of all categories
     */
    @Query("SELECT * FROM categories ORDER BY name ASC")
    LiveData<List<CategoryEntity>> getAllCategories();

    /**
     * Get a category by ID
     *
     * @param id category ID
     * @return LiveData of the category
     */
    @Query("SELECT * FROM categories WHERE id = :id")
    LiveData<CategoryEntity> getCategoryById(long id);

    /**
     * Get child categories by parent ID
     *
     * @param parentId parent category ID
     * @return LiveData of child categories
     */
    @Query("SELECT * FROM categories WHERE parent_id = :parentId ORDER BY name ASC")

