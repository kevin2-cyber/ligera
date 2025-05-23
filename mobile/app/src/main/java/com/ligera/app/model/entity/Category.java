package com.ligera.app.model.entity;

import androidx.annotation.NonNull;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.library.baseAdapters.BR;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Objects;

/**
 * Room entity for product categories
 */
@Entity(
    tableName = "categories",
    indices = {
        @Index("name"),
        @Index("parent_id"),
        @Index("position"),
        @Index("is_active"),
        @Index("display_in_menu"),
        @Index("slug_url"),
        @Index("created_at")
    }
)
public class Category extends BaseObservable {
    @PrimaryKey(autoGenerate = true)
    private long id;
    
    @ColumnInfo(name = "name")
    private String name;
    
    @ColumnInfo(name = "description")
    private String description;
    
    @ColumnInfo(name = "image_url")
    private String imageUrl;
    
    @ColumnInfo(name = "parent_id")
    private Long parentId;
    
    @ColumnInfo(name = "product_count")
    private int productCount;
    
    @ColumnInfo(name = "position")
    private int position;
    
    // Tracking fields
    @ColumnInfo(name = "created_at")
    private long createdAt;
    
    @ColumnInfo(name = "last_updated")
    private long lastUpdated;
    
    @ColumnInfo(name = "last_refreshed")
    private long lastRefreshed;
    
    // Additional features
    @ColumnInfo(name = "is_active")
    private boolean isActive = true;
    
    @ColumnInfo(name = "display_in_menu")
    private boolean displayInMenu = true;
    
    @ColumnInfo(name = "meta_title")
    private String metaTitle;
    
    @ColumnInfo(name = "meta_description")
    private String metaDescription;
    
    @ColumnInfo(name = "slug_url")
    private String slugUrl;

    @Ignore
    public Category() {
        this.createdAt = System.currentTimeMillis();
        this.lastUpdated = System.currentTimeMillis();
        this.lastRefreshed = System.currentTimeMillis();
    }

    public Category(long id, String name, String description, String imageUrl, Long parentId, int position) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.parentId = parentId;
        this.position = position;
        this.createdAt = System.currentTimeMillis();
        this.lastUpdated = System.currentTimeMillis();
        this.lastRefreshed = System.currentTimeMillis();
        
        // Generate a slug URL from the name
        this.slugUrl = name.toLowerCase().replace(' ', '-').replaceAll("[^a-z0-9-]", "");
        this.metaTitle = name;
    }

    @Bindable
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
        notifyPropertyChanged(BR.id);
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
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
        notifyPropertyChanged(BR.description);
    }

    @Bindable
    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
        notifyPropertyChanged(BR.imageUrl);
    }

    @Bindable
    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
        notifyPropertyChanged(BR.parentId);
    }

    @Bindable
    public int getProductCount() {
        return productCount;
    }

    public void setProductCount(int productCount) {
        this.productCount = productCount;
        notifyPropertyChanged(BR.productCount);
    }

    @Bindable
    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
        notifyPropertyChanged(BR.position);
    }

    @Bindable
    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
        notifyPropertyChanged(BR.createdAt);
    }

    @Bindable
    public long getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(long lastUpdated) {
        this.lastUpdated = lastUpdated;
        notifyPropertyChanged(BR.lastUpdated);
    }

    @Bindable
    public long getLastRefreshed() {
        return lastRefreshed;
    }

    public void setLastRefreshed(long lastRefreshed) {
        this.lastRefreshed = lastRefreshed;
        notifyPropertyChanged(BR.lastRefreshed);
    }

    @Bindable
    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
        notifyPropertyChanged(BR.active);
    }

    @Bindable
    public boolean isDisplayInMenu() {
        return displayInMenu;
    }

    public void setDisplayInMenu(boolean displayInMenu) {
        this.displayInMenu = displayInMenu;
        notifyPropertyChanged(BR.displayInMenu);
    }

    @Bindable
    public String getMetaTitle() {
        return metaTitle;
    }

    public void setMetaTitle(String metaTitle) {
        this.metaTitle = metaTitle;
        notifyPropertyChanged(BR.metaTitle);
    }

    @Bindable
    public String getMetaDescription() {
        return metaDescription;
    }

    public void setMetaDescription(String metaDescription) {
        this.metaDescription = metaDescription;
        notifyPropertyChanged(BR.metaDescription);
    }

    @Bindable
    public String getSlugUrl() {
        return slugUrl;
    }

    public void setSlugUrl(String slugUrl) {
        this.slugUrl = slugUrl;
        notifyPropertyChanged(BR.slugUrl);
    }

    @NonNull
    @Override
    public String toString() {
        return "Category{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", parentId=" + parentId +
                ", productCount=" + productCount +
                ", position=" + position +
                ", isActive=" + isActive +
                ", displayInMenu=" + displayInMenu +
                ", slugUrl='" + slugUrl + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return id == category.id
                && Objects.equals(name, category.name)
                && Objects.equals(description, category.description)
                && Objects.equals(imageUrl, category.imageUrl)
                && Objects.equals(parentId, category.parentId)
                && Objects.equals(slugUrl, category.slugUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, imageUrl, parentId, slugUrl);
    }
    
    /**
     * Check if this category is a root category (has no parent)
     * 
     * @return true if this is a root category
     */
    @Bindable
    public boolean isRootCategory() {
        return parentId == null;
    }
    
    /**
     * Check if this category has children
     * This is a helper method and doesn't actually query the database
     * 
     * @return true if this category might have children
     */
    @Bindable
    public boolean hasChildren() {
        // This is an estimate - actual children count would need to be queried from database
        return true;
    }
}
