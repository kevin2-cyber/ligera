package com.ligera.app.viewmodel;

import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SplashViewModel extends ViewModel {
    private static final String TAG = SplashViewModel.class.getSimpleName();
    private final MutableLiveData<Boolean> isLoadingComplete = new MutableLiveData<>(false);
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public SplashViewModel() {
        loadData();
    }

    private void loadData() {
        executor.execute(() -> {
            try {
                // Simulate a delay for loading data or other setup
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Log.e(TAG, "Splash screen delay interrupted", e);
                Thread.currentThread().interrupt();
            } finally {
                isLoadingComplete.postValue(true);
            }
        });
    }

    public LiveData<Boolean> getLoadingStatus() {
        return isLoadingComplete;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        executor.shutdownNow();
    }
}
