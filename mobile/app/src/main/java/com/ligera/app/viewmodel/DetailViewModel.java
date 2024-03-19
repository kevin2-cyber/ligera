package com.ligera.app.viewmodel;

import android.view.View;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class DetailViewModel extends ViewModel {
    MutableLiveData<Integer> counter = new MutableLiveData<>();

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
