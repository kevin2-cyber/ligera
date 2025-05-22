package com.ligera.app.view;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.transition.Explode;
import android.view.MenuItem;
import android.view.Window;

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

        getWindow().setExitTransition(new Explode());
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
        if (item.getItemId() == R.id.navigation_home) {
            getSupportFragmentManager().beginTransaction().replace(R.id.flFragment,homeFragment).commit();
            return true;
        } else if (item.getItemId() == R.id.navigation_cart) {
            getSupportFragmentManager().beginTransaction().replace(R.id.flFragment,cartFragment).commit();
            return true;
        } else if (item.getItemId() == R.id.navigation_liked) {
            getSupportFragmentManager().beginTransaction().replace(R.id.flFragment,favoritesFragment).commit();
            return true;
        } else if (item.getItemId() == R.id.navigation_profile) {
            getSupportFragmentManager().beginTransaction().replace(R.id.flFragment,profileFragment).commit();
            return true;
        }
        return false;
    }
}