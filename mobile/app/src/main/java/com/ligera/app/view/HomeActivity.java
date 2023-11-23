package com.ligera.app.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.ligera.app.R;
import com.ligera.app.databinding.ActivityHomeBinding;
import com.ligera.app.model.entity.Product;

public class HomeActivity extends AppCompatActivity {

    ActivityHomeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Product product = new Product();
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home);
        binding.setProduct(product);
    }
}