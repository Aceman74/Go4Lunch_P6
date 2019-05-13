package com.aceman.go4lunch.navigation.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.aceman.go4lunch.R;
import com.aceman.go4lunch.data.nearby_search.Result;
import com.aceman.go4lunch.navigation.adapter.ListViewAdapter;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.aceman.go4lunch.navigation.activities.CoreActivity.mResults;

/**
 * A simple {@link Fragment} subclass.
 */
public class ListViewFragment extends Fragment {
    @BindView(R.id.restaurant_recycler_view)
    RecyclerView mRecyclerView;
    public static ListViewAdapter mListViewAdapter;

    public ListViewFragment() {
        // Required empty public constructor
    }

    public static ListViewFragment newInstance() {
        return new ListViewFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_list_view, container, false);
        ButterKnife.bind(this,view);
        configureRecyclerView();
        return view;
    }

    public void configureRecyclerView() {
        if (mResults != null) {
            mListViewAdapter = new ListViewAdapter(mResults, Glide.with(this), getContext());
            mRecyclerView.setAdapter(mListViewAdapter);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            mRecyclerView.addItemDecoration(new DividerItemDecoration(Objects.requireNonNull(getContext()), DividerItemDecoration.VERTICAL));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
