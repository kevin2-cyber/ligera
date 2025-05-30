package com.ligera.app.network.model.response;

import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;
import java.util.List;

/**
 * Response model for a product
 */
public class ProductResponse {
    @SerializedName("id")
    private long id;
    
    @SerializedName("name")
    private String name;
    
    @SerializedName("description")
    private String description;
    
    @SerializedName("price")
    private BigDecimal price;
    
    @SerializedName("image")
    private String image;
    
    @SerializedName("images")
    private List<String> images;
    
    @SerializedName("categoryId")
    private long categoryId;
    
    @SerializedName("categoryName")
    private String categoryName;
    
    @SerializedName("rating")
    private float rating;
    
    @SerializedName("reviewCount")
    private int reviewCount;
    
    @SerializedName("inStock")
    private boolean inStock;
    
    @SerializedName("quantity")
    private int quantity;
    
    @SerializedName("brand")
    private String brand;
    
    @SerializedName("size")
    private String size;
    
    @SerializedName("color")
    private String color;
    
    @SerializedName("featured")
    private boolean featured;
    
    @SerializedName("popular")
    private boolean popular;
    
    @SerializedName("discountPercentage")
    private int discountPercentage;
    
    @SerializedName("tags")
    private List<String> tags;
    
    @SerializedName("createdAt")
    private String createdAt;
    
    @SerializedName("updatedAt")
    private String updatedAt;
    
    public ProductResponse() {
    }
    
    public long getId() {
        return id;
    }
    
    public void setId(long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
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
    
    public long getCategoryId() {
        return categoryId;
    }
    
    public void setCategoryId(long categoryId) {
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
}

