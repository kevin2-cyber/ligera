package com.ligera.app.view.fragments;

import static com.ligera.app.view.DetailActivity.PRODUCT_EXTRA;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ligera.app.R;
import com.ligera.app.databinding.FragmentFavoritesBinding;
import com.ligera.app.model.entity.Product;
import com.ligera.app.view.DetailActivity;
import com.ligera.app.view.adapter.FavoritesAdapter;
import com.ligera.app.viewmodel.FavoritesViewModel;

import java.util.ArrayList;

public class FavoritesFragment extends Fragment implements FavoritesAdapter.OnProductItemClickListener {
    private FragmentFavoritesBinding binding;
    private FavoritesViewModel viewModel;
    private FavoritesAdapter adapter;

    public FavoritesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(FavoritesViewModel.class);

        setupRecyclerView();

        viewModel.getFavoriteProducts().observe(getViewLifecycleOwner(), products -> {
            if (products != null && !products.isEmpty()) {
                adapter.submitList(new ArrayList<>(products));
                binding.emptyView.setVisibility(View.GONE);
                binding.rvFavorites.setVisibility(View.VISIBLE);
            } else {
                binding.rvFavorites.setVisibility(View.GONE);
                binding.emptyView.setVisibility(View.VISIBLE);
            }
        });
    }

    private void setupRecyclerView() {
        adapter = new FavoritesAdapter(requireContext());
        RecyclerView recyclerView = binding.rvFavorites;
        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        recyclerView.setAdapter(adapter);
        adapter.setListener(this);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_favorites, container, false);
        return binding.getRoot();
    }

    @Override
    public void onProductItemClick(Product product) {
        View transitionView = requireView().findViewById(R.id.imageView);
        Intent intent = new Intent(requireActivity(), DetailActivity.class);
        intent.putExtra(PRODUCT_EXTRA, product);

        // Apply MaterialContainerTransform for shared element transition
        transitionView.setTransitionName("product_image_" + product.getId());
        
        ActivityOptions options = ActivityOptions
                .makeSceneTransitionAnimation(requireActivity(), transitionView, "product_image_" + product.getId());

        startActivity(intent, options.toBundle());
    }
}
