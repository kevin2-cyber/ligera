package com.ligera.app.model.entity;

import static androidx.room.ForeignKey.CASCADE;

import android.icu.text.NumberFormat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.databinding.library.baseAdapters.BR;

import java.util.Locale;
import java.util.Objects;

@Entity(tableName = "product_table", foreignKeys = @ForeignKey(entity = Category.class,
        parentColumns = "categoryId", childColumns = "category_id", onDelete = CASCADE))
public class Product extends BaseObservable {
    @PrimaryKey(autoGenerate = true)
    private int productId;
    @ColumnInfo(name = "category_id")
    private int categoryId;
    @ColumnInfo(name = "image")
    private int image;
    @ColumnInfo(name = "product_name")
    private String name;
    @ColumnInfo(name = "product_description")
    private int description;
    @ColumnInfo(name = "product_price")
    private String price;
    @ColumnInfo(name = "product_quantity")
    private int quantity;
    @ColumnInfo(name = "product_brand")
    private String brand;
    @ColumnInfo(name = "product_size")
    private String size;

    @Ignore
    NumberFormat numberFormat = NumberFormat.getCurrencyInstance(Locale.US);

    @Ignore
    public Product() {}

    public Product(int productId,
                   String name,
                   int image,
                   int description,
                   String price,
                   int quantity,
                   int categoryId,
                   String brand,
                   String size) {
        this.productId = productId;
        this.name = name;
        this.image = image;
        this.description = description;
        this.price = numberFormat.format(Double.parseDouble(price));
        this.quantity = quantity;
        this.categoryId = categoryId;
        this.brand = brand;
        this.size = size;
    }

    @Bindable
    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
        notifyPropertyChanged(BR.productId);
    }

    @Bindable
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        notifyPropertyChanged(BR.name);
    }

    @Bindable
    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
        notifyPropertyChanged(BR.image);
    }



    @Bindable
    public int getDescription() {
        return description;
    }

    public void setDescription(int description) {
        this.description = description;
        notifyPropertyChanged(BR.description);
    }

    @Bindable
    public String getPrice(){
        return price;
    }

    public void setPrice(String price){
       this.price = price;
        notifyPropertyChanged(BR.price);
    }

    @Bindable
    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
        notifyPropertyChanged(BR.quantity);
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
    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
        notifyPropertyChanged(BR.brand);
    }

    @Bindable
    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
        notifyPropertyChanged(BR.size);
    }

    @NonNull
    @Override
    public String toString() {
        return "Product{" +
                "productId=" + productId +
                ", categoryId=" + categoryId +
                ", image=" + image +
                ", name='" + name + '\'' +
                ", description=" + description +
                ", price='" + price + '\'' +
                ", quantity=" + quantity +
                ", brand='" + brand + '\'' +
                ", size='" + size + '\'' +
                ", numberFormat=" + numberFormat +
                '}';
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Product product = (Product) obj;
        return productId == product.productId
                && categoryId == product.categoryId
                && name.equals(product.name)
                && image == product.image
                && description == product.description
                && price.equals(product.price)
                && quantity == product.quantity
                && brand.equals(product.brand)
                && size.equals(product.size);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId, categoryId, name, image, description, price, size, quantity, brand);
    }
}

