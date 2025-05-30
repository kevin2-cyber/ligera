package com.ligera.app.network.model.request;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Request model for product filtering
 */
public class ProductFilterRequest {
    @SerializedName("categoryId")
    private Long categoryId;
    
    @SerializedName("brands")
    private List<String> brands;
    
    @SerializedName("colors")
    private List<String> colors;
    
    @SerializedName("sizes")
    private List<String> sizes;
    
    @SerializedName("minPrice")
    private Double minPrice;
    
    @SerializedName("maxPrice")
    private Double maxPrice;
    
    @SerializedName("query")
    private String query;
    
    @SerializedName("inStock")
    private Boolean inStock;
    
    @SerializedName("featured")
    private Boolean featured;
    
    @SerializedName("popular")
    private Boolean popular;
    
    @SerializedName("sortBy")
    private String sortBy;
    
    @SerializedName("sortDirection")
    private String sortDirection;
    
    public ProductFilterRequest() {
    }
    
    public Long getCategoryId() {
        return categoryId;
    }
    
    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }
    
    public List<String> getBrands() {
        return brands;
    }
    
    public void setBrands(List<String> brands) {
        this.brands = brands;
    }
    
    public List<String> getColors() {
        return colors;
    }
    
    public void setColors(List<String> colors) {
        this.colors = colors;
    }
    
    public List<String> getSizes() {
        return sizes;
    }
    
    public void setSizes(List<String> sizes) {
        this.sizes = sizes;
    }
    
    public Double getMinPrice() {
        return minPrice;
    }
    
    public void setMinPrice(Double minPrice) {
        this.minPrice = minPrice;
    }
    
    public Double getMaxPrice() {
        return maxPrice;
    }
    
    public void setMaxPrice(Double maxPrice) {
        this.maxPrice = maxPrice;
    }
    
    public String getQuery() {
        return query;
    }
    
    public void setQuery(String query) {
        this.query = query;
    }
    
    public Boolean getInStock() {
        return inStock;
    }
    
    public void setInStock(Boolean inStock) {
        this.inStock = inStock;
    }
    
    public Boolean getFeatured() {
        return featured;
    }
    
    public void setFeatured(Boolean featured) {
        this.featured = featured;
    }
    
    public Boolean getPopular() {
        return popular;
    }
    
    public void setPopular(Boolean popular) {
        this.popular = popular;
    }
    
    public String getSortBy() {
        return sortBy;
    }
    
    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }
    
    public String getSortDirection() {
        return sortDirection;
    }
    
    public void setSortDirection(String sortDirection) {
        this.sortDirection = sortDirection;
    }
    
    /**
     * Builder pattern for creating ProductFilterRequest instances
     */
    public static class Builder {
        private final ProductFilterRequest request;
        
        public Builder() {
            request = new ProductFilterRequest();
        }
        
        public Builder categoryId(Long categoryId) {
            request.setCategoryId(categoryId);
            return this;
        }
        
        public Builder brands(List<String> brands) {
            request.setBrands(brands);
            return this;
        }
        
        public Builder colors(List<String> colors) {
            request.setColors(colors);
            return this;
        }
        
        public Builder sizes(List<String> sizes) {
            request.setSizes(sizes);
            return this;
        }
        
        public Builder priceRange(Double minPrice, Double maxPrice) {
            request.setMinPrice(minPrice);
            request.setMaxPrice(maxPrice);
            return this;
        }
        
        public Builder query(String query) {
            request.setQuery(query);
            return this;
        }
        
        public Builder inStock(Boolean inStock) {
            request.setInStock(inStock);
            return this;
        }
        
        public Builder featured(Boolean featured) {
            request.setFeatured(featured);
            return this;
        }
        
        public Builder popular(Boolean popular) {
            request.setPopular(popular);
            return this;
        }
        
        public Builder sort(String sortBy, String sortDirection) {
            request.setSortBy(sortBy);
            request.setSortDirection(sortDirection);
            return this;
        }
        
        public ProductFilterRequest build() {
            return request;
        }
    }
}
