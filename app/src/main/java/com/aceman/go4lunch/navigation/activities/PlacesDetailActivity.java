package com.aceman.go4lunch.navigation.activities;

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
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aceman.go4lunch.R;
import com.aceman.go4lunch.api.RestaurantPublicHelper;
import com.aceman.go4lunch.api.UserHelper;
import com.aceman.go4lunch.base.BaseActivity;
import com.aceman.go4lunch.data.nearby_search.Result;
import com.aceman.go4lunch.events.PlacesDetailEvent;
import com.aceman.go4lunch.events.UserListEvent;
import com.aceman.go4lunch.models.Restaurant;
import com.aceman.go4lunch.models.RestaurantPublic;
import com.aceman.go4lunch.models.User;
import com.aceman.go4lunch.navigation.adapter.WorkersJoiningAdapter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import timber.log.Timber;


public class PlacesDetailActivity extends BaseActivity {

    private static final int REQUEST_PHONE_CALL = 10;
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

    public static String mName;
    static String mAddress;
    static String mUrl;
    static String mID;
    static int mStar;
    @BindView(R.id.detail_fragment_btn)
    FloatingActionButton mFloatingActionButton;
    static private String mPhone;
    static private String mWebsite;
    public List<RestaurantPublic> mUserList = new ArrayList<>();
    private Result mResult;
    private Restaurant mRestaurant = new Restaurant();
    private Intent mIntent;
    static private String mIntentString;
    private boolean mNoLunch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getAnyIntent();
        configureRecyclerView();
    }

    private void getAnyIntent() {
        mIntent = getIntent();
        mIntentString = mIntent.getStringExtra("detail_intent");
    }

    @Subscribe(sticky = true)
    public void onPlacesDetailEvent(PlacesDetailEvent detail) {
        mResult = detail.mDetail;
        mUrl = detail.mUrl;
        mID = detail.mDetail.getPlaceId();
        configureInfos();
        nullCheck();

    }

    @Subscribe(sticky = true)
    public void onUserListEvent(UserListEvent userlist) {
        mUserList = userlist.mUserList;
        configureRecyclerView();
        mWorkersJoiningAdapter.notifyDataSetChanged();

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

    private void nullCheck() {
        if (mAddress == null)
            mAddress = getString(R.string.no_address);

        if (mWebsite == null)
            websiteLayout.setVisibility(View.GONE);

        if (mPhone == null) {
            callLayout.setVisibility(View.GONE);
        }
    }

    private void configureInfos() {


        if (mIntentString.equals(getString(R.string.adapter))) {    //  Intent from Adapter click

            mName = mResult.getName();
            mAddress = mResult.getFormattedAddress();
            mPhone = mResult.getFormattedPhoneNumber();
            mWebsite = mResult.getWebsite();
            mStar = mResult.getRatingStars();
            mID = mResult.getPlaceId();
        }
        if (mIntentString.equals(getString(R.string.lunch))) {      //  Intent from My Lunch click
            RestaurantPublicHelper.getUser(Objects.requireNonNull(getCurrentUser()).getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    RestaurantPublic currentUser = documentSnapshot.toObject(RestaurantPublic.class);
                    if (currentUser.getDetails() != null) {
                        noLunch.setVisibility(View.GONE);

                        mName = currentUser.getDetails().getName();
                        mAddress = currentUser.getDetails().getAddress();
                        mPhone = currentUser.getDetails().getPhone();
                        mWebsite = currentUser.getDetails().getWebsite();
                        mStar = currentUser.getDetails().getRating();
                        mUrl = currentUser.getDetails().getImageUrl();
                        mID = currentUser.getDetails().getPlaceID();
                    } else {
                        noLunchClick();

                    }
                }
            });
        }

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
        UserHelper.getUser(getCurrentUser().getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User currentUser = documentSnapshot.toObject(User.class);
                if (currentUser.getRestaurant() != null && currentUser.getRestaurant().equals(mID)) {
                    mFloatingActionButton.setImageResource(R.drawable.done_icon);
                    mFloatingActionButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.quantum_vanillagreen500)));
                }
                if(currentUser.getLike() != null && currentUser.getLike().equals(mRestaurant.getPlaceID())){
                    mLikeBtn.setColorFilter(getResources().getColor(R.color.quantum_yellow));
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

    private void noLunchClick() {
        noLunch.setVisibility(View.VISIBLE);
        mBackToMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
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

                            RestaurantPublicHelper.restaurantPublic(getCurrentUser().getUid(), null, null, null, null).addOnFailureListener(onFailureListener());
                            UserHelper.updateRestaurantID(getCurrentUser().getUid(), null, null).addOnFailureListener(onFailureListener());
                            mFloatingActionButton.setImageResource(R.drawable.add_icon);
                            mFloatingActionButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
                            toastDeletePlace();
                        } else {
                            Date todayDate = Calendar.getInstance().getTime();
                            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                            String date = formatter.format(todayDate);
                            RestaurantPublicHelper.restaurantPublic(getCurrentUser().getUid(), mRestaurant.getPlaceID(), mRestaurant.getName(), mRestaurant, date).addOnFailureListener(onFailureListener());
                            UserHelper.updateRestaurantID(getCurrentUser().getUid(), mRestaurant.getPlaceID(), mRestaurant.getName()).addOnFailureListener(onFailureListener());
                            mFloatingActionButton.setImageResource(R.drawable.done_icon);
                            mFloatingActionButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.quantum_vanillagreen500)));
                            toastAddPlace();
                        }
                    }
                });
            }
        });
    }

    private void toastAddPlace() {
        Toast.makeText(this,"You have added " + mRestaurant.getName() + " for lunch !",Toast.LENGTH_LONG).show();
    }

    private void toastDeletePlace() {

        Toast.makeText(this,"You have removed " + mRestaurant.getName() + " for lunch !",Toast.LENGTH_LONG).show();
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
                            RestaurantPublicHelper.updateLikeRestaurant(getCurrentUser().getUid(), null).addOnFailureListener(onFailureListener());
                            mLikeBtn.setColorFilter(getResources().getColor(R.color.colorAccent));
                            toastRemoveLike();
                        } else {
                            UserHelper.updateLikeRestaurant(getCurrentUser().getUid(), mID).addOnFailureListener(onFailureListener());
                            RestaurantPublicHelper.updateLikeRestaurant(getCurrentUser().getUid(), mID).addOnFailureListener(onFailureListener());
                            mLikeBtn.setColorFilter(getResources().getColor(R.color.quantum_yellow));
                            toastAddLike();
                        }
                    }
                });
            }
        });
    }

    private void toastRemoveLike() {
        Toast.makeText(this,"You don't like " + mRestaurant.getName()+ " anymore !",Toast.LENGTH_LONG).show();
    }

    private void toastAddLike() {
        Toast.makeText(this,"You now like " + mRestaurant.getName()+ " !",Toast.LENGTH_LONG).show();
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
        if (mUserList != null) {
            mWorkersJoiningAdapter = new WorkersJoiningAdapter(mUserList, Glide.with(this), mContext);
            mRecyclerView.setAdapter(mWorkersJoiningAdapter);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
            mRecyclerView.addItemDecoration(new DividerItemDecoration(Objects.requireNonNull(this.getApplicationContext()), DividerItemDecoration.VERTICAL));
        } else {
            mRecyclerView.setVisibility(View.GONE);
        }
    }
}
