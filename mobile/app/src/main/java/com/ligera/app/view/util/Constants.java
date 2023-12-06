package com.ligera.app.view.util;

import com.ligera.app.R;
import com.ligera.app.model.entity.Product;

import java.util.ArrayList;

public class Constants {
    public static ArrayList<Product> getProductData() {
        ArrayList<Product> products = new ArrayList<>();
        products.add(new Product("Hoodie", R.drawable.hoodie, "Hello", "200", 2, 1, "Ligera","XL"));
        products.add(new Product("Hoodie", R.drawable.hoodie, "Hello", "200", 2, 1, "Ligera","XL"));
        products.add(new Product("Hoodie", R.drawable.hoodie, "Hello", "200", 2, 1, "Ligera","XL"));
        products.add(new Product("Hoodie", R.drawable.hoodie, "Hello", "200", 2, 1, "Ligera","XL"));
        products.add(new Product("Hoodie", R.drawable.hoodie, "Hello", "200", 2, 1, "Ligera","XL"));
        products.add(new Product("Hoodie", R.drawable.hoodie, "Hello", "200", 2, 1, "Ligera","XL"));
        products.add(new Product("Hoodie", R.drawable.hoodie, "Hello", "200", 2, 1, "Ligera","XL"));
        products.add(new Product("Hoodie", R.drawable.hoodie, "Hello", "200", 2, 1, "Ligera","XL"));
        return products;
    }
}
