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

        binding.themeSwitcher.setOnClickListener(v -> {
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
            //user is null, user not logged in go to login activity
            startActivity(new Intent(requireActivity(), LoginActivity.class));
            requireActivity().finish();
        }
    }
}