package com.ligera.app.model.entity;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.ligera.app.BR;

public class Category extends BaseObservable {
    private int categoryId;
    private String name;

    public Category() {}

    public Category(int categoryId, String name) {
        this.categoryId = categoryId;
        this.name = name;
    }

    @Bindable
    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
        notifyPropertyChanged(BR.categoryId);
    }

    @Bindable
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        notifyPropertyChanged(BR.name);
    }
}
