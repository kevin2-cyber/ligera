package com.ligera.app.view;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ligera.app.R;
import com.ligera.app.databinding.ActivityRegisterBinding;

import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    ActivityRegisterBinding binding;
    private ProgressBar bar;
    private FirebaseAuth auth;
    String name = "";
    String email = "";
    String password = "";
    private InputMethodManager inputMethodManager;

    RegisterActivityClickHandler clickHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.register),
                (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding = DataBindingUtil.setContentView(this, R.layout.activity_register);

        clickHandler = new RegisterActivityClickHandler(this);
        binding.setRegisterClickHandler(clickHandler);

        binding.etEmail.setOnTouchListener((v, event) -> {
            binding.upperSection.setVisibility(View.INVISIBLE);
            binding.lowerSection.setTranslationY(-530f);
            return false;
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

        clickHandler = new RegisterActivityClickHandler(this);
        binding.setRegisterClickHandler(clickHandler);

        // configure progress dialog
        bar = new ProgressBar(this);
        bar.setVisibility(View.GONE);

        binding.tvLogin.setOnClickListener(view -> {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        });

        auth = FirebaseAuth.getInstance();
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

    @Override
    public boolean onSupportNavigateUp() {
        // go back to previous activity, when back button of actionbar clicked
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    public class RegisterActivityClickHandler {
        Context context;

        public RegisterActivityClickHandler(Context context) {
            this.context = context;
        }

        public void validateData(View view) {
            name = binding.etName.getText().toString();
            email = binding.etEmail.getText().toString();
            password = Objects.requireNonNull(binding.etPassword.getText()).toString();

            // validate the data
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                // invalid email format
                binding.etEmail.setError("Invalid Email format",
//                    AppCompatResources.getDrawable(this, R.drawable.baseline_error_24)
                        ContextCompat.getDrawable(RegisterActivity.this, R.drawable.baseline_error_24)
                );
            } else if (TextUtils.isEmpty(password)) {
                // password isn't entered
                binding.etPassword.setError("Please enter your password",
//                    AppCompatResources.getDrawable(this, R.drawable.baseline_error_24)
                        ContextCompat.getDrawable(RegisterActivity.this, R.drawable.baseline_error_24)
                );
            } else if (password.length() < 6) {
                binding.etPassword.setError("Please enter at least 6 characters long",
//                    AppCompatResources.getDrawable(this, R.drawable.baseline_error_24)
                        ContextCompat.getDrawable(RegisterActivity.this, R.drawable.baseline_error_24)
                );
            } else if (name.isEmpty()) {
                binding.etName.setError("Please enter your name",
//                    AppCompatResources.getDrawable(this, R.drawable.baseline_error_24)
                        ContextCompat.getDrawable(RegisterActivity.this, R.drawable.baseline_error_24)
                );
            } else register();
        }

        private void register() {
            //show progress
            bar.setVisibility(View.VISIBLE);

            // create account
            auth.createUserWithEmailAndPassword(email, password)
                    .addOnSuccessListener(task -> {

                        // dismiss progress
                        bar.setVisibility(View.GONE);
                        FirebaseUser user = auth.getCurrentUser();
                        assert user != null;
                        String email = user.getEmail();
                        Toast.makeText(RegisterActivity.this, "Account created with " + email, Toast.LENGTH_LONG).show();

                        startActivity(new Intent(RegisterActivity.this, HomeActivity.class));
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        //signup failed
                        bar.setVisibility(View.GONE);
                        Toast.makeText(RegisterActivity.this, "Sign up failed due to " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
        }
    }
}