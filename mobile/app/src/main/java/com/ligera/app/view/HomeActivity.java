package com.ligera.app.view;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.Window;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.transition.MaterialFadeThrough;
import com.ligera.app.R;
import com.ligera.app.databinding.ActivityHomeBinding;
import com.ligera.app.view.fragments.CartFragment;
import com.ligera.app.view.fragments.FavoritesFragment;
import com.ligera.app.view.fragments.HomeFragment;
import com.ligera.app.view.fragments.ProfileFragment;


public class HomeActivity extends AppCompatActivity implements
        NavigationBarView.OnItemSelectedListener {
    private ActivityHomeBinding binding;

    private final HomeFragment homeFragment = new HomeFragment();
    private final CartFragment cartFragment = new CartFragment();
    private final FavoritesFragment favoritesFragment = new FavoritesFragment();
    private final ProfileFragment profileFragment = new ProfileFragment();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // requestFeature must be called before adding content
        getWindow().requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_home);

        ViewCompat.setOnApplyWindowInsetsListener(binding.home, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        BottomNavigationView bottomNavigationView = binding.bottomNavigationView;
        bottomNavigationView.setOnItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.navigation_home);

        // Setup badge on cart icon
        BadgeDrawable badgeDrawable = bottomNavigationView.getOrCreateBadge(R.id.navigation_cart);
        badgeDrawable.setVisible(true);
        badgeDrawable.setNumber(2);
        badgeDrawable.setBadgeTextColor(ContextCompat.getColor(this, R.color.charcoal_gray_dark));
        badgeDrawable.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
    }

    private void switchFragment(Fragment fragment) {
        // This allows the fragments to use their own enter/exit transitions
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.flFragment, fragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
    }

    // switch between fragments when view is selected
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.navigation_home) {
            switchFragment(homeFragment);
            return true;
        } else if (itemId == R.id.navigation_cart) {
            switchFragment(cartFragment);
            return true;
        } else if (itemId == R.id.navigation_liked) {
            switchFragment(favoritesFragment);
            return true;
        } else if (itemId == R.id.navigation_profile) {
            switchFragment(profileFragment);
            return true;
        }
        return false;
    }
}
