package com.ligera.app.view.fragments;

import static com.ligera.app.view.DetailActivity.CATEGORY_ID;
import static com.ligera.app.view.DetailActivity.PRODUCT_BRAND;
import static com.ligera.app.view.DetailActivity.PRODUCT_DESCRIPTION;
import static com.ligera.app.view.DetailActivity.PRODUCT_ID;
import static com.ligera.app.view.DetailActivity.PRODUCT_IMAGE;
import static com.ligera.app.view.DetailActivity.PRODUCT_NAME;
import static com.ligera.app.view.DetailActivity.PRODUCT_PRICE;
import static com.ligera.app.view.DetailActivity.PRODUCT_QUANTITY;
import static com.ligera.app.view.DetailActivity.PRODUCT_SIZE;

import android.app.ActivityOptions;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuHost;
import androidx.core.view.MenuProvider;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ligera.app.R;
import com.ligera.app.databinding.FragmentHomeBinding;
import com.ligera.app.model.entity.Product;
import com.ligera.app.view.DetailActivity;
import com.ligera.app.view.adapter.HomeProductAdapter;
import com.ligera.app.view.util.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class HomeFragment extends Fragment implements MenuProvider {
    private FragmentHomeBinding binding;
    SearchView searchView;
    ArrayList<Product> products;
    HomeProductAdapter adapter;
    public int selectedProductId;


    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
            binding = DataBindingUtil.inflate(inflater,R.layout.fragment_home, container, false);
            binding.setProduct(new Product());
        MenuHost menuHost = requireActivity();
        menuHost.addMenuProvider(this, getViewLifecycleOwner(), Lifecycle.State.RESUMED);
        return binding.getRoot();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View getView() {
        return super.getView();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.toolbar.inflateMenu(R.menu.home_app_bar_menu);

        MenuItem itemSearch = binding.toolbar.getMenu().findItem(R.id.search);
        // get the searchView and searchable configuration
        SearchManager searchManager = (SearchManager) requireActivity().getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) itemSearch.getActionView();

        // Create a ContextThemeWrapper with the desired style
        Context context = getContext();
        ContextThemeWrapper newContext = new ContextThemeWrapper(context, R.style.CustomSearchViewStyle);

        // Replace the SearchView with a new one using the themed context
        SearchView newSearchView = new SearchView(newContext);
        assert searchView != null;
        searchView.setQueryHint("Type Here");
        ComponentName componentName = requireActivity().getComponentName();
        SearchableInfo searchableInfo = searchManager.getSearchableInfo(componentName);
        searchView.setSearchableInfo(searchableInfo);
        searchView.setIconified(false);

        SearchView.OnQueryTextListener onQueryTextListener = new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterList(newText);
                return false;
            }
        };
        searchView.setOnQueryTextListener(onQueryTextListener);

        // Transfer listeners and other properties from the old SearchView to the new one
        newSearchView.setOnQueryTextListener(onQueryTextListener);
        newSearchView.setSearchableInfo(searchableInfo);
        newSearchView.setQueryHint(searchView.getQueryHint());
        newSearchView.setIconifiedByDefault(false);

        // get the list of products
        products = Constants.getProductData();
        // assign list of products to adapter
        adapter = new HomeProductAdapter(getActivity());
        RecyclerView recyclerView = binding.rvItems;
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        adapter.submitList(products);

        // sending the data to DetailsActivity
        adapter.setListener(product -> {

            View transitionView = requireView().findViewById(R.id.imageView);
            selectedProductId = product.getProductId();
            Intent intent = new Intent(requireActivity(), DetailActivity.class);

            intent.putExtra(PRODUCT_ID, selectedProductId);
            intent.putExtra(PRODUCT_NAME, product.getName());
            intent.putExtra(PRODUCT_IMAGE, product.getImage());
            intent.putExtra(PRODUCT_DESCRIPTION, product.getDescription());
            intent.putExtra(CATEGORY_ID, product.getCategoryId());
            intent.putExtra(PRODUCT_PRICE, product.getPrice());
            intent.putExtra(PRODUCT_QUANTITY, product.getQuantity());
            intent.putExtra(PRODUCT_BRAND, product.getBrand());
            intent.putExtra(PRODUCT_SIZE, product.getSize());

            ActivityOptions options = ActivityOptions
                    .makeSceneTransitionAnimation(requireActivity(), transitionView, PRODUCT_IMAGE);

            startActivity(intent, options.toBundle());
        });
    }


    public void filterList(String query) {
        try {
            List<Product> filtered = products.stream()
                    .filter(n -> TextUtils.isEmpty(query)
                    || n.getName().toLowerCase().contains(query.toLowerCase())
                    || n.getBrand().toLowerCase().contains(query.toLowerCase())
                    || String.valueOf(n.getDescription()).toLowerCase().contains(query.toLowerCase()))
                    .collect(Collectors.toList());

            adapter.submitList(filtered);
        } catch (Exception e) {
            Toast.makeText(getActivity(), "Error filtering list: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.home_app_bar_menu, menu);
    }

    @Override
    public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
        return false;
    }
}