package com.ligera.app.view;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.Window;

import com.google.android.material.transition.MaterialFadeThrough;
import com.google.android.material.transition.MaterialContainerTransform;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.ligera.app.R;
import com.ligera.app.databinding.ActivityHomeBinding;
import com.ligera.app.model.entity.Product;
import com.ligera.app.view.fragments.CartFragment;
import com.ligera.app.view.fragments.FavoritesFragment;
import com.ligera.app.view.fragments.HomeFragment;
import com.ligera.app.view.fragments.ProfileFragment;


public class HomeActivity extends AppCompatActivity implements
        NavigationBarView.OnItemSelectedListener{
    ActivityHomeBinding binding;

    BottomNavigationView bottomNavigationView;
    HomeFragment homeFragment = new HomeFragment();
    CartFragment cartFragment = new CartFragment();
    FavoritesFragment favoritesFragment = new FavoritesFragment();
    ProfileFragment profileFragment = new ProfileFragment();
    BadgeDrawable badgeDrawable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        getWindow().requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);

        // Configure Material Design transitions
        MaterialFadeThrough fadeThrough = new MaterialFadeThrough();
        fadeThrough.setDuration(300);
        
        // Set activity transitions
        getWindow().setEnterTransition(fadeThrough);
        getWindow().setExitTransition(fadeThrough);
        getWindow().setReenterTransition(fadeThrough);
        getWindow().setReturnTransition(fadeThrough);
        
        // Configure shared element transitions
        MaterialContainerTransform containerTransform = new MaterialContainerTransform();
        containerTransform.setDuration(400);
        containerTransform.setFadeMode(MaterialContainerTransform.FADE_MODE_THROUGH);
        getWindow().setSharedElementEnterTransition(containerTransform);
        getWindow().setSharedElementReturnTransition(containerTransform);
        
        setContentView(R.layout.activity_home);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.home), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding = DataBindingUtil.setContentView(this, R.layout.activity_home);
        Product product = new Product();
        binding.setProduct(product);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.navigation_home);

        badgeDrawable = bottomNavigationView.getOrCreateBadge(R.id.navigation_cart);
        badgeDrawable.setVisible(true);
        badgeDrawable.setState(new int[]{android.R.attr.state_focused, android.R.attr.state_pressed});
        badgeDrawable.setBadgeTextColor(ContextCompat.getColor(this, R.color.charcoal_gray_dark));
        badgeDrawable.setNumber(2);

    }

    // switch between fragments when view is selected
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Create the Material Design transition for fragment navigation
        MaterialFadeThrough fadeThrough = new MaterialFadeThrough();
        fadeThrough.setDuration(300);
        
        // Create the fragment transaction with transition
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        
        if (item.getItemId() == R.id.navigation_home) {
            homeFragment.setEnterTransition(fadeThrough);
            homeFragment.setExitTransition(fadeThrough);
            homeFragment.setReenterTransition(fadeThrough);
            homeFragment.setReturnTransition(fadeThrough);
            transaction.replace(R.id.flFragment, homeFragment).commit();
            return true;
        } else if (item.getItemId() == R.id.navigation_cart) {
            cartFragment.setEnterTransition(fadeThrough);
            cartFragment.setExitTransition(fadeThrough);
            cartFragment.setReenterTransition(fadeThrough);
            cartFragment.setReturnTransition(fadeThrough);
            transaction.replace(R.id.flFragment, cartFragment).commit();
            return true;
        } else if (item.getItemId() == R.id.navigation_liked) {
            favoritesFragment.setEnterTransition(fadeThrough);
            favoritesFragment.setExitTransition(fadeThrough);
            favoritesFragment.setReenterTransition(fadeThrough);
            favoritesFragment.setReturnTransition(fadeThrough);
            transaction.replace(R.id.flFragment, favoritesFragment).commit();
            return true;
        } else if (item.getItemId() == R.id.navigation_profile) {
            profileFragment.setEnterTransition(fadeThrough);
            profileFragment.setExitTransition(fadeThrough);
            profileFragment.setReenterTransition(fadeThrough);
            profileFragment.setReturnTransition(fadeThrough);
            transaction.replace(R.id.flFragment, profileFragment).commit();
            return true;
        }
        return false;
    }
}