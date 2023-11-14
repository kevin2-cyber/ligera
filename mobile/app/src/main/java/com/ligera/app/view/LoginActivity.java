package com.ligera.app.view;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.ligera.app.R;
import com.ligera.app.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {

    ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        setContentView(binding.getRoot());
    }
}