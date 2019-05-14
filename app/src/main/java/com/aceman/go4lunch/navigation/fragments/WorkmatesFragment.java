package com.aceman.go4lunch.navigation.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aceman.go4lunch.R;
import com.aceman.go4lunch.navigation.adapter.WorkersAdapter;
import com.bumptech.glide.Glide;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.aceman.go4lunch.navigation.activities.CoreActivity.mResults;
import static com.aceman.go4lunch.navigation.activities.CoreActivity.mWorkersAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class WorkmatesFragment extends Fragment {
    @BindView(R.id.workmate_fragment_recycler_view)
    RecyclerView mRecyclerView;
    Toolbar mToolbar;

    public WorkmatesFragment() {
        // Required empty public constructor
    }

    public static WorkmatesFragment newInstance() {
        return new WorkmatesFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_workmates, container, false);
        ButterKnife.bind(this,view);
        configureRecyclerView();
        return view;
    }

    public void configureRecyclerView() {
        if (mResults != null) {
            mWorkersAdapter = new WorkersAdapter(mResults, Glide.with(this), getContext());
            mRecyclerView.setAdapter(mWorkersAdapter);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            mRecyclerView.addItemDecoration(new DividerItemDecoration(Objects.requireNonNull(getContext()), DividerItemDecoration.VERTICAL));
        }
    }
}
