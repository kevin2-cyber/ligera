package com.ligera.app.view.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.google.android.material.transition.MaterialContainerTransform;
import com.google.android.material.transition.MaterialElevationScale;
import com.google.android.material.transition.MaterialFadeThrough;

import com.ligera.app.R;
import com.ligera.app.databinding.FragmentProfileBinding;
import com.ligera.app.view.LoginActivity;
import com.ligera.app.view.bottomsheet.ThemeModalBottomSheet;


public class ProfileFragment extends Fragment {
    FragmentProfileBinding binding;
    ProfileClickHandler handler;


    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Set up Material Design transitions
        MaterialFadeThrough fadeThrough = new MaterialFadeThrough();
        fadeThrough.setDuration(300); // Match HomeActivity transition duration
        
        // Set enter and exit transitions
        setEnterTransition(fadeThrough);
        setExitTransition(fadeThrough);
        setReenterTransition(fadeThrough);
        setReturnTransition(fadeThrough);
        
        // Configure shared element transitions
        MaterialContainerTransform containerTransform = new MaterialContainerTransform();
        containerTransform.setDuration(400); // Match HomeActivity container transform duration
        containerTransform.setFadeMode(MaterialContainerTransform.FADE_MODE_THROUGH);
        containerTransform.setScrimColor(getResources().getColor(R.color.transparent, null));
        setSharedElementEnterTransition(containerTransform);
        setSharedElementReturnTransition(containerTransform);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false);
        handler = new ProfileClickHandler(getContext());
        binding.setHandler(handler);
        // Inflate the layout for this fragment
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Add elevation scale transition for theme switcher
        binding.themeSwitcher.setOnClickListener(v -> {
            // Apply MaterialElevationScale transition when opening the theme bottom sheet
            MaterialElevationScale exitTransition = new MaterialElevationScale(false);
            exitTransition.setDuration(300);
            setExitTransition(exitTransition);
            
            // Set up return transition for when bottom sheet is dismissed
            MaterialElevationScale returnTransition = new MaterialElevationScale(true);
            returnTransition.setDuration(300);
            setReenterTransition(returnTransition);
            
            // Create and show theme bottom sheet
            ThemeModalBottomSheet modalBottomSheet = new ThemeModalBottomSheet();
            modalBottomSheet.show(requireActivity().getSupportFragmentManager(), ThemeModalBottomSheet.TAG);
        });

    }


    public class ProfileClickHandler {
        Context context;

        public ProfileClickHandler(Context context) {
            this.context = context;
        }

        public void logout(View view) {
            // Apply transition for logout
            MaterialFadeThrough fadeThrough = new MaterialFadeThrough();
            fadeThrough.setDuration(300);
            
            // Set exit transition for the fragment
            setExitTransition(fadeThrough);
            
            // Start login activity with transition
            Intent intent = new Intent(requireActivity(), LoginActivity.class);
            startActivity(intent);
            requireActivity().finish();
        }
    }
}