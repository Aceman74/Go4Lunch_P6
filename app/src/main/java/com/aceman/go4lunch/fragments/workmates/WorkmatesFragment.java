package com.aceman.go4lunch.fragments.workmates;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.aceman.go4lunch.R;
import com.aceman.go4lunch.adapter.WorkersAdapter;
import com.aceman.go4lunch.data.models.RestaurantPublic;
import com.aceman.go4lunch.utils.events.RefreshEvent;
import com.bumptech.glide.Glide;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by Lionel JOFFRAY - on 12/06/2019.
 * <p>
 * Workmates Fragment, the third fragment, shows the list of workmates and their choices.
 */
public class WorkmatesFragment extends Fragment implements WorkmatesContract.WorkmatesViewInterface {
    public WorkersAdapter mWorkersAdapter;
    public List<RestaurantPublic> mUserList = new ArrayList<>();
    @BindView(R.id.workmate_fragment_recycler_view)
    RecyclerView mRecyclerView;
    WorkmatesPresenter mPresenter;

    public WorkmatesFragment() {
    }

    public static WorkmatesFragment newInstance() {
        return new WorkmatesFragment();
    }

    /**
     * When created, the presenter is initialized, recyclerview too.
     * Toolbar autocomplete search is removed.
     *
     * @param inflater           inflate
     * @param container          container
     * @param savedInstanceState savedInstanceState
     * @return view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_workmates, container, false);
        ButterKnife.bind(this, view);
        mPresenter = new WorkmatesPresenter();
        mPresenter.attachView(this);
        mUserList = mPresenter.getUserList();
        setHasOptionsMenu(true);
        return view;
    }

    /**
     * Register EventBus on start.
     */
    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    /**
     * Unregister EventBus on stop.
     */
    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    /**
     * remove Autocomplete search for this page.
     *
     * @param menu menu
     */
    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.action_search);
        if (item != null)
            item.setVisible(false);
    }

    /**
     * get fresh list on resume.
     */
    @Override
    public void onResume() {
        super.onResume();
        mUserList = mPresenter.getUserList();
    }

    /**
     * Configure the recycler view.
     */
    @Override
    public void configureRecyclerView() {
        if (mUserList != null) {
            mWorkersAdapter = new WorkersAdapter(mUserList, Glide.with(this), getContext());
            mRecyclerView.setAdapter(mWorkersAdapter);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            mRecyclerView.addItemDecoration(new DividerItemDecoration(Objects.requireNonNull(getContext()), DividerItemDecoration.VERTICAL));
        }
    }

    /**
     * Refresh event to get the userlist.
     *
     * @param refreshEvent event
     */
    @Subscribe
    public void onRefreshEvent(RefreshEvent refreshEvent) {
        configureRecyclerView();
    }
}
