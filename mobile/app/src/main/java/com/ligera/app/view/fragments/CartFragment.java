package com.ligera.app.view.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.transition.MaterialContainerTransform;
import com.google.android.material.transition.MaterialElevationScale;
import com.google.android.material.transition.MaterialFadeThrough;

import com.ligera.app.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CartFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CartFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
   String mParam1;
   String mParam2;

    public CartFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ShoppingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CartFragment newInstance(String param1, String param2) {
        CartFragment fragment = new CartFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        
        // Set up Material Design transitions
        MaterialFadeThrough fadeThrough = new MaterialFadeThrough();
        fadeThrough.setDuration(300); // Match HomeActivity transition duration
        
        // Set enter and exit transitions
        setEnterTransition(fadeThrough);
        setExitTransition(fadeThrough);
        setReenterTransition(fadeThrough);
        setReturnTransition(fadeThrough);
        
        // Set up MaterialElevationScale for item interactions
        MaterialElevationScale elevationScale = new MaterialElevationScale(true);
        elevationScale.setDuration(300);
        
        // Configure shared element transitions
        MaterialContainerTransform containerTransform = new MaterialContainerTransform();
        containerTransform.setDuration(400); // Match HomeActivity container transform duration
        containerTransform.setFadeMode(MaterialContainerTransform.FADE_MODE_THROUGH);
        containerTransform.setScrimColor(getResources().getColor(R.color.colorTransparent, null));
        setSharedElementEnterTransition(containerTransform);
        setSharedElementReturnTransition(containerTransform);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_cart, container, false);
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // When a RecyclerView is added to this fragment, configure it with Material motion
        // This is a placeholder for when cart functionality is implemented
        /*
        RecyclerView recyclerView = view.findViewById(R.id.cart_recycler_view);
        if (recyclerView != null) {
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            
            // Configure custom item animator with Material motion
            DefaultItemAnimator itemAnimator = new DefaultItemAnimator();
            itemAnimator.setAddDuration(300);
            itemAnimator.setRemoveDuration(300);
            itemAnimator.setChangeDuration(300);
            itemAnimator.setMoveDuration(300);
            recyclerView.setItemAnimator(itemAnimator);
            
            // Set up adapter and item click listener for transitions
            // CartAdapter adapter = new CartAdapter();
            // recyclerView.setAdapter(adapter);
            
            // Handle item click with transitions
            // adapter.setOnItemClickListener(position -> {
            //    // Get the clicked view for transition
            //    View itemView = recyclerView.findViewHolderForAdapterPosition(position).itemView;
            //    View productImage = itemView.findViewById(R.id.product_image);
            //    
            //    // Set unique transition name for the shared element
            //    String transitionName = "cart_item_" + position;
            //    productImage.setTransitionName(transitionName);
            //
            //    // Create exit transition
            //    MaterialElevationScale exitTransition = new MaterialElevationScale(false);
            //    exitTransition.setDuration(300);
            //    setExitTransition(exitTransition);
            //
            //    // Navigate to detail with shared element transition
            //    Intent intent = new Intent(getActivity(), DetailActivity.class);
            //    startActivity(intent, 
            //        ActivityOptions.makeSceneTransitionAnimation(
            //            requireActivity(), productImage, transitionName).toBundle());
            // });
        }
        */
    }
}
