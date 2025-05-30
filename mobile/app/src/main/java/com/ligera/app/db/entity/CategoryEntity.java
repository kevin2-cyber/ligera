package com.ligera.app.db.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/**
 * Room entity for product categories
 */
@Entity(
    tableName = "categories",
    indices = {
        @Index("name"),
        @Index("parent_id")
    }
)
public class CategoryEntity {
    @PrimaryKey
    private long id;
    
    @NonNull
    @ColumnInfo(name = "name")
    private String name;
    
    @ColumnInfo(name = "description")
    private String description;
    
    @ColumnInfo(name = "image")
    private String image;
    
    @ColumnInfo(name = "parent_id")
    private Long parentId;
    
    @ColumnInfo(name = "product_count")
    private int productCount;
    
    @ColumnInfo(name = "last_refreshed")
    private long lastRefreshed;

    public CategoryEntity() {
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public int getProductCount() {
        return productCount;
    }

    public void setProductCount(int productCount) {
        this.productCount = productCount;
    }

    public long getLastRefreshed() {
        return lastRefreshed;
    }

    public void setLastRefreshed(long lastRefreshed) {
        this.lastRefreshed = lastRefreshed;
    }
}

