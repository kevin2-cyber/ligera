package com.ligera.app.viewmodel.state;

import androidx.annotation.Nullable;

import com.ligera.app.model.entity.Category;
import com.ligera.app.model.entity.Product;

import java.util.List;

public class ProductState {
    private final boolean loading;
    private final boolean refreshing;
    private final boolean offline;
    private final String error;
    private final List<Product> products;
    private final List<Product> featuredProducts;
    private final List<Product> popularProducts;
    private final Product productDetails;
    private final List<Category> categories;
    private final String searchQuery;

    private ProductState(Builder builder) {
        this.loading = builder.loading;
        this.refreshing = builder.refreshing;
        this.offline = builder.offline;
        this.error = builder.error;
        this.products = builder.products;
        this.featuredProducts = builder.featuredProducts;
        this.popularProducts = builder.popularProducts;
        this.productDetails = builder.productDetails;
        this.categories = builder.categories;
        this.searchQuery = builder.searchQuery;
    }

    public static ProductState loading() {
        return new Builder().loading(true).build();
    }

    public static class Builder {
        private boolean loading;
        private boolean refreshing;
        private boolean offline;
        private String error;
        private List<Product> products;
        private List<Product> featuredProducts;
        private List<Product> popularProducts;
        private Product productDetails;
        private List<Category> categories;
        private String searchQuery;

        public Builder loading(boolean loading) {
            this.loading = loading;
            return this;
        }

        public Builder refreshing(boolean refreshing) {
            this.refreshing = refreshing;
            return this;
        }

        public Builder offline(boolean offline) {
            this.offline = offline;
            return this;
        }

        public Builder error(String error) {
            this.error = error;
            return this;
        }

        public Builder products(List<Product> products) {
            this.products = products;
            return this;
        }

        public Builder featuredProducts(List<Product> featuredProducts) {
            this.featuredProducts = featuredProducts;
            return this;
        }

        public Builder popularProducts(List<Product> popularProducts) {
            this.popularProducts = popularProducts;
            return this;
        }

        public Builder productDetails(Product productDetails) {
            this.productDetails = productDetails;
            return this;
        }

        public Builder categories(List<Category> categories) {
            this.categories = categories;
            return this;
        }

        public Builder searchQuery(String searchQuery) {
            this.searchQuery = searchQuery;
            return this;
        }

        public ProductState build() {
            return new ProductState(this);
        }
    }

    public boolean isLoading() {
        return loading;
    }

    public boolean isRefreshing() {
        return refreshing;
    }

    public boolean isOffline() {
        return offline;
    }

    @Nullable
    public String getError() {
        return error;
    }

    @Nullable
    public List<Product> getProducts() {
        return products;
    }

    @Nullable
    public List<Product> getFeaturedProducts() {
        return featuredProducts;
    }

    @Nullable
    public List<Product> getPopularProducts() {
        return popularProducts;
    }

    @Nullable
    public Product getProductDetails() {
        return productDetails;
    }

    @Nullable
    public List<Category> getCategories() {
        return categories;
    }


    @Nullable
    public String getSearchQuery() {
        return searchQuery;
    }
}
