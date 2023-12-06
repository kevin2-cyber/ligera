package com.ligera.app.view.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.TextView;

import com.ligera.app.R;
import com.ligera.app.model.entity.Product;
import com.ligera.app.view.adapter.HomeRecyclerVA;
import com.ligera.app.view.util.Constants;

import java.util.ArrayList;
import java.util.Objects;


public class HomeFragment extends Fragment {
//    private FragmentHomeBinding binding;

    SearchView searchView;
    TextView appTitle;
    ImageButton notification;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        disableViews();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // get the list of products
        ArrayList<Product> products = Constants.getProductData();
        // assign list of products to adapter
        HomeRecyclerVA adapter = new HomeRecyclerVA(getContext(), products);
        RecyclerView recyclerView = view.findViewById(R.id.rv_items);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerView.setAdapter(adapter);

//        searchView = view.findViewById(R.id.searchView);
//        appTitle = view.findViewById(R.id.app_title);
//        notification = view.findViewById(R.id.iv_notification);

    }

    private void disableViews() {
        searchView = requireActivity().findViewById(R.id.searchView);
        appTitle = requireActivity().findViewById(R.id.app_title);
        notification = requireActivity().findViewById(R.id.iv_notification);


        searchView.clearFocus();
        searchView.setOnClickListener(v -> {
            appTitle.setVisibility(View.GONE);
            notification.setVisibility(View.GONE);
        });
    }


}