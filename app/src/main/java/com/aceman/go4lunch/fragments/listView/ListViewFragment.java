package com.aceman.go4lunch.fragments.listView;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.aceman.go4lunch.R;
import com.aceman.go4lunch.adapter.ListViewAdapter;
import com.aceman.go4lunch.data.models.RestaurantPublic;
import com.aceman.go4lunch.data.places.nearby_search.Result;
import com.aceman.go4lunch.utils.events.RefreshEvent;
import com.aceman.go4lunch.utils.events.ResultListEvent;
import com.aceman.go4lunch.utils.events.UserListEvent;
import com.bumptech.glide.Glide;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by Lionel JOFFRAY - on 12/06/2019.
 * <p>
 * ListView Fragment, the second fragment, shows in a recyclerview a list of restaurant (mResults) who comes from MapsFragment.
 */
public class ListViewFragment extends Fragment implements ListViewContract.ListViewViewInterface {
    public List<Result> mResults = new ArrayList<>();
    public List<RestaurantPublic> mUserList = new ArrayList<>();
    @BindView(R.id.restaurant_recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.list_view_progressbar)
    ProgressBar mProgressBar;
    @BindView(R.id.sort_by_name)
    ImageButton mByName;
    @BindView(R.id.sort_by_distance)
    ImageButton mByDistance;
    @BindView(R.id.no_result_layout)
    LinearLayout mNoResultLayout;
    ListViewPresenter mPresenter;
    private boolean mByDistanceBoolean = true;
    private boolean mByNameBoolean = true;
    private ListViewAdapter mListViewAdapter;


    public ListViewFragment() {
    }

    public static ListViewFragment newInstance() {
        return new ListViewFragment();
    }

    /**
     * When created, the presenter is initialized, buttons and recyclerview too.
     *
     * @param inflater           inflate
     * @param container          container
     * @param savedInstanceState savedInstanceState
     * @return view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_list_view, container, false);
        ButterKnife.bind(this, view);
        mPresenter = new ListViewPresenter();
        mPresenter.attachView(this);
        configureRecyclerView();
        configureBtn();
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
     * Get the mResult list from MapsFragment.
     */
    @Subscribe
    public void onResultListEvent(ResultListEvent result) {
        mResults = result.mResults;
        mListViewAdapter.notifyDataSetChanged();
    }

    /**
     * Get the userList .
     *
     * @param userlist userlist
     */
    @Subscribe(sticky = true)
    public void onUserListEvent(UserListEvent userlist) {
        mUserList = userlist.mUserList;
    }

    /**
     * Refresh callback when userJoining is ready, refresh Recycler View.
     *
     * @param refreshEvent refreshEvent
     */
    @Subscribe
    public void onRefreshEvent(RefreshEvent refreshEvent) {
        loadingView();
    }

    /**
     * Load UI when is visible.
     *
     * @param isVisibleToUser isVisibleToUser
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            loadingView();
            configureRecyclerView();
        }
    }

    /**
     * Show a different layout if mResults is empty or not.
     */
    @Override
    public void loadingView() {
        if (mResults.isEmpty()) {
            mNoResultLayout.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.GONE);
        } else {
            mNoResultLayout.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.GONE);
        }
    }

    /**
     * Configure sorts buttons.
     */
    @Override
    public void configureBtn() {
        mByName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mByNameBoolean)
                    sortByName();
                else {
                    Collections.reverse(mResults);
                    mListViewAdapter.notifyDataSetChanged();
                    mByNameBoolean = true;
                }
            }
        });

        mByDistance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mByDistanceBoolean)
                    sortByDistance();
                else {
                    Collections.reverse(mResults);
                    mListViewAdapter.notifyDataSetChanged();
                    mByDistanceBoolean = true;
                }
            }
        });

    }

    /**
     * Sort places by name .
     */
    @Override
    public void sortByName() {
        Collections.sort(mResults, new Comparator<Result>() {
            @Override
            public int compare(Result o1, Result o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
        mListViewAdapter.notifyDataSetChanged();
        mByNameBoolean = false;
    }

    /**
     * Sort places by distance( rounded to 5m).
     */
    @Override
    public void sortByDistance() {
        Collections.sort(mResults, new Comparator<Result>() {
            @Override
            public int compare(Result o1, Result o2) {
                int first = Math.round(o1.getDistanceTo());
                first = ((first + 4) / 5) * 5;
                int second = Math.round(o2.getDistanceTo());
                second = ((second + 4) / 5) * 5;
                if (first > second) {
                    return 1;
                } else if (first < second) {
                    return -1;
                } else {
                    return 0;
                }
            }
        });
        mListViewAdapter.notifyDataSetChanged();
        mByDistanceBoolean = false;
    }

    /**
     * Configure the recyclerview.
     */
    @Override
    public void configureRecyclerView() {
        mListViewAdapter = new ListViewAdapter(mResults, mUserList, Glide.with(this), getContext());
        mRecyclerView.setAdapter(mListViewAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(Objects.requireNonNull(getContext()), DividerItemDecoration.VERTICAL));
    }

    /**
     * Reload view on Resume.
     */
    @Override
    public void onResume() {
        super.onResume();
        loadingView();
    }

}
