package com.ligera.app.model.entity;

import androidx.annotation.NonNull;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.ligera.app.BR;
@Entity(tableName = "categories_table")
public class Category extends BaseObservable {
    @PrimaryKey(autoGenerate = true)
    private int categoryId;
    @ColumnInfo(name = "category_name")
    private String name;

    @Ignore
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

    @NonNull
    @Override
    public String toString() {
        return this.name;
    }
}