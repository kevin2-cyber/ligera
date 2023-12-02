package com.ligera.app.view.fragments;

import android.os.Bundle;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.ligera.app.R;


public class HomeFragment extends Fragment {

    SearchView searchView;
    TextView appTitle;
    ImageButton notification;

    public HomeFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        searchView = requireActivity().findViewById(R.id.searchView);
        appTitle = requireActivity().findViewById(R.id.app_title);
        notification = requireActivity().findViewById(R.id.iv_notification);


        searchView.clearFocus();
        searchView.setOnClickListener(v -> {
            appTitle.setVisibility(View.GONE);
            notification.setVisibility(View.GONE);
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }
}