package com.ligera.app.view.util;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;

import com.ligera.app.model.entity.Product;

import java.util.ArrayList;

public class ProductDiffUtil extends DiffUtil.Callback {
    ArrayList<Product> oldProducts;
    ArrayList<Product> newProducts;

    public ProductDiffUtil(ArrayList<Product> oldProducts, ArrayList<Product> newProducts) {
        this.oldProducts = oldProducts;
        this.newProducts = newProducts;
    }


    @Override
    public int getOldListSize() {
        return oldProducts == null ? 0 : oldProducts.size();
    }


    @Override
    public int getNewListSize() {
        return newProducts == null ? 0 : newProducts.size();
    }


    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldProducts.get(oldItemPosition).getProductId() == newProducts.get(newItemPosition).getProductId();
    }


    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return oldProducts.get(oldItemPosition).equals(newProducts.get(newItemPosition));
    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        return super.getChangePayload(oldItemPosition, newItemPosition);
    }
}
