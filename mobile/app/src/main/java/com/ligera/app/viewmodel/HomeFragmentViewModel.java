package com.ligera.app.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.LiveDataReactiveStreams;
import androidx.lifecycle.ViewModelKt;
import androidx.paging.Pager;
import androidx.paging.PagingConfig;
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

    public HomeFragmentViewModel(@NonNull Application application) {
        super(application);
        AppDatabase database = AppDatabase.getInstance(application);
        TokenManager tokenManager = TokenManager.getInstance(application);
        ProductApiService apiService = RetrofitClient.getInstance(tokenManager, new NetworkConnectionInterceptor(application)).getClientV1().create(ProductApiService.class);
        repository = new ProductRepository(database, apiService);
    }

    public LiveData<Resource<List<Category>>> getAllCategories() {
        return repository.getCategories();
    }

//    public LiveData<PagingData<Product>> getProductsOfSelectedCategory(long categoryId) {
//        CoroutineScope viewModelScope = ViewModelKt.getViewModelScope(this);
//        Flowable<PagingData<Product>> productsFlowable = repository.getProductsByCategory(categoryId);
//        return PagingRx.getLiveData(PagingRx.cachedIn(productsFlowable, viewModelScope));
//    }

//    public LiveData<PagingData<Product>> getProductsOfSelectedCategory(long categoryId) {
//        CoroutineScope viewModelScope = ViewModelKt.getViewModelScope(this);
//
//        // Create the Pager here instead of getting Flowable from repository
//        Pager<Integer, Product> pager = new Pager<>(
//                new PagingConfig(20, 40, false),
//                () -> repository.getProductDao().getProductsByCategory(categoryId)
//        );
//
//        // Get LiveData directly from Pager using PagingRx
//        return PagingRx.getLiveData(
//                PagingRx.cachedIn(PagingRx.getFlowable(pager), viewModelScope)
//        );
//    }

    public LiveData<PagingData<Product>> getProductsOfSelectedCategory(long categoryId) {
        CoroutineScope viewModelScope = ViewModelKt.getViewModelScope(this);
        Flowable<PagingData<Product>> productsFlowable =
                PagingRx.cachedIn(repository.getProductsByCategory(categoryId), viewModelScope);

        // Convert Flowable → LiveData
        return LiveDataReactiveStreams.fromPublisher(productsFlowable);
    }

    public LiveData<PagingData<Product>> getAllProducts() {
        CoroutineScope viewModelScope = ViewModelKt.getViewModelScope(this);
        Flowable<PagingData<Product>> productsFlowable =
                PagingRx.cachedIn(repository.getProducts(), viewModelScope);

        // Convert Flowable → LiveData
        return LiveDataReactiveStreams.fromPublisher(productsFlowable);
    }

    public LiveData<Integer> getProductCount() {
        return repository.getProductCount();
    }

    public void insertProducts(List<Product> products) {
        repository.insertProducts(products);
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
