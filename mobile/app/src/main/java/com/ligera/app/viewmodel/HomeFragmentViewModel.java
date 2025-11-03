package com.ligera.app.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.LiveDataReactiveStreams;
import androidx.lifecycle.ViewModelKt;
import androidx.paging.PagingData;
import androidx.paging.rxjava3.PagingRx;

import com.ligera.app.model.database.AppDatabase;
import com.ligera.app.model.entity.Category;
import com.ligera.app.model.entity.Product;
import com.ligera.app.network.RetrofitClient;
import com.ligera.app.network.TokenManager;
import com.ligera.app.network.interceptor.NetworkConnectionInterceptor;
import com.ligera.app.network.service.ProductApiService;
import com.ligera.app.repository.ProductRepository;
import com.ligera.app.util.Resource;

import java.util.List;

import io.reactivex.rxjava3.core.Flowable;
import kotlinx.coroutines.CoroutineScope;

public class HomeFragmentViewModel extends AndroidViewModel {
    private final ProductRepository repository;

    private final LiveData<Resource<List<Category>>> allCategories;
    private LiveData<PagingData<Product>> productsOfSelectedCategory;

    public HomeFragmentViewModel(@NonNull Application application) {
        super(application);
        AppDatabase database = AppDatabase.getInstance(application);
        TokenManager tokenManager = TokenManager.getInstance(application);
        ProductApiService apiService = RetrofitClient.getInstance(tokenManager, new NetworkConnectionInterceptor(application)).getClientV1().create(ProductApiService.class);
        repository = new ProductRepository(database, apiService);
        allCategories = repository.getCategories();
    }

    public LiveData<Resource<List<Category>>> getAllCategories() {
        return allCategories;
    }

    public LiveData<PagingData<Product>> getProductsOfSelectedCategory(long categoryId) {
        CoroutineScope viewModelScope = ViewModelKt.getViewModelScope(this);
        Flowable<PagingData<Product>> productsFlowable = repository.getProductsByCategory(categoryId);
        productsOfSelectedCategory = LiveDataReactiveStreams.fromPublisher(PagingRx.cachedIn(productsFlowable, viewModelScope));
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
