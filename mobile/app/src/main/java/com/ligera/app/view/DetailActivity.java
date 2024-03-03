package com.ligera.app.view;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.ligera.app.R;
import com.ligera.app.databinding.ActivityDetailBinding;
import com.ligera.app.model.entity.Product;

public class DetailActivity extends AppCompatActivity {
    private ActivityDetailBinding binding;
    private DetailHandler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_detail);
        Product product = new Product();
        binding.setProduct(product);

        handler =  new DetailHandler(this);
        binding.setHandler(handler);

    }

    public class DetailHandler {
        Context context;

        public DetailHandler(Context context) {
            this.context = context;
        }

        public void addToCart(View view) {}
    }
}