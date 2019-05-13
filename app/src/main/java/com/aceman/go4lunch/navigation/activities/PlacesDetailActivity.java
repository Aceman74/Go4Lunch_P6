package com.aceman.go4lunch.navigation.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.aceman.go4lunch.R;
import com.aceman.go4lunch.base.BaseActivity;
import com.aceman.go4lunch.navigation.adapter.WorkersAdapter;
import com.bumptech.glide.Glide;

import java.util.Objects;

import butterknife.BindView;

import static com.aceman.go4lunch.navigation.activities.CoreActivity.mResults;

public class PlacesDetailActivity extends BaseActivity {

    @BindView(R.id.places_details_recycler_view)
    RecyclerView mRecyclerView;
    WorkersAdapter mWorkersAdapter;
    Context mContext;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.detail_fragment_banner_name)
    TextView setName;
    @BindView(R.id.detail_fragment_banner_address)
    TextView setAdress;
    String mName;
    String mAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSupportActionBar(mToolbar);
        configureToolbar();
        configureRecyclerView();

        Intent details = getIntent();
       mName = details.getStringExtra("name");
        mAddress = details.getStringExtra("address");
        configureInfos();
    }

    private void configureInfos() {
        setName.setText(mName);
        setAdress.setText(mAddress);

    }

    @Override
    public int getFragmentLayout() {
        return R.layout.activity_places_detail;
    }

    public void configureRecyclerView() {
        if (mResults != null) {
            mWorkersAdapter = new WorkersAdapter(mResults, Glide.with(this),mContext);
            mRecyclerView.setAdapter(mWorkersAdapter);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
            mRecyclerView.addItemDecoration(new DividerItemDecoration(Objects.requireNonNull(this.getApplicationContext()), DividerItemDecoration.VERTICAL));
        }
    }
}
