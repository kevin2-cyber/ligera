package com.ligera.app.viewmodel;

import android.app.Application;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.ligera.app.model.database.AppDatabase;
import com.ligera.app.network.RetrofitClient;
import com.ligera.app.network.TokenManager;
import com.ligera.app.network.interceptor.NetworkConnectionInterceptor;
import com.ligera.app.network.service.ProductApiService;
import com.ligera.app.repository.ProductRepository;


public class DetailActivityViewModel extends AndroidViewModel {
    private final MutableLiveData<Integer> counter = new MutableLiveData<>();
    private final ProductRepository repository;

    public DetailActivityViewModel(@NonNull Application application) {
        super(application);
        AppDatabase database = AppDatabase.getInstance(application);
        TokenManager tokenManager = TokenManager.getInstance(application);
        ProductApiService apiService = RetrofitClient.getInstance(tokenManager, new NetworkConnectionInterceptor(application)).getClientV1().create(ProductApiService.class);
        repository = new ProductRepository(database, apiService);
    }

    public void increaseCounter(View view) {
        // retrieve the current value from LiveData
        int currentValue = counter.getValue() != null ? counter.getValue() : 0;

        // increase value by 1
        counter.setValue(currentValue + 1);
    }

    public void decreaseCounter(View view) {
        // retrieve the current value from LiveData
        int currentValue = counter.getValue() != null ? counter.getValue() : 0;

        // decrease value by 1
        if (currentValue > 0) {
            counter.setValue(currentValue - 1);
        }
    }

    public void onSubmitOrder(View view) {}

    public LiveData<Integer> getCounter() {
        return counter;
    }
}
