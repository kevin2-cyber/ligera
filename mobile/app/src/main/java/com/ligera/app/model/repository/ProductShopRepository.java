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
    private ProductDao productDao;
    private CategoryDao categoryDao;
    private LiveData<List<Product>> products;
    private LiveData<List<Category>> categories;

    public ProductShopRepository(Application application) {
        ProductDatabase productDatabase = ProductDatabase.getInstance(application);
        productDao = productDatabase.productDao();
        categoryDao = productDatabase.categoryDao();
    }

    public LiveData<List<Category>> getCategories() {
        return categoryDao.getAllCategories();
    }

    public LiveData<List<Product>> getProducts(int categoryId) {
        return productDao.getProducts(categoryId);
    }

    public void insertCategory(Category category) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                // Inserting categories
                categoryDao.insert(category);
            }
        });
    }

    public void insertProduct(Product product) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Handler handler =  new Handler(Looper.getMainLooper());

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
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Handler handler =  new Handler(Looper.getMainLooper());

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                // deleting categories
                categoryDao.delete(category);
            }
        });
    }

    public void deleteProduct(Product product) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Handler handler =  new Handler(Looper.getMainLooper());

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                // deleting products
                productDao.delete(product);
            }
        });
    }

    public void updateCategory(Category category) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Handler handler =  new Handler(Looper.getMainLooper());

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                // update categories
                categoryDao.update(category);
            }
        });
    }

    public void updateProduct(Product product) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Handler handler =  new Handler(Looper.getMainLooper());

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                // update product
                productDao.update(product);
            }
        });
    }
}
