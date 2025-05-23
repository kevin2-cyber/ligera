package com.ligera.app.view;

import android.content.Intent;
import android.os.Bundle;
import android.transition.Explode;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.ligera.app.R;
import com.ligera.app.model.entity.Onboarding;
import com.ligera.app.view.adapter.OnboardingAdapter;
import com.ligera.app.view.util.DepthPageTransformer;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

import java.util.ArrayList;
import java.util.List;

public class OnboardingActivity extends AppCompatActivity {

    ViewPager2 onboardingViewPager;
    OnboardingAdapter onboardingAdapter;
    Button shopNowBtn;

    List<Onboarding> onboardings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        getWindow().requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);

        getWindow().setExitTransition(new Explode());
        setContentView(R.layout.activity_onboarding);


        shopNowBtn = findViewById(R.id.shop_now_btn);

        setupOnboardingItems();

        // initializing the ViewPager2 object
        onboardingViewPager = findViewById(R.id.view_pager);
        onboardingViewPager.setAdapter(onboardingAdapter);
        onboardingViewPager.setPageTransformer(new DepthPageTransformer());

        DotsIndicator scaleDotsIndicator = findViewById(R.id.indicator);
        scaleDotsIndicator.attachTo(onboardingViewPager);

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

                if (position == onboardingAdapter.getItemCount() - 1) {
                    shopNowBtn.setVisibility(View.VISIBLE);
                } else {
                    shopNowBtn.setVisibility(View.GONE);
                }
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
}