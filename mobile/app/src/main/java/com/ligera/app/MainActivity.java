package com.ligera.app;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.transition.Explode;
import android.view.Window;

import com.ligera.app.view.OnboardingActivity;
import com.ligera.app.viewmodel.SplashViewModel;

public class MainActivity extends AppCompatActivity {
    private SplashViewModel splashViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Request feature must be called before adding content
        getWindow().requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);

        EdgeToEdge.enable(this);
        // Install splash screen and hold onto the instance
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);

        getWindow().setExitTransition(new Explode());

        splashViewModel = new ViewModelProvider(this).get(SplashViewModel.class);

        // Keep the splash screen on until the loading is complete
        splashScreen.setKeepOnScreenCondition(() -> {
            Boolean isLoadingComplete = splashViewModel.getLoadingStatus().getValue();
            // Keep splash on screen if loading isn't complete yet.
            return isLoadingComplete == null || !isLoadingComplete;
        });

        // Observe the loading status to know when to transition
        splashViewModel.getLoadingStatus().observe(this, isLoadingComplete -> {
            if (Boolean.TRUE.equals(isLoadingComplete)) {
                // Start the next activity or update the UI
                proceedToMainContent();
            }
        });
    }

    private void proceedToMainContent() {
        Intent intent = new Intent(this, OnboardingActivity.class); // Your main activity after splash
        startActivity(intent);
        finish();
    }
}