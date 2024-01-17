package com.ligera.app.model.repository;

import androidx.lifecycle.LiveData;

import com.ligera.app.model.dao.CategoryDao;
import com.ligera.app.model.dao.ProductDao;
import com.ligera.app.model.entity.Category;
import com.ligera.app.model.entity.Product;

import java.util.List;

public class ProductShopRepository {
    private ProductDao productDao;
    private CategoryDao categoryDao;
    private LiveData<List<Product>> products;
    private LiveData<List<Category>> categories;
}
