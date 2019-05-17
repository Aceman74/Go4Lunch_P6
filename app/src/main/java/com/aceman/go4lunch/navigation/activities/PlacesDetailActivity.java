package com.aceman.go4lunch.navigation.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aceman.go4lunch.R;
import com.aceman.go4lunch.api.RestaurantHelper;
import com.aceman.go4lunch.api.UserHelper;
import com.aceman.go4lunch.base.BaseActivity;
import com.aceman.go4lunch.data.nearby_search.Result;
import com.aceman.go4lunch.models.User;
import com.aceman.go4lunch.navigation.adapter.WorkersJoiningAdapter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import timber.log.Timber;


public class PlacesDetailActivity extends BaseActivity {

    private static final int REQUEST_PHONE_CALL = 10;
    public List<Result> mResults = new ArrayList<>();
    @BindView(R.id.places_details_recycler_view)
    RecyclerView mRecyclerView;
    WorkersJoiningAdapter mWorkersJoiningAdapter;
    Context mContext;
    @BindView(R.id.detail_fragment_image_view)
    ImageView mImageView;
    @BindView(R.id.detail_fragment_banner_name)
    TextView setName;
    @BindView(R.id.detail_fragment_banner_address)
    TextView setAdress;
    @BindView(R.id.detail_fragment_rating_1)
    ImageView mStar1;
    @BindView(R.id.detail_fragment_rating_2)
    ImageView mStar2;
    @BindView(R.id.detail_fragment_rating_3)
    ImageView mStar3;
    @BindView(R.id.detail_fragment_btn_call)
    ImageButton mCallBtn;
    @BindView(R.id.detail_fragment_btn_website)
    ImageButton mWebsiteBtn;
    @BindView(R.id.detail_fragment_btn_like)
    ImageButton mLikeBtn;
    @BindView(R.id.call_layout)
    LinearLayout callLayout;
    @BindView(R.id.website_layout)
    LinearLayout websiteLayout;
    String mName;
    String mAddress;
    String mUrl;
    String mID;
    int mStar;
    @BindView(R.id.detail_fragment_btn)
    FloatingActionButton mFloatingActionButton;
    private String mPhone;
    private String mWebsite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        configureRecyclerView();

        Intent details = getIntent();
        mName = details.getStringExtra("name");
        mAddress = details.getStringExtra("address");
        mStar = details.getIntExtra("star", 0);
        mPhone = details.getStringExtra("phone");
        mWebsite = details.getStringExtra("website");
        mUrl = details.getStringExtra("url");
        mID = details.getStringExtra("id");
        nullCheck();
        configureInfos();
    }

    private void nullCheck() {
        if (mAddress == null)
            mAddress = "Adresse:";

        if (mWebsite == null)
            websiteLayout.setVisibility(View.GONE);

        if (mPhone == null) {
            callLayout.setVisibility(View.GONE);
        }
    }

    private void configureInfos() {
        showStar(mStar);
        setName.setText(mName);
        setAdress.setText(mAddress);
        UserHelper.getUser(getCurrentUser().getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User currentUser = documentSnapshot.toObject(User.class);
                if (currentUser.getRestaurant() != null && currentUser.getRestaurant().equals(mName)) {
                    mFloatingActionButton.setImageResource(R.drawable.done_icon);
                }
            }
        });
        try {
            Glide.with(this).asDrawable()
                    .load(mUrl) //  Base URL added in Data
                    .apply(RequestOptions.fitCenterTransform()) //  Adapt to placeholder size
                    .into(mImageView);
        } catch (Exception e) {
            Timber.tag("Image_Loading").e("Loading error");
        }
        selectBtnListener();
        likeBtnListener();
        callBtnListener();
        websiteBtnListener();
    }

    private void callBtnListener() {
        mCallBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission(PlacesDetailActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(PlacesDetailActivity.this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_PHONE_CALL);
                } else {
                    Intent callRestaurant = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + mPhone));
                    startActivity(callRestaurant);
                }
            }
        });
    }

    private void websiteBtnListener() {
        mWebsiteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mWebsite));
                startActivity(browserIntent);
            }
        });

    }

    private void selectBtnListener() {
        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                UserHelper.getUser(getCurrentUser().getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        User currentUser = documentSnapshot.toObject(User.class);

                        if (currentUser.getRestaurant() != null && currentUser.getRestaurant().equals(mID)) {

                            RestaurantHelper.updateRestaurant(getCurrentUser().getUid(), null).addOnFailureListener(onFailureListener());
                            mFloatingActionButton.setImageResource(R.drawable.add_icon);
                        } else {
                            RestaurantHelper.updateRestaurant(getCurrentUser().getUid(), mID).addOnFailureListener(onFailureListener());
                            mFloatingActionButton.setImageResource(R.drawable.done_icon);
                        }
                    }
                });
            }
        });
    }

    private void likeBtnListener() {
        mLikeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserHelper.getUser(getCurrentUser().getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        User currentUser = documentSnapshot.toObject(User.class);
                        if (currentUser.getLike() != null && currentUser.getLike().equals(mID)) {
                            UserHelper.updateLikeRestaurant(getCurrentUser().getUid(), null).addOnFailureListener(onFailureListener());
                        } else {
                            UserHelper.updateLikeRestaurant(getCurrentUser().getUid(), mID).addOnFailureListener(onFailureListener());
                        }
                    }
                });
            }
        });
    }

    private void showStar(int star) {
        switch (star) {
            case 1:
                mStar1.setVisibility(View.VISIBLE);
                break;
            case 2:
                mStar1.setVisibility(View.VISIBLE);
                mStar2.setVisibility(View.VISIBLE);
                break;
            case 3:
                mStar1.setVisibility(View.VISIBLE);
                mStar2.setVisibility(View.VISIBLE);
                mStar3.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    protected OnFailureListener onFailureListener() {
        return super.onFailureListener();
    }

    @Nullable
    @Override
    protected FirebaseUser getCurrentUser() {
        return super.getCurrentUser();

    }

    @Override
    protected Boolean isCurrentUserLogged() {
        return super.isCurrentUserLogged();
    }

    @Override
    public int getFragmentLayout() {
        return R.layout.activity_places_detail;
    }

    public void configureRecyclerView() {
        if (mResults != null) {
            mWorkersJoiningAdapter = new WorkersJoiningAdapter(mResults, Glide.with(this), mContext);
            mRecyclerView.setAdapter(mWorkersJoiningAdapter);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
            mRecyclerView.addItemDecoration(new DividerItemDecoration(Objects.requireNonNull(this.getApplicationContext()), DividerItemDecoration.VERTICAL));
        }
    }
}
