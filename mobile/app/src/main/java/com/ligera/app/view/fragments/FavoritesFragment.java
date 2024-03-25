package com.ligera.app.view.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.ligera.app.R;
import com.ligera.app.databinding.FragmentFavoritesBinding;


public class FavoritesFragment extends Fragment {
    private FragmentFavoritesBinding binding;

    private static final String SWITCH_BUTTON_KEY = "switch";
    private static final String PREF_KEY = "pref";



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

        SharedPreferences sharedPreferences = view.getContext().getApplicationContext().getSharedPreferences(PREF_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        updateUI(sharedPreferences);

        binding.switchButton.setThumbDrawable(AppCompatResources.getDrawable(view.getContext(), R.drawable.thumb_layer_list));

        binding.switchButton.setTrackDrawable(AppCompatResources.getDrawable(view.getContext(), R.drawable.track_backgrounds));

        binding.switchButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                editor.putBoolean(SWITCH_BUTTON_KEY, true).apply();
                updateUI(sharedPreferences);
            } else {
                editor.putBoolean(SWITCH_BUTTON_KEY, false).apply();
                updateUI(sharedPreferences);
            }
        });
    }

    private void updateUI(SharedPreferences sharedPreferences) {
        boolean isChecked = sharedPreferences.getBoolean(SWITCH_BUTTON_KEY, false);
        binding.switchButton.setChecked(isChecked);
        if (isChecked) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_favorites, container, false);
        return binding.getRoot();
    }
}