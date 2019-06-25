package com.aceman.go4lunch.activities.placesDetail;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aceman.go4lunch.R;
import com.aceman.go4lunch.adapter.WorkersJoiningAdapter;
import com.aceman.go4lunch.data.nearby_search.Result;
import com.aceman.go4lunch.models.Restaurant;
import com.aceman.go4lunch.models.RestaurantPublic;
import com.aceman.go4lunch.utils.base.BaseActivity;
import com.aceman.go4lunch.utils.events.PlacesDetailEvent;
import com.aceman.go4lunch.utils.events.RefreshEvent;
import com.aceman.go4lunch.utils.events.RestaurantPublicEvent;
import com.aceman.go4lunch.utils.events.UserJoiningRefreshEvent;
import com.aceman.go4lunch.utils.events.UserListEvent;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseUser;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import timber.log.Timber;


public class PlacesDetailActivity extends BaseActivity implements PlacesDetailContract.PlacesDetailViewInterface {

    private static final int REQUEST_PHONE_CALL = 10;
    public static String mName;
    static String mAddress;
    static String mUrl;
    static String mID;
    static int mStar;
    static private String mPhone;
    static private String mWebsite;
    static private String mIntentString;
    public List<RestaurantPublic> mUserList = new ArrayList<>();
    public List<RestaurantPublic> mUserJoinning = new ArrayList<>();
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
    @BindView(R.id.no_lunch_layout)
    LinearLayout noLunch;
    @BindView(R.id.back_to_map)
    Button mBackToMap;
    @BindView(R.id.detail_fragment_btn)
    FloatingActionButton mFloatingActionButton;
    private PlacesDetailPresenter mPresenter;
    private Result mResult;
    private RestaurantPublic mRestaurantPublic;
    private Restaurant mRestaurant = new Restaurant();
    private Intent mIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new PlacesDetailPresenter();
        mPresenter.attachView(this);
        configureRecyclerView();
        getAnyIntent();
    }

    @Override
    public void getAnyIntent() {
        mIntent = getIntent();
        mIntentString = mIntent.getStringExtra("detail_intent");
    }

    @Override
    public void onUpdateFirebaseFailed() {
        Toast.makeText(this, "An error occured.", Toast.LENGTH_LONG).show();
    }

    @Override
    public void likeBtnRemoveColor() {
        mLikeBtn.setColorFilter(getResources().getColor(R.color.colorAccent));
    }

    @Override
    public void likeBtnAddColor() {
        mLikeBtn.setColorFilter(getResources().getColor(R.color.quantum_yellow));
    }

    @Override
    public void setInfosWithSnapshot(RestaurantPublic currentUser) {
        noLunch.setVisibility(View.GONE);

        mName = currentUser.getDetails().getName();
        mAddress = currentUser.getDetails().getAddress();
        mPhone = currentUser.getDetails().getPhone();
        mWebsite = currentUser.getDetails().getWebsite();
        mStar = currentUser.getDetails().getRating();
        mUrl = currentUser.getDetails().getImageUrl();
        mID = currentUser.getDetails().getPlaceID();
        showInfos();
    }

    @Override
    public void setFloatingBtnTint() {
        mFloatingActionButton.setImageResource(R.drawable.done_icon);
        mFloatingActionButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.quantum_vanillagreen500)));
    }

    @Override
    public void setLikeBtnTint() {
        mLikeBtn.setColorFilter(getResources().getColor(R.color.quantum_yellow));
    }

    @Override
    public void floatingBtnNullStyle() {
        mFloatingActionButton.setImageResource(R.drawable.add_icon);
        mFloatingActionButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
    }

    @Override
    public void floatingBtnAddStyle() {
        mFloatingActionButton.setImageResource(R.drawable.done_icon);
        mFloatingActionButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.quantum_vanillagreen500)));
    }


    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        getAnyIntent();
        configureInfos();
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void nullCheck() {
        if (mAddress == null)
            mAddress = getString(R.string.no_address);

        if (mWebsite == null)
            websiteLayout.setVisibility(View.GONE);

        if (mPhone == null) {
            callLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void configureInfos() {

        if (mIntentString != null && mIntentString.equals(getString(R.string.adapter))) {    //  Intent from Adapter click

            mName = mResult.getName();
            mAddress = mResult.getFormattedAddress();
            mPhone = mResult.getFormattedPhoneNumber();
            mWebsite = mResult.getWebsite();
            mStar = mResult.getRatingStars();
            mID = mResult.getPlaceId();
            showInfos();
        }
        if (mIntentString != null && mIntentString.equals(getString(R.string.workers))) {

            mName = mRestaurantPublic.getDetails().getName();
            mAddress = mRestaurantPublic.getDetails().getAddress();
            mPhone = mRestaurantPublic.getDetails().getPhone();
            mWebsite = mRestaurantPublic.getDetails().getWebsite();
            mStar = mRestaurantPublic.getDetails().getRating();
            mID = mRestaurantPublic.getDetails().getPlaceID();
            mUrl = mRestaurantPublic.getDetails().getImageUrl();
            showInfos();

        }
        if (mIntentString == null || mIntentString.equals(getString(R.string.lunch))) {      //  Intent from My Lunch click
            mPresenter.lunchIntentSetInfos();
        }
    }

    @Override
    public void showInfos() {
        mRestaurant.setAddress(mAddress);
        mRestaurant.setName(mName);
        mRestaurant.setPhone(mPhone);
        mRestaurant.setPlaceID(mID);
        mRestaurant.setWebsite(mWebsite);
        mRestaurant.setImageUrl(mUrl);
        mRestaurant.setRating(mStar);
        showStar(mStar);
        setName.setText(mName);
        setAdress.setText(mAddress);
        mPresenter.resetPlaceChoiceIfNewDay();
        mPresenter.setIconTintWithFirebaseInfos(mID, mRestaurant);
        loadPlaceImageWithGlide();
        nullCheck();
        selectBtnListener();
        likeBtnListener();      // << With contract?
        callBtnListener();
        websiteBtnListener();
        startGettingUserList();
    }

    @Override
    public void loadPlaceImageWithGlide() {

        try {
            Glide.with(this).asDrawable()
                    .load(mUrl) //  Base URL added in Data
                    .apply(RequestOptions.fitCenterTransform()) //  Adapt to placeholder size
                    .into(mImageView);
        } catch (Exception e) {
            Timber.tag("Image_Loading").e("Loading error");
        }
    }

    @Override
    public void noLunchCase() {
        noLunch.setVisibility(View.VISIBLE);
        mBackToMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void callBtnListener() {
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

    @Override
    public void websiteBtnListener() {
        mWebsiteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mWebsite));
                startActivity(browserIntent);
            }
        });

    }

    @Override
    public void selectBtnListener() {

        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.onClickSelectFloatingBtn(mID, mRestaurant);
            }
        });
    }

    @Override
    public void toastAddPlace() {
        Toast.makeText(this, "You have added " + mRestaurant.getName() + " for lunch !", Toast.LENGTH_LONG).show();
    }

    @Override
    public void toastRemovePlace() {

        Toast.makeText(this, "You have removed " + mRestaurant.getName() + " for lunch !", Toast.LENGTH_LONG).show();
    }

    @Override
    public void likeBtnListener() {
        mLikeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.onClickLike(mName);

            }
        });
    }

    @Override
    public void toastRemoveLike() {
        Toast.makeText(this, "You don't like " + mRestaurant.getName() + " anymore !", Toast.LENGTH_LONG).show();
    }

    @Override
    public void toastAddLike() {
        Toast.makeText(this, "You now like " + mRestaurant.getName() + " !", Toast.LENGTH_LONG).show();
    }

    @Override
    public void showStar(int star) {
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

    @Override
    public void configureRecyclerView() {
        mWorkersJoiningAdapter = new WorkersJoiningAdapter(mUserJoinning, Glide.with(this), mContext);
        mRecyclerView.setAdapter(mWorkersJoiningAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(Objects.requireNonNull(this.getApplicationContext()), DividerItemDecoration.VERTICAL));
        if (mUserList == null || mUserJoinning == null) {
            mRecyclerView.setVisibility(View.GONE);
        }
    }

    @Subscribe(sticky = true)
    public void onPlacesDetailEvent(PlacesDetailEvent detail) {
        mResult = detail.mDetail;
        mUrl = detail.mUrl;
        mID = detail.mDetail.getPlaceId();
    }

    @Subscribe(sticky = true)
    public void onRestaurantPublicEvent(RestaurantPublicEvent restaurantPublic) {
        mRestaurantPublic = restaurantPublic.mRestaurantPublic;
    }

    @Subscribe
    public void onRefreshEvent(RestaurantPublicEvent restaurantPublic) {
        mRestaurantPublic = restaurantPublic.mRestaurantPublic;
    }

    @Subscribe
    public void onUserJoiningRefreshEvent(UserJoiningRefreshEvent refreshEvent) {
        notifyDataChanged();
    }

    @Override
    public void startGettingUserList() {
        mUserList = mPresenter.getUserList();
        mUserJoinning = mPresenter.getUserJoinningList(mName);
        EventBus.getDefault().post(new UserListEvent(mUserList));
        notifyDataChanged();
    }

    @Override
    public void notifyDataChanged() {
        configureRecyclerView();
        mWorkersJoiningAdapter.notifyDataSetChanged();
    }
}
