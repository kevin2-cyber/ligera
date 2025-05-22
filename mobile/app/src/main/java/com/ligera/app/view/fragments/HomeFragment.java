package com.ligera.app.view.fragments;

import android.app.ActivityOptions;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.SearchView;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
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


public class HomeFragment extends Fragment {
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
        return binding.getRoot();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
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
            selectedProductId = product.getProductId();
            Intent intent = new Intent(requireActivity(), DetailActivity.class);

            intent.putExtra(DetailActivity.PRODUCT_ID, selectedProductId);
            intent.putExtra(DetailActivity.PRODUCT_NAME, product.getName());
            intent.putExtra(DetailActivity.PRODUCT_IMAGE, product.getImage());
            intent.putExtra(DetailActivity.PRODUCT_DESCRIPTION, product.getDescription());
            intent.putExtra(DetailActivity.CATEGORY_ID, product.getCategoryId());
            intent.putExtra(DetailActivity.PRODUCT_PRICE, product.getPrice());
            intent.putExtra(DetailActivity.PRODUCT_QUANTITY, product.getQuantity());
            intent.putExtra(DetailActivity.PRODUCT_BRAND, product.getBrand());
            intent.putExtra(DetailActivity.PRODUCT_SIZE, product.getSize());

            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(requireActivity()).toBundle());
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



}