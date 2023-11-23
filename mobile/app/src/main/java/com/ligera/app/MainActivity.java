package com.ligera.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnticipateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ligera.app.databinding.ActivityMainBinding;
import com.ligera.app.model.Onboarding;
import com.ligera.app.view.RegisterActivity;
import com.ligera.app.view.adapter.OnboardingAdapter;
import com.ligera.app.view.anim.DepthPageTransformer;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    ViewPager2 onboardingViewPager;
    OnboardingAdapter onboardingAdapter;
    Button shopNowBtn;
    LinearLayout onboardingIndicators;

    List<Onboarding> onboardings;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_Ligera);
        getSplashScreen().setOnExitAnimationListener(splashScreenView -> {
            final ObjectAnimator slideUp = ObjectAnimator.ofFloat(
                    splashScreenView,
                    View.TRANSLATION_Y,
                    0f,
                    -splashScreenView.getHeight()
            );
            slideUp.setInterpolator(new AnticipateInterpolator());
            slideUp.setDuration(500L);

            // Call SplashScreenView.remove at the end of your custom animation.
            slideUp.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    splashScreenView.remove();
                }
            });

            // Run your animation.
            slideUp.start();
        });
        setContentView(binding.getRoot());

        // init auth
        auth = FirebaseAuth.getInstance();
        checkUser();


        shopNowBtn = binding.shopNowBtn;
        onboardingIndicators = binding.onboardingIndicators;

        setupOnboardingItems();

        // initializing the ViewPager2 object
        onboardingViewPager = binding.viewPager;
        onboardingViewPager.setAdapter(onboardingAdapter);
        onboardingViewPager.setPageTransformer(new DepthPageTransformer());

        setupOnboardingIndicator();
        setCurrentOnboardingIndicator(0);

        onboardingViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            /**
             * This method will be invoked when a new page becomes selected. Animation is not
             * necessarily complete.
             *
             * @param position Position index of the new selected page.
             */
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                setCurrentOnboardingIndicator(position);
            }
        });


        shopNowBtn.setOnClickListener(view -> {

            if (onboardingViewPager.getCurrentItem() + 1 < onboardingAdapter.getItemCount()) {
                onboardingViewPager.setCurrentItem(onboardingViewPager.getCurrentItem() + 1);
            } else {
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setupOnboardingItems() {
        onboardings = new ArrayList<>();

        Onboarding first = new Onboarding();
        first.setImage(R.drawable.img_1);

        Onboarding second = new Onboarding();
        second.setImage(R.drawable.img_2);

        Onboarding third = new Onboarding();
        third.setImage(R.drawable.img_3);

        onboardings.add(first);
        onboardings.add(second);
        onboardings.add(third);

        onboardingAdapter = new OnboardingAdapter(onboardings, this);
    }

    private void setupOnboardingIndicator() {
        ImageView[] indicators = new ImageView[onboardingAdapter.getItemCount()];
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(8,0,8,0);
        for (int i = 0; i < indicators.length; i++) {
            indicators[i] = new ImageView(this);
            indicators[i].setImageDrawable(
                    ContextCompat.getDrawable(
                            getApplicationContext(),
                            R.drawable.default_dot
                    )
            );
            indicators[i].setLayoutParams(params);
            onboardingIndicators.addView(indicators[i]);
        }
    }

    private void setCurrentOnboardingIndicator(int index) {
        int childCount = onboardingIndicators.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ImageView imageView = (ImageView) onboardingIndicators.getChildAt(i);
            if (i == index) {
                imageView.setImageDrawable(
                        ContextCompat.getDrawable(
                                getApplicationContext(),
                                R.drawable.selected_dot
                        )
                );
            } else {
                imageView.setImageDrawable(
                        ContextCompat.getDrawable(
                                getApplicationContext(),
                                R.drawable.default_dot
                        )
                );
            }
        }

        if (index == onboardingAdapter.getItemCount() - 1) {
            shopNowBtn.setVisibility(View.VISIBLE);
        } else {
            shopNowBtn.setVisibility(View.GONE);
        }
    }

    private void checkUser() {
        // if user is already logged in go to profile activity
        // get current user
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            // user is already logged in
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        }
    }
}