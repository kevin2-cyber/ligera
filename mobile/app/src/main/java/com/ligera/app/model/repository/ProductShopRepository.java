package com.ligera.app.model.repository;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LiveData;

import com.ligera.app.model.dao.CategoryDao;
import com.ligera.app.model.dao.ProductDao;
import com.ligera.app.model.database.ProductDatabase;
import com.ligera.app.model.entity.Category;
import com.ligera.app.model.entity.Product;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProductShopRepository {
    private final ProductDao productDao;
    private final CategoryDao categoryDao;
    private LiveData<List<Product>> products;
    private LiveData<List<Category>> categories;
    ExecutorService executorService;
    Handler handler;

    public ProductShopRepository(Application application) {
        ProductDatabase productDatabase = ProductDatabase.getInstance(application);
        productDao = productDatabase.productDao();
        categoryDao = productDatabase.categoryDao();
        executorService = Executors.newSingleThreadExecutor();
        handler = new Handler(Looper.getMainLooper());
    }

    public LiveData<List<Category>> getCategories() {
        categories = categoryDao.getAllCategories();
        return categories;
    }

    public LiveData<List<Product>> getProducts(int categoryId) {
        products = productDao.getProducts(categoryId);
        return products;
    }

    public void insertCategory(Category category) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                // Inserting categories
                categoryDao.insert(category);
            }
        });
    }

    public void insertProduct(Product product) {

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                // Inserting products
                productDao.insert(product);

                // do after background execution is done - post execution
            }
        });
    }

    public void deleteCategory(Category category) {

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                // deleting categories
                categoryDao.delete(category);
            }
        });
    }

    public void deleteProduct(Product product) {

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                // deleting products
                productDao.delete(product);
            }
        });
    }

    public void updateCategory(Category category) {

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                // update categories
                categoryDao.update(category);
            }
        });
    }

    public void updateProduct(Product product) {

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                // update product
                productDao.update(product);
            }
        });
    }
}
