package com.aceman.go4lunch.fragments.workmatesFragment;


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
import com.aceman.go4lunch.utils.events.UserListEvent;
import com.aceman.go4lunch.models.RestaurantPublic;
import com.aceman.go4lunch.adapter.WorkersAdapter;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;


/**
 * A simple {@link Fragment} subclass.
 */
public class WorkmatesFragment extends Fragment implements WorkmatesContract.WorkmatesViewInterface {
    public WorkersAdapter mWorkersAdapter;
    public List<RestaurantPublic> mUserList = new ArrayList<>();
    @BindView(R.id.workmate_fragment_recycler_view)
    RecyclerView mRecyclerView;
    Toolbar mToolbar;
    WorkmatesPresenter mPresenter;

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
        mPresenter = new WorkmatesPresenter();
        mPresenter.attachView(this);
        configureRecyclerView();
        mPresenter.getUserList();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.getUserList();
    }

    @Override
    public void configureRecyclerView() {
        if (mUserList != null) {
            mWorkersAdapter = new WorkersAdapter(mUserList, Glide.with(this), getContext());
            mRecyclerView.setAdapter(mWorkersAdapter);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            mRecyclerView.addItemDecoration(new DividerItemDecoration(Objects.requireNonNull(getContext()), DividerItemDecoration.VERTICAL));
        }
    }

    @Override
    public void setUserListFromFirebase(Task<QuerySnapshot> task) {
        mUserList.clear();
        for (QueryDocumentSnapshot document : task.getResult()) {
            RestaurantPublic userP = document.toObject(RestaurantPublic.class);     // < == GET LIST FIRESTORE
            mUserList.add(userP);
            Timber.tag("Task To List").i("Sucess");
        }
        mWorkersAdapter.notifyDataSetChanged();
    }

    @Override
    public void errorGettingUserListFromFirebase(Task<QuerySnapshot> task) {
        Timber.tag("Task Exeption").d(task.getException(), "Error getting documents: ");
    }

    @Override
   public void updateRecyclerView(){
        mWorkersAdapter.notifyDataSetChanged();
    }
}
