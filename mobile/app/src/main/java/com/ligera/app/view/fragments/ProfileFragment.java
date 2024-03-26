package com.ligera.app.view.fragments;

import android.content.Context;
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
import com.ligera.app.databinding.ThemeModalBottomSheetBinding;
import com.ligera.app.view.LoginActivity;
import com.ligera.app.view.util.ThemeModalBottomSheet;


public class ProfileFragment extends Fragment {
    FragmentProfileBinding binding;
    private FirebaseAuth auth;
    private ProfileClickHandler handler;


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

        binding.ordersBtn.setOnClickListener(v -> {
            ThemeModalBottomSheet modalBottomSheet = new ThemeModalBottomSheet();
            modalBottomSheet.show(requireActivity().getSupportFragmentManager(), ThemeModalBottomSheet.TAG);
        });

    }



    public void checkUser() {

        auth = FirebaseAuth.getInstance();
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

    public class ProfileClickHandler {
        Context context;

        public ProfileClickHandler(Context context) {
            this.context = context;
        }

        public void logout(View view) {
            checkUser();
            auth.signOut();
        }
    }
}