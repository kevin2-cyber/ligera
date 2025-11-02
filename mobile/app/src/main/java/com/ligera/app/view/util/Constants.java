package com.ligera.app.view.util;

import com.ligera.app.model.entity.Product;

import java.math.BigDecimal;
import java.util.ArrayList;

public class Constants {
    /**
     * Provides a list of sample products. 
     * Note: This is mock data and may not reflect the actual data structure from the API.
     */
    public static ArrayList<Product> getProductData() {
        ArrayList<Product> products = new ArrayList<>();
        String placeholderDesc = "Lorem Ipsum is simply dummy text of the printing and typesetting industry.";
        
        // Create URIs for drawable resources
        // Assumes you have drawables named hoodie, attire, etc. in your res/drawable folder
        String drawableUriBase = "android.resource://com.ligera.app/drawable/";

        products.add(new Product(0L, "Hoodie", placeholderDesc, new BigDecimal("2000.00"), drawableUriBase + "hoodie", 2, 1L, "Ligera", "XL"));
        products.add(new Product(1L, "Attire", placeholderDesc, new BigDecimal("4000.00"), drawableUriBase + "attire", 2, 2L, "Ligera", "XL"));
        products.add(new Product(2L, "Done", placeholderDesc, new BigDecimal("8000.00"), drawableUriBase + "done", 2, 1L, "Ligera", "XL"));
        products.add(new Product(3L, "Made", placeholderDesc, new BigDecimal("1000.00"), drawableUriBase + "made", 2, 2L, "Ligera", "XL"));
        products.add(new Product(4L, "Shirt", placeholderDesc, new BigDecimal("1200.00"), drawableUriBase + "shirt", 2, 1L, "Ligera", "XL"));
        products.add(new Product(5L, "Sown", placeholderDesc, new BigDecimal("1400.00"), drawableUriBase + "sown", 2, 2L, "Ligera", "XL"));
        products.add(new Product(6L, "Style", placeholderDesc, new BigDecimal("1600.00"), drawableUriBase + "style", 2, 1L, "Ligera", "XL"));
        products.add(new Product(7L, "Tailor", placeholderDesc, new BigDecimal("1800.00"), drawableUriBase + "tailor", 2, 2L, "Ligera", "XL"));
        products.add(new Product(8L, "Worn", placeholderDesc, new BigDecimal("6000.00"), drawableUriBase + "worn", 2, 1L, "Ligera", "XL"));
        
        return products;
    }
}
