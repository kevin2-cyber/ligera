package com.ligera.app.view.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
//    Toolbar mToolbar;
    ArrayList<Product> products;
    HomeRecyclerVA adapter;


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

//    @Override
//    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
//        inflater.inflate(R.menu.app_bar_menu, menu);
//

//        super.onCreateOptionsMenu(menu, inflater);
//    }

//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        if (item.getItemId() == R.id.search) {
//            searchView = new SearchView(requireContext());
//            searchView.clearFocus();
//            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//                @Override
//                public boolean onQueryTextSubmit(String query) {
//                    return false;
//                }
//
//                @Override
//                public boolean onQueryTextChange(String newText) {
//                    filterList(newText);
//                    return false;
//                }
//            });
////            return true;
//        } else if (item.getItemId() == R.id.notification_icon) {
//            Toast.makeText(requireActivity(), "Notification", Toast.LENGTH_LONG).show();
////            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

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



    }



    public void filterList(String newText) {
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



}