package com.ligera.app.view.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.search.SearchBar;
import com.ligera.app.R;
import com.ligera.app.databinding.FragmentHomeBinding;
import com.ligera.app.model.entity.Product;
import com.ligera.app.view.adapter.HomeRecyclerVA;
import com.ligera.app.view.util.Constants;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;

    SearchView searchView;
    SearchBar searchBar;
    TextView appTitle;
    Button notification;
    ArrayList<Product> products;
    HomeRecyclerVA adapter;
    int index;

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
    }

    @Nullable
    @Override
    public View getView() {
        return super.getView();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // get the list of products
        products = Constants.getProductData();
        // assign list of products to adapter
        adapter = new HomeRecyclerVA(getContext(), products);
        RecyclerView recyclerView = binding.rvItems;
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        adapter.setProductList(products);

        searchView = binding.searchView;

//        ImageView imageView = view.findViewById(R.id.imageView);
//
//        String image = String.valueOf(products.get(index).getImage());
//
//        Glide.with(view.getContext()).load(image).apply(new RequestOptions().fitCenter()).into(imageView);

//        searchView = view.findViewById(R.id.searchView);
//        appTitle = view.findViewById(R.id.app_title);
//        notification = view.findViewById(R.id.iconNotification);
//        searchBar = view.findViewById(R.id.search_bar);

        searchView.clearFocus();

        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.appTitle.setTranslationX(180);
                binding.appTitle.setText(R.string.find_products);
                searchView.setBackground(AppCompatResources.getDrawable(v.getContext(), R.drawable.search_bg));
                binding.iconNotification.setVisibility(View.GONE);
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterList(newText);
                return false;
            }
        });

//        searchBar.setVisibility(View.GONE);

//        searchView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                appTitle.setVisibility(View.GONE);
//                notification.setVisibility(View.GONE);
//            }
//        });

//        searchView = view.findViewById(R.id.searchView);
//        appTitle = view.findViewById(R.id.app_title);
//        notification = view.findViewById(R.id.iv_notification);

    }

    private void filterList(String newText) {
        List<Product> filteredProducts = new ArrayList<>();
        for (Product product : products) {
            if (product.getName().toLowerCase().contains(newText.toLowerCase())) {
                filteredProducts.add(product);
            }
        }

        if (filteredProducts.isEmpty()) {
            Toast.makeText(getActivity(), "No products", Toast.LENGTH_SHORT).show();
        } else {
            adapter.setFilterList(filteredProducts);
        }
    }

    private void disableViews() {
        searchView = requireActivity().findViewById(R.id.searchView);
        appTitle = requireActivity().findViewById(R.id.app_title);
        notification = requireActivity().findViewById(R.id.iconNotification);


        searchView.clearFocus();
        searchView.setOnClickListener(v -> {
            appTitle.setVisibility(View.GONE);
            notification.setVisibility(View.GONE);
        });
    }


}