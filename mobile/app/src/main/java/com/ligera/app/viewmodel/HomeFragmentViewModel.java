package com.ligera.app.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.ligera.app.model.database.AppDatabase;
import com.ligera.app.model.entity.Category;
import com.ligera.app.model.entity.Product;
import com.ligera.app.network.RetrofitClient;
import com.ligera.app.network.TokenManager;
import com.ligera.app.network.service.ProductApiService;
import com.ligera.app.repository.ProductRepository;

import java.util.List;

public class HomeFragmentViewModel extends AndroidViewModel {
    private final ProductRepository repository;

    private final LiveData<List<Category>> allCategories;
    private LiveData<List<Product>> productsOfSelectedCategory;

    public HomeFragmentViewModel(@NonNull Application application) {
        super(application);
        AppDatabase database = AppDatabase.getInstance(application);
        TokenManager tokenManager = TokenManager.getInstance(application);
        ProductApiService apiService = RetrofitClient.getInstance(tokenManager).getClientV1().create(ProductApiService.class);
        repository = new ProductRepository(database, apiService);
        allCategories = repository.getCategories();
    }

    public LiveData<List<Category>> getAllCategories() {
        return allCategories;
    }

    public LiveData<List<Product>> getProductsOfSelectedCategory(int categoryId) {
        productsOfSelectedCategory = repository.getProducts(categoryId);
        return productsOfSelectedCategory;
    }

    public void addProduct(Product product) {
        repository.insertProduct(product);
    }

    public void updateProduct(Product product) {
        repository.updateProduct(product);
    }

    public void deleteProduct(Product product) {
        repository.deleteProduct(product);
    }
}
