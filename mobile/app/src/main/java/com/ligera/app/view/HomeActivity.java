package com.ligera.app.view;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.ligera.app.R;
import com.ligera.app.databinding.ActivityHomeBinding;

public class HomeActivity extends AppCompatActivity {

    ActivityHomeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        setContentView(binding.getRoot());
    }
}