package com.ligera.app.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.ligera.app.model.database.AppDatabase;
import com.ligera.app.model.entity.Product;
import com.ligera.app.repository.ProductRepository;
import com.ligera.app.util.Resource;


public class DetailActivityViewModel extends AndroidViewModel {
    private final MutableLiveData<Integer> counter = new MutableLiveData<>();
    private final ProductRepository repository;

    public DetailActivityViewModel(@NonNull Application application) {
        super(application);
        AppDatabase database = AppDatabase.getInstance(application);
        repository = new ProductRepository(database);
    }

    public LiveData<Resource<Product>> getProductById(long productId) {
        return repository.getProductById(productId);
    }

    public void setFavorite(long id, boolean isFavourite) {
        repository.setFavorite(id, isFavourite);
    }

    public void increaseCounter() {
        // retrieve the current value from LiveData
        int currentValue = counter.getValue() != null ? counter.getValue() : 0;

        // increase value by 1
        counter.setValue(currentValue + 1);
    }

    public void decreaseCounter() {
        // retrieve the current value from LiveData
        int currentValue = counter.getValue() != null ? counter.getValue() : 0;

        // decrease value by 1
        if (currentValue > 0) {
            counter.setValue(currentValue - 1);
        }
    }

    public void onSubmitOrder() {}

    public LiveData<Integer> getCounter() {
        return counter;
    }
}
