package com.ligera.app.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.ligera.app.R;
import com.ligera.app.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {

    ActivityLoginBinding binding;
    String email = "";
    String password = "";
    private InputMethodManager inputMethodManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        setContentView(binding.getRoot());

        binding.tvCreateAccount.setOnClickListener(view -> {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
        });

        binding.etEmail.setFocusable(false);
        inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        binding.etEmail.setOnClickListener(this::editTextClickMethod);

        binding.registerBtn.setOnClickListener(view -> {
            validateData();
        });
    }

    private void validateData() {
        // get data
        email = binding.etEmail.getText().toString().trim();
        password = binding.etPassword.getText().toString().trim();

        // validate user
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            // invalid email format
            binding.etEmail.setError("Invalid Email Address",
                    AppCompatResources.getDrawable(this, R.drawable.baseline_error_24));
        } else if (TextUtils.isEmpty(password)) {
            // no password entered
            binding.etPassword.setError("no password entered",
                    AppCompatResources.getDrawable(this, R.drawable.baseline_error_24));
        } else if (password.length() < 6) {
            binding.etPassword.setError("Password must be more than six characters",
                    AppCompatResources.getDrawable(this, R.drawable.baseline_error_24));
        } else {
            login();
        }
    }

    private void login() {}
    private void editTextClickMethod(View view) {
        inputMethodManager.hideSoftInputFromWindow(view.getRootView().getWindowToken(), 0);
    }
}