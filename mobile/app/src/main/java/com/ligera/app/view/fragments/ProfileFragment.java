package com.ligera.app.view.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ligera.app.R;
import com.ligera.app.databinding.FragmentProfileBinding;
import com.ligera.app.view.LoginActivity;


public class ProfileFragment extends Fragment {
    private FragmentProfileBinding binding;
    private FirebaseAuth auth;


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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
        // Inflate the layout for this fragment
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        auth = FirebaseAuth.getInstance();

        binding.llLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signOut();
                checkUser();
            }
        });
    }



    private void checkUser() {
        // check if user is logged in or not
        FirebaseUser user = auth.getCurrentUser();

        if (user != null) {
            // user is not null, user is logged in, get user info
            String email = user.getEmail();

            Toast.makeText(requireActivity(), "You're logged in as " + email, Toast.LENGTH_SHORT).show();
        } else {
            //user is null, user not logged in go to login activity
            startActivity(new Intent(requireActivity(), LoginActivity.class));
            requireActivity().finish();
        }
    }
}