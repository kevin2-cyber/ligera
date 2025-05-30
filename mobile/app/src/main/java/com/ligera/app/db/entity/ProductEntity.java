package com.ligera.app.db.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.ligera.app.db.converter.StringListConverter;

import java.math.BigDecimal;
import java.util.List;

/**
 * Room entity for products
 */
@Entity(
    tableName = "products",
    foreignKeys = {
        @ForeignKey(
            entity = CategoryEntity.class,
            parentColumns = "id",
            childColumns = "category_id",
            onDelete = ForeignKey.SET_NULL
        )
    },
    indices = {
        @Index("category_id"),
        @Index("name"),
        @Index("brand"),
        @Index("featured"),
        @Index("popular")
    }
)
@TypeConverters(StringListConverter.class)
public class ProductEntity {
    @PrimaryKey
    private long id;
    
    @NonNull
    @ColumnInfo(name = "name")
    private String name;
    
    @ColumnInfo(name = "description")
    private String description;
    
    @ColumnInfo(name = "price")
    private BigDecimal price;
    
    @ColumnInfo(name = "image")
    private String image;
    
    @ColumnInfo(name = "images")
    private List<String> images;
    
    @ColumnInfo(name = "category_id")
    private Long categoryId;
    
    @ColumnInfo(name = "category_name")
    private String categoryName;
    
    @ColumnInfo(name = "rating")
    private float rating;
    
    @ColumnInfo(name = "review_count")
    private int reviewCount;
    
    @ColumnInfo(name = "in_stock")
    private boolean inStock;
    
    @ColumnInfo(name = "quantity")
    private int quantity;
    
    @ColumnInfo(name = "brand")
    private String brand;
    
    @ColumnInfo(name = "size")
    private String size;
    
    @ColumnInfo(name = "color")
    private String color;
    
    @ColumnInfo(name = "featured")
    private boolean featured;
    
    @ColumnInfo(name = "popular")
    private boolean popular;
    
    @ColumnInfo(name = "discount_percentage")
    private int discountPercentage;
    
    @ColumnInfo(name = "tags")
    private List<String> tags;
    
    @ColumnInfo(name = "created_at")
    private String createdAt;
    
    @ColumnInfo(name = "updated_at")
    private String updatedAt;
    
    @ColumnInfo(name = "last_refreshed")
    private long lastRefreshed;

    public ProductEntity() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public int getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(int reviewCount) {
        this.reviewCount = reviewCount;
    }

    public boolean isInStock() {
        return inStock;
    }

    public void setInStock(boolean inStock) {
        this.inStock = inStock;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public boolean isFeatured() {
        return featured;
    }

    public void setFeatured(boolean featured) {
        this.featured = featured;
    }

    public boolean isPopular() {
        return popular;
    }

    public void setPopular(boolean popular) {
        this.popular = popular;
    }

    public int getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(int discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public long getLastRefreshed() {
        return lastRefreshed;
    }

    public void setLastRefreshed(long lastRefreshed) {
        this.lastRefreshed = lastRefreshed;
    }
}

