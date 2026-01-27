package com.ligera.app.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.ligera.app.model.database.AppDatabase;
import com.ligera.app.model.entity.Product;
import com.ligera.app.repository.ProductRepository;

import java.util.List;

public class FavoritesViewModel extends AndroidViewModel {
    private final ProductRepository repository;

    public FavoritesViewModel(@NonNull Application application) {
        super(application);
        AppDatabase database = AppDatabase.getInstance(application);
        repository = new ProductRepository(database);
    }

    public LiveData<List<Product>> getFavoriteProducts() {
        return repository.getFavoriteProducts();
    }
}
