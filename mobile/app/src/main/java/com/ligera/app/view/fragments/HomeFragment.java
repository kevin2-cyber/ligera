package com.ligera.app.view.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.TextView;

import com.ligera.app.R;

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

        disableViews();
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