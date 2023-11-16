package com.ligera.app.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.ligera.app.R;
import com.ligera.app.databinding.ActivityRegisterBinding;

public class RegisterActivity extends AppCompatActivity {

    ActivityRegisterBinding binding;
    String name = "";
    String email = "";
    String password = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        setContentView(binding.getRoot());

        binding.registerBtn.setOnClickListener(view -> {
            validateData();
        });

        binding.tvLogin.setOnClickListener(view -> {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        });
    }

    private void validateData() {
        name = binding.etName.getText().toString();
        email = binding.etEmail.getText().toString();
        password = binding.etPassword.getText().toString();

        // validate the data
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            // invalid email format
            binding.etEmail.setError("Invalid Email format",
                    AppCompatResources.getDrawable(this, R.drawable.baseline_error_24));
        } else if (TextUtils.isEmpty(password)) {
            // password isn't entered
            binding.etPassword.setError("Please enter your password",
                    AppCompatResources.getDrawable(this, R.drawable.baseline_error_24));
        } else if (password.length() < 6) {
            binding.etPassword.setError("Please enter at least 6 characters long",
                    AppCompatResources.getDrawable(this, R.drawable.baseline_error_24));
        } else if (name.isEmpty()) {
            binding.etName.setError("Please enter your name",
                    AppCompatResources.getDrawable(this, R.drawable.baseline_error_24));
        } else register();
    }

    private void register() {}

    private void togglePassword(View view) {
        if (view.getTag() == binding.etPassword.getCompoundDrawables()) {
            if(binding.etPassword.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())) {
                ((ImageView) (view)).setImageResource(R.drawable.lock);
                // show password
                binding.etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            } else {
                ((ImageView) (view)).setImageResource(R.drawable.lock);
                // Hide password
                binding.etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        }
    }
}