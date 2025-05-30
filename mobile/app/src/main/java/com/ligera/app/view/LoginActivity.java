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
import android.transition.Explode;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.ligera.app.R;
import com.ligera.app.databinding.ActivityLoginBinding;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    ActivityLoginBinding binding;
    private ProgressBar bar;

    String email = "";
    String password = "";
    private InputMethodManager inputMethodManager;
    LoginClickHandler clickHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        getWindow().requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);

        getWindow().setExitTransition(new Explode());
        setContentView(R.layout.activity_login);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);

        clickHandler = new LoginClickHandler(this);
        binding.setLoginClickHandler(clickHandler);

        // configure progressbar
        bar = new ProgressBar(this);
        bar.setVisibility(View.GONE);


        binding.tvCreateAccount.setOnClickListener(view -> {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
        });

        // shift the LinearLayout up when any of the EditText is selected
        binding.etEmail.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                binding.upperSection.setVisibility(View.INVISIBLE);
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

    public class LoginClickHandler {
        Context context;

        public LoginClickHandler(Context context) {
            this.context = context;
        }

        // validate date from input
        public void validateData(View view) {
            // get data
            email = binding.etEmail.getText().toString().trim();
            password = Objects.requireNonNull(binding.etPassword.getText()).toString().trim();

            // validate user
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                // invalid email format
                binding.etEmail.setError("Invalid Email Address",
//                    AppCompatResources.getDrawable(this, R.drawable.baseline_error_24)
                        ContextCompat.getDrawable(LoginActivity.this, R.drawable.baseline_error_24)
                );
            } else if (TextUtils.isEmpty(password)) {
                // no password entered
                binding.etPassword.setError("no password entered",
//                    AppCompatResources.getDrawable(this, R.drawable.baseline_error_24)
                        ContextCompat.getDrawable(LoginActivity.this, R.drawable.baseline_error_24)
                );
            } else if (password.length() < 6) {
                binding.etPassword.setError("Password must be more than six characters",
//                    AppCompatResources.getDrawable(this, R.drawable.baseline_error_24)
                        ContextCompat.getDrawable(LoginActivity.this, R.drawable.baseline_error_24)
                );
            } else {
                // proceed to login
                login();
            }
        }

        public void login() {
//            // show progress
//            bar.setVisibility(View.VISIBLE);
//            bar.setVisibility(View.GONE);

            // open profile
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();

        }
    }
}