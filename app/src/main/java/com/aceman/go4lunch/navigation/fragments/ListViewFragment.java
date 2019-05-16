package com.aceman.go4lunch.navigation.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.Toolbar;

import com.aceman.go4lunch.R;
import com.aceman.go4lunch.data.nearby_search.Result;
import com.aceman.go4lunch.navigation.activities.PlacesDetailActivity;
import com.aceman.go4lunch.navigation.adapter.ListViewAdapter;
import com.aceman.go4lunch.utils.AdapterCallback;
import com.aceman.go4lunch.utils.ProgressBarCallback;
import com.bumptech.glide.Glide;

import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.aceman.go4lunch.navigation.activities.CoreActivity.mListViewAdapter;
import static com.aceman.go4lunch.navigation.activities.CoreActivity.mResults;

/**
 * A simple {@link Fragment} subclass.
 */
public class ListViewFragment extends Fragment implements AdapterCallback, ProgressBarCallback {
    @BindView(R.id.restaurant_recycler_view)
    public static RecyclerView mRecyclerView;
    @BindView(R.id.list_view_progressbar)
    ProgressBar mProgressBar;
    String mName;
    String mAddress;
    Toolbar mToolbar;
    private int mStar;
    private String mWebsite;
    private String mPhone;
    @BindView(R.id.sort_by_name)
    ImageButton mByName;
    @BindView(R.id.sort_by_distance)
    ImageButton mByDistance;
    private boolean mByDistanceBoolean = true;
    private boolean mByNameBoolean = true;


    public ListViewFragment() {
        // Required empty public constructor
    }

    public static ListViewFragment newInstance() {
        return new ListViewFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_list_view, container, false);
        ButterKnife.bind(this, view);
        configureRecyclerView();
        configureBtn();
        return view;
    }

    private void configureBtn() {
        mByName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mByNameBoolean)
                sortByName();
                else{
                    Collections.reverse(mResults);
                    mListViewAdapter.notifyDataSetChanged();
                    mByNameBoolean = true;
                }
            }
        });

        mByDistance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mByDistanceBoolean)
                sortByDistance();
                else {
                    Collections.reverse(mResults);
                    mListViewAdapter.notifyDataSetChanged();
                    mByDistanceBoolean = true;
                }
            }
        });

    }

    private void sortByName() {
        Collections.sort(mResults, new Comparator<Result>() {
            @Override
            public int compare(Result o1, Result o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
        mListViewAdapter.notifyDataSetChanged();
        mByNameBoolean = false;
    }

    private void sortByDistance() {
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

    public void configureRecyclerView() {
        if (mResults != null) {
            mListViewAdapter = new ListViewAdapter(mResults, Glide.with(this), getContext(), this);
            mRecyclerView.setAdapter(mListViewAdapter);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            mRecyclerView.addItemDecoration(new DividerItemDecoration(Objects.requireNonNull(getContext()), DividerItemDecoration.VERTICAL));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onMethodCallback(Result item, int star, String url) {

        Toast.makeText(getContext(), "HELLO CALLBACK WORLD :" + item.getName(), Toast.LENGTH_LONG).show();
        mName = item.getName();
        mAddress = item.getFormattedAddress();
        mStar = star;
        mWebsite = item.getWebsite();
        mPhone = item.getFormattedPhoneNumber();
        Intent details = new Intent(getActivity(), PlacesDetailActivity.class);
        details.putExtra("name", mName);
        details.putExtra("address", mAddress);
        details.putExtra("star", mStar);
        details.putExtra("phone", mPhone);
        details.putExtra("website",mWebsite);
        details.putExtra("url", url);
        startActivity(details);

    }

    @Override
    public void onProgressCallback() {
        if(mRecyclerView != null && mProgressBar != null){
        mRecyclerView.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onFinishCallback() {
        if(mRecyclerView != null && mProgressBar != null){
        mRecyclerView.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.GONE);
        }
    }

}
