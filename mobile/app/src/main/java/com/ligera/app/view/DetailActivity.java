package com.ligera.app.view;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.ligera.app.R;
import com.ligera.app.databinding.ActivityDetailBinding;
import com.ligera.app.model.entity.Product;

public class DetailActivity extends AppCompatActivity {
    private ActivityDetailBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_detail);
        Product product = new Product();
        binding.setProduct(product);
    }
}