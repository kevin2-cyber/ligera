package com.ligera.app.view.fragments;

import static com.ligera.app.view.DetailActivity.PRODUCT_EXTRA;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuHost;
import androidx.core.view.MenuProvider;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.transition.MaterialElevationScale;
import com.google.android.material.transition.MaterialFadeThrough;

import com.ligera.app.R;
import com.ligera.app.databinding.FragmentHomeBinding;
import com.ligera.app.model.entity.Category;
import com.ligera.app.model.entity.Product;
import com.ligera.app.view.DetailActivity;
import com.ligera.app.view.adapter.HomeProductAdapter;
import com.ligera.app.viewmodel.HomeFragmentViewModel;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment implements MenuProvider, HomeProductAdapter.OnProductItemClickListener {
    private FragmentHomeBinding binding;
    HomeProductAdapter adapter;
    private HomeFragmentViewModel viewModel;
    private boolean isTabsSetup = false;
    private boolean isObserverSetup = false;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_home, container, false);
        viewModel = new ViewModelProvider(this).get(HomeFragmentViewModel.class);
        binding.setLifecycleOwner(this);

        MenuHost menuHost = requireActivity();
        menuHost.addMenuProvider(this, getViewLifecycleOwner(), Lifecycle.State.RESUMED);
        return binding.getRoot();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Set up Material Design transitions
        MaterialFadeThrough fadeThrough = new MaterialFadeThrough();
        fadeThrough.setDuration(300);
        
        setEnterTransition(fadeThrough);
        setExitTransition(fadeThrough);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Setup RecyclerView first
        setupRecyclerView();

        // Check if database is empty and seed with dummy data if needed
        viewModel.getProductCount().observe(getViewLifecycleOwner(), count -> {
            if (count == null || count == 0) {
                // Database is empty, seed with dummy products
                seedDummyProducts();
            }
            // Setup category tabs only once (this will trigger loading data)
            if (!isTabsSetup) {
                isTabsSetup = true;
                setupCategoryTabs();
            }
        });
    }

    private void seedDummyProducts() {
        List<Product> dummyProducts = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            Product product = new Product();
            product.setId(i);
            product.setName("Dummy Product " + i);
            product.setDescription("This is a dummy product description for product " + i);
            product.setPrice(BigDecimal.valueOf(i * 15.50));
            product.setBrand("Dummy Brand");
            product.setImageUrl("https://picsum.photos/200/300?random=" + i);
            product.setRating(4.5f);
            product.setFeatured(i % 2 == 0);
            product.setPopular(i % 3 == 0);
            product.setQuantity(100);
            product.setCategoryId(1L);
            dummyProducts.add(product);
        }
        viewModel.insertProducts(dummyProducts);
    }

    private void setupRecyclerView() {
        adapter = new HomeProductAdapter(requireContext());
        RecyclerView recyclerView = binding.rvItems;
        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        
        // Configure custom item animator with Material motion
        DefaultItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setAddDuration(300);
        itemAnimator.setRemoveDuration(300);
        itemAnimator.setChangeDuration(300);
        itemAnimator.setMoveDuration(300);
        recyclerView.setItemAnimator(itemAnimator);
        
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        adapter.setListener(this);
    }


    private void setupCategoryTabs() {
        binding.tabLayout.setVisibility(View.VISIBLE);

        // Setup observer for products only once
        if (!isObserverSetup) {
            isObserverSetup = true;
            viewModel.getProducts().observe(getViewLifecycleOwner(), products -> {
                if (products != null) {
                    adapter.submitList(products);
                }
            });
        }

        // Attach listener FIRST before adding any tabs
        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab != null && tab.getTag() != null) {
                    long categoryId = (long) tab.getTag();
                    viewModel.loadProductsByCategory(categoryId);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        // Add "All" tab first (will trigger onTabSelected because listener is already attached)
        TabLayout.Tab allTab = binding.tabLayout.newTab().setText("All").setTag(-1L);
        binding.tabLayout.addTab(allTab, true); // Select by default - triggers listener

        viewModel.getAllCategories().observe(getViewLifecycleOwner(), resource -> {
            if (resource != null && resource.data != null && !resource.data.isEmpty()) {
                // Remove all tabs except "All"
                while (binding.tabLayout.getTabCount() > 1) {
                    binding.tabLayout.removeTabAt(1);
                }
                for (Category category : resource.data) {
                    TabLayout.Tab tab = binding.tabLayout.newTab().setText(category.getName()).setTag(category.getId());
                    binding.tabLayout.addTab(tab, false);
                }
            }
        });
    }


    @Override
    public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.home_app_bar_menu, menu);
        MenuItem itemSearch = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) itemSearch.getActionView();

        assert searchView != null;
        searchView.setQueryHint("Type Here");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // viewModel.searchProducts(newText);
                return true;
            }
        });
    }

    @Override
    public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
        return false;
    }

    @Override
    public void onProductItemClick(Product product) {
        View transitionView = requireView().findViewById(R.id.imageView);
        Intent intent = new Intent(requireActivity(), DetailActivity.class);
        intent.putExtra(PRODUCT_EXTRA, product);

        // Apply MaterialContainerTransform for shared element transition
        transitionView.setTransitionName("product_image_" + product.getId());
        
        // Create exit transition for current fragment when navigating to detail
        MaterialElevationScale exitTransition = new MaterialElevationScale(false);
        exitTransition.setDuration(300);
        setExitTransition(exitTransition);
        
        // Create reenter transition for when returning from detail view
        MaterialElevationScale reenterTransition = new MaterialElevationScale(true);
        reenterTransition.setDuration(300);
        setReenterTransition(reenterTransition);
        
        ActivityOptions options = ActivityOptions
                .makeSceneTransitionAnimation(requireActivity(), transitionView, "product_image_" + product.getId());

        startActivity(intent, options.toBundle());
    }
}
