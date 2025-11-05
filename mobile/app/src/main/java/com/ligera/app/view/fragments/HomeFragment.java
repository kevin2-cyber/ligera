package com.ligera.app.view.fragments;

import static com.ligera.app.view.DetailActivity.PRODUCT_ID;

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


public class HomeFragment extends Fragment implements MenuProvider, HomeProductAdapter.OnProductItemClickListener {
    private FragmentHomeBinding binding;
    HomeProductAdapter adapter;
    private HomeFragmentViewModel viewModel;

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

        // get the list of products
        setupRecyclerView();
        setupCategoryTabs();
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
        viewModel.getAllCategories().observe(getViewLifecycleOwner(), resource -> {
            if (resource != null && resource.data != null) {
                binding.tabLayout.removeAllTabs();
                for (Category category : resource.data) {
                    binding.tabLayout.addTab(binding.tabLayout.newTab().setText(category.getName()).setTag(category.getId()));
                }
            }
        });

        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                long categoryId = (long) tab.getTag();
                viewModel.getProductsOfSelectedCategory(categoryId).observe(getViewLifecycleOwner(), pagingData -> 
                    adapter.submitData(getLifecycle(), pagingData));
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

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
        intent.putExtra(PRODUCT_ID, product.getId());

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
