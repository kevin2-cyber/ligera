package com.ligera.app.model.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.ligera.app.model.entity.Product;

import java.util.List;

@Dao
public interface ProductDao {
    @Insert
    void insert(Product product);
    @Update
    void update(Product product);
    @Delete
    void delete(Product product);

    @Query("SELECT * FROM product_table")
    LiveData<List<Product>> getAllProducts();

    @Query("SELECT * FROM product_table WHERE category_id==:categoryId")
    LiveData<List<Product>> getProducts(int categoryId);
}
