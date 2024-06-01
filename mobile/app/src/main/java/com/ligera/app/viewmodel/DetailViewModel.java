package com.ligera.app.viewmodel;

import android.app.Application;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.ligera.app.model.repository.ProductShopRepository;

public class DetailViewModel extends AndroidViewModel {
    MutableLiveData<Integer> counter = new MutableLiveData<>();
    private final ProductShopRepository repository;

    public DetailViewModel(@NonNull Application application) {
        super(application);
        repository = new ProductShopRepository(application);
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
        counter.setValue(currentValue - 1);
    }

    public LiveData<Integer> getCounter() {
        return counter;
    }
}
