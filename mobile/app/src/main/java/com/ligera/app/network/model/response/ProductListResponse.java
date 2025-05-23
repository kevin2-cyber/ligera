package com.ligera.app.network.model.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Response model for paginated product list
 */
public class ProductListResponse {
    @SerializedName("products")
    private List<ProductResponse> products;
    
    @SerializedName("pageInfo")
    private PageInfo pageInfo;
    
    @SerializedName("filters")
    private ProductFilters filters;
    
    public ProductListResponse() {
    }
    
    public List<ProductResponse> getProducts() {
        return products;
    }
    
    public void setProducts(List<ProductResponse> products) {
        this.products = products;
    }
    
    public PageInfo getPageInfo() {
        return pageInfo;
    }
    
    public void setPageInfo(PageInfo pageInfo) {
        this.pageInfo = pageInfo;
    }
    
    public ProductFilters getFilters() {
        return filters;
    }
    
    public void setFilters(ProductFilters filters) {
        this.filters = filters;
    }
    
    /**
     * Inner class for available product filters
     */
    public static class ProductFilters {
        @SerializedName("categories")
        private List<CategoryResponse> categories;
        
        @SerializedName("brands")
        private List<String> brands;
        
        @SerializedName("colors")
        private List<String> colors;
        
        @SerializedName("sizes")
        private List<String> sizes;
        
        @SerializedName("priceRange")
        private PriceRange priceRange;
        
        public ProductFilters() {
        }
        
        public List<CategoryResponse> getCategories() {
            return categories;
        }
        
        public void setCategories(List<CategoryResponse> categories) {
            this.categories = categories;
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
        
        public PriceRange getPriceRange() {
            return priceRange;
        }
        
        public void setPriceRange(PriceRange priceRange) {
            this.priceRange = priceRange;
        }
    }
    
    /**
     * Inner class for price range information
     */
    public static class PriceRange {
        @SerializedName("min")
        private double min;
        
        @SerializedName("max")
        private double max;
        
        public PriceRange() {
        }
        
        public double getMin() {
            return min;
        }
        
        public void setMin(double min) {
            this.min = min;
        }
        
        public double getMax() {
            return max;
        }
        
        public void setMax(double max) {
            this.max = max;
        }
    }
}

