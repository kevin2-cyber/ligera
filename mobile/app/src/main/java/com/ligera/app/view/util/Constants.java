package com.ligera.app.view.util;

import com.ligera.app.R;
import com.ligera.app.model.entity.Product;

import java.util.ArrayList;

public class Constants {
    public static ArrayList<Product> getProductData() {
        ArrayList<Product> products = new ArrayList<>();
        products.add(new Product(0,"Hoodie", R.drawable.hoodie, R.string.contents, "2000.00", 2, 1, "Ligera","XL"));
        products.add(new Product(1,"Attire", R.drawable.attire, R.string.contents, "4000.00", 2, 2, "Ligera","XL"));
        products.add(new Product(2,"Done", R.drawable.done, R.string.contents, "8000.00", 2, 1, "Ligera","XL"));
        products.add(new Product(3,"Made", R.drawable.made, R.string.contents, "1000.00", 2, 2, "Ligera","XL"));
        products.add(new Product(4,"Shirt", R.drawable.shirt, R.string.contents, "1200.00", 2, 1, "Ligera","XL"));
        products.add(new Product(5,"Sown", R.drawable.sown, R.string.contents, "1400.00", 2, 2, "Ligera","XL"));
        products.add(new Product(6,"Style", R.drawable.style, R.string.contents, "1600.00", 2, 1, "Ligera","XL"));
        products.add(new Product(7,"Tailor", R.drawable.tailor, R.string.contents, "1800.00", 2, 2, "Ligera","XL"));
        products.add(new Product(8,"Worn", R.drawable.worn, R.string.contents, "6000.00", 2, 1, "Ligera","XL"));
        return products;
    }
}
