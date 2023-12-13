package com.ligera.app.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

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

public class HomeActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener{
    ActivityHomeBinding binding;
    Toolbar mToolbar;

    BottomNavigationView bottomNavigationView;
    HomeFragment homeFragment = new HomeFragment();
    CartFragment cartFragment = new CartFragment();
    FavoritesFragment favoritesFragment = new FavoritesFragment();
    ProfileFragment profileFragment = new ProfileFragment();
    BadgeDrawable badgeDrawable;
    SearchView searchView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_home);
        Product product = new Product();
        binding.setProduct(product);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.navigation_home);

        badgeDrawable = bottomNavigationView.getOrCreateBadge(R.id.navigation_cart);
        badgeDrawable.setVisible(badgeDrawable.isVisible());
        badgeDrawable.setState(badgeDrawable.getState());
        badgeDrawable.setBadgeTextColor(ContextCompat.getColor(this, R.color.charcoal_gray_dark));
        badgeDrawable.setNumber(99);

    }

    // switch between fragments when view is selected
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.navigation_home) {
            getSupportFragmentManager().beginTransaction().replace(R.id.flFragment,homeFragment).commit();
            displayMenuInHomeFragment();
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

    public void displayMenuInHomeFragment() {
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        searchView = findViewById(R.id.search);

//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                homeFragment.filterList(newText);
//                return false;
//            }
//        });
    }

}