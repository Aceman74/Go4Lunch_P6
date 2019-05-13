package com.aceman.go4lunch.navigation.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aceman.go4lunch.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class PlacesDetailFragment extends Fragment {


    public PlacesDetailFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_places_detail, container, false);

        return view;
    }

}
