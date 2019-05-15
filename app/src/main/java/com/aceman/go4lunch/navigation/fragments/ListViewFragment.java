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
    RecyclerView mRecyclerView;
    @BindView(R.id.list_view_progressbar)
    ProgressBar mProgressBar;
    String mName;
    String mAddress;
    Toolbar mToolbar;
    private int mStar;
    private String mWebsite;
    private String mPhone;


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

        return view;
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
    public void onMethodCallback(Result item, int star) {

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
