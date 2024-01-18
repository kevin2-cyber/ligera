package com.ligera.app.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.ligera.app.model.entity.Category;
import com.ligera.app.model.entity.Product;
import com.ligera.app.model.repository.ProductShopRepository;

import java.util.List;

public class HomeActivityViewModel extends AndroidViewModel {
    // repository
    private ProductShopRepository repository;
    // livedata
    private LiveData<List<Category>> allCategories;
    private LiveData<List<Product>> productsOfSelectedCategory;

    public HomeActivityViewModel(@NonNull Application application) {
        super(application);
        repository = new ProductShopRepository(application);
    }

    public LiveData<List<Category>> getAllCategories() {
        allCategories = repository.getCategories();
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
