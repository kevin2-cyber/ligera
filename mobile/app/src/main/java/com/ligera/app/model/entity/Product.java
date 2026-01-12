package com.ligera.app.model.entity;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import com.ligera.app.BR;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.ligera.app.model.converter.BigDecimalConverter;
import com.ligera.app.model.converter.StringListConverter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Room entity for products
 */
@Entity(
    tableName = "products",
    foreignKeys = {
        @ForeignKey(
            entity = Category.class,
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
        @Index("popular"),
        @Index("is_favorite"),
        @Index("discount_percent"),
        @Index("rating")
    }
)
@TypeConverters({StringListConverter.class, BigDecimalConverter.class})
public class Product extends BaseObservable implements Parcelable {
    @PrimaryKey(autoGenerate = true)
    private long id;
    
    @NonNull
    @ColumnInfo(name = "name")
    private String name = "";
    
    @ColumnInfo(name = "description")
    private String description = "";
    
    @ColumnInfo(name = "price")
    private BigDecimal price = BigDecimal.ZERO;
    
    @ColumnInfo(name = "image_url")
    private String imageUrl;
    
    @ColumnInfo(name = "quantity")
    private int quantity = 0;
    
    @ColumnInfo(name = "category_id")
    private Long categoryId;
    
    @ColumnInfo(name = "brand")
    private String brand = "";
    
    @ColumnInfo(name = "size")
    private String size = "";
    
    @ColumnInfo(name = "featured")
    private boolean featured = false;

    @ColumnInfo(name = "popular")
    private boolean popular = false;

    @ColumnInfo(name = "is_favorite")
    private boolean isFavorite = false;
    
    @ColumnInfo(name = "discount_percent")
    private int discountPercent = 0;
    
    @ColumnInfo(name = "rating")
    private float rating = 0f;
    
    @ColumnInfo(name = "rating_count")
    private int ratingCount = 0;
    
    @ColumnInfo(name = "created_at")
    private long createdAt = System.currentTimeMillis();
    
    @ColumnInfo(name = "last_updated")
    private long lastUpdated = System.currentTimeMillis();
    
    @ColumnInfo(name = "last_refreshed")
    private long lastRefreshed = System.currentTimeMillis();
    
    @ColumnInfo(name = "popularity_score")
    private int popularityScore = 0;
    
    @TypeConverters(StringListConverter.class)
    @ColumnInfo(name = "image_urls")
    private List<String> imageUrls = new ArrayList<>();
    
    @TypeConverters(StringListConverter.class)
    @ColumnInfo(name = "tags")
    private List<String> tags = new ArrayList<>();
    
    @Ignore
    public Product() {}
    
    public Product(long id, @NonNull String name, String description, BigDecimal price, 
                  String imageUrl, int quantity, Long categoryId, String brand, String size) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageUrl = imageUrl;
        this.quantity = quantity;
        this.categoryId = categoryId;
        this.brand = brand;
        this.size = size;
        this.createdAt = System.currentTimeMillis();
        this.lastUpdated = System.currentTimeMillis();
        this.lastRefreshed = System.currentTimeMillis();
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
    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
        notifyPropertyChanged(BR.price);
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
    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
        notifyPropertyChanged(BR.quantity);
    }

    @Bindable
    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
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

    @Bindable
    public boolean isFeatured() {
        return featured;
    }

    public void setFeatured(boolean featured) {
        this.featured = featured;
        notifyPropertyChanged(BR.featured);
    }

    @Bindable
    public boolean isPopular() {
        return popular;
    }

    public void setPopular(boolean popular) {
        this.popular = popular;
        notifyPropertyChanged(BR.popular);
    }

    @Bindable
    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean isFavorite) {
        this.isFavorite = isFavorite;
        notifyPropertyChanged(BR.favorite);
    }

    @Bindable
    public int getDiscountPercent() {
        return discountPercent;
    }

    public void setDiscountPercent(int discountPercent) {
        this.discountPercent = discountPercent;
        notifyPropertyChanged(BR.discountPercent);
    }

    @Bindable
    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
        notifyPropertyChanged(BR.rating);
    }

    @Bindable
    public int getRatingCount() {
        return ratingCount;
    }

    public void setRatingCount(int ratingCount) {
        this.ratingCount = ratingCount;
        notifyPropertyChanged(BR.ratingCount);
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
    public int getPopularityScore() {
        return popularityScore;
    }

    public void setPopularityScore(int popularityScore) {
        this.popularityScore = popularityScore;
        notifyPropertyChanged(BR.popularityScore);
    }

    @Bindable
    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
        notifyPropertyChanged(BR.imageUrls);
    }

    @Bindable
    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
        notifyPropertyChanged(BR.tags);
    }

    /**
     * Calculate the discounted price
     * 
     * @return The price after applying the discount
     */
    @Bindable
    public BigDecimal getDiscountedPrice() {
        if (discountPercent <= 0) {
            return price;
        }
        
        BigDecimal discountMultiplier = new BigDecimal("1.0").subtract(
                new BigDecimal(discountPercent).divide(new BigDecimal("100")));
        return price.multiply(discountMultiplier);
    }

    @NonNull
    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", imageUrl='" + imageUrl + '\'' +
                ", quantity=" + quantity +
                ", categoryId=" + categoryId +
                ", brand='" + brand + '\'' +
                ", size='" + size + '\'' +
                ", featured=" + featured +
                ", popular=" + popular +
                ", isFavorite=" + isFavorite +
                ", discountPercent=" + discountPercent +
                ", rating=" + rating +
                ", ratingCount=" + ratingCount +
                '}';
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Product product = (Product) obj;
        return id == product.id &&
                quantity == product.quantity &&
                isFavorite == product.isFavorite &&
                Objects.equals(categoryId, product.categoryId) &&
                Objects.equals(name, product.name) &&
                Objects.equals(description, product.description) &&
                Objects.equals(price, product.price) &&
                Objects.equals(brand, product.brand) &&
                Objects.equals(size, product.size);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, price, quantity, categoryId, brand, size, isFavorite);
    }

    // Parcelable implementation
    @Ignore
    protected Product(Parcel in) {
        id = in.readLong();
        String nameStr = in.readString();
        name = nameStr != null ? nameStr : "";
        description = in.readString();
        String priceStr = in.readString();
        price = priceStr != null ? new BigDecimal(priceStr) : BigDecimal.ZERO;
        imageUrl = in.readString();
        quantity = in.readInt();
        categoryId = in.readByte() == 0 ? null : in.readLong();
        brand = in.readString();
        size = in.readString();
        featured = in.readByte() != 0;
        popular = in.readByte() != 0;
        isFavorite = in.readByte() != 0;
        discountPercent = in.readInt();
        rating = in.readFloat();
        ratingCount = in.readInt();
        createdAt = in.readLong();
        lastUpdated = in.readLong();
        lastRefreshed = in.readLong();
        popularityScore = in.readInt();
        imageUrls = in.createStringArrayList();
        tags = in.createStringArrayList();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeString(price != null ? price.toString() : null);
        dest.writeString(imageUrl);
        dest.writeInt(quantity);
        if (categoryId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(categoryId);
        }
        dest.writeString(brand);
        dest.writeString(size);
        dest.writeByte((byte) (featured ? 1 : 0));
        dest.writeByte((byte) (popular ? 1 : 0));
        dest.writeByte((byte) (isFavorite ? 1 : 0));
        dest.writeInt(discountPercent);
        dest.writeFloat(rating);
        dest.writeInt(ratingCount);
        dest.writeLong(createdAt);
        dest.writeLong(lastUpdated);
        dest.writeLong(lastRefreshed);
        dest.writeInt(popularityScore);
        dest.writeStringList(imageUrls);
        dest.writeStringList(tags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Product> CREATOR = new Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };
}
