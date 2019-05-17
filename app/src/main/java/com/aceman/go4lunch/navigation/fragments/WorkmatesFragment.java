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
import com.aceman.go4lunch.data.nearby_search.Result;
import com.aceman.go4lunch.models.User;
import com.aceman.go4lunch.navigation.adapter.WorkersAdapter;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;


/**
 * A simple {@link Fragment} subclass.
 */
public class WorkmatesFragment extends Fragment {
    public WorkersAdapter mWorkersAdapter;
    public List<Result> mResults = new ArrayList<>();
    public List<User> mUserList = new ArrayList<>();
    @BindView(R.id.workmate_fragment_recycler_view)
    RecyclerView mRecyclerView;
    FirebaseDatabase mFirebaseInstance;
    DatabaseReference mDatabase;
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
        ButterKnife.bind(this, view);
        configureRecyclerView();
        firebaseDatabase();
        return view;
    }


    private void firebaseDatabase() {
        mFirebaseInstance = FirebaseDatabase.getInstance();
        mDatabase = mFirebaseInstance.getReference();


        //  mRvData.setLayoutManager(new LinearLayoutManager(this));
        mDatabase.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mUserList.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    User user = dataSnapshot1.getValue(User.class);
                    mUserList.add(user);
                    Timber.tag("TEST").e(mUserList.get(0).getUsername());
                }

                // recycler and adapter

            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
                Timber.tag("FIREBASE_DATABASE").w(databaseError.getMessage());

            }
        });
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
