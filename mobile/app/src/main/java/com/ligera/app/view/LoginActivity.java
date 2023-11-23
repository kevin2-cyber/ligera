package com.ligera.app.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.Toast;

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

        // shift the LinearLayout up when any of the EditText is selected
        binding.etEmail.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                binding.lowerSection.setTranslationY(-620f);
                return false;
            }
        });

        binding.etEmail.setOnEditorActionListener((v, actionId, event) -> {
            if (event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(binding.etEmail.getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
            // when it is done put the LinearLayout back
            binding.lowerSection.setTranslationY(0f);
            return false;
        });

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
//                    AppCompatResources.getDrawable(this, R.drawable.baseline_error_24)
                    ContextCompat.getDrawable(this, R.drawable.baseline_error_24)
            );
        } else if (TextUtils.isEmpty(password)) {
            // no password entered
            binding.etPassword.setError("no password entered",
//                    AppCompatResources.getDrawable(this, R.drawable.baseline_error_24)
                    ContextCompat.getDrawable(this, R.drawable.baseline_error_24)
            );
        } else if (password.length() < 6) {
            binding.etPassword.setError("Password must be more than six characters",
//                    AppCompatResources.getDrawable(this, R.drawable.baseline_error_24)
                    ContextCompat.getDrawable(this, R.drawable.baseline_error_24)
            );
        } else {
            login();
        }
    }

    private void login() {
        email = binding.etEmail.getText().toString().trim();
        password = binding.etPassword.getText().toString().trim();

        if (email.equals("kimikevin@zoho.com") && password.equals("asdfzxcvbnm")) {
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Wrong credentials", Toast.LENGTH_SHORT).show();
        }
    }

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

    public class LoginActivityClickHandler {
        Context context;

        public LoginActivityClickHandler(Context context) {
            this.context = context;
        }


    }
}