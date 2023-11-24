package com.ligera.app.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.ligera.app.R;
import com.ligera.app.view.fragments.CartFragment;
import com.ligera.app.view.fragments.FavoritesFragment;
import com.ligera.app.view.fragments.HomeFragment;
import com.ligera.app.view.fragments.ProfileFragment;

public class HomeActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener {

    BottomNavigationView bottomNavigationView;
    HomeFragment homeFragment = new HomeFragment();
    CartFragment cartFragment = new CartFragment();
    FavoritesFragment favoritesFragment = new FavoritesFragment();
    ProfileFragment profileFragment = new ProfileFragment();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
//                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.navigation_home);
    }

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