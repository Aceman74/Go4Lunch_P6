package com.aceman.go4lunch.activities.placesDetail;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import com.aceman.go4lunch.data.models.Restaurant;
import com.aceman.go4lunch.data.models.RestaurantPublic;
import com.aceman.go4lunch.data.places.nearby_search.Result;
import com.aceman.go4lunch.utils.base.BaseActivity;
import com.aceman.go4lunch.utils.events.PlacesDetailEvent;
import com.aceman.go4lunch.utils.events.RestaurantPublicEvent;
import com.aceman.go4lunch.utils.events.UserJoiningRefreshEvent;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseUser;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import timber.log.Timber;

/**
 * Created by Lionel JOFFRAY - on 10/05/2019.
 * <p>
 * Place Detail Activity is the detailled view when user click on a restaurant.
 * This activity is opened from navigation drawer (Your lunch), ListViewFragment and WorkmatesFragment.
 */
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
    @BindView(R.id.detail_fragment_image_view)
    ImageView mImageView;
    @BindView(R.id.detail_fragment_banner_name)
    TextView setName;
    @BindView(R.id.detail_fragment_banner_address)
    TextView setAddress;
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

    /**
     * When created, presenter is initialized, and the recyclerview for Joining User is initialized.
     * The activity also check for any Intent (My Lunch, Restaurant from list or from a click on a workmate).
     *
     * @param savedInstanceState savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new PlacesDetailPresenter();
        mPresenter.attachView(this);
        configureRecyclerView();
        getAnyIntent();
    }

    /**
     * Check for intent.
     */
    @Override
    public void getAnyIntent() {
        mIntent = getIntent();
        mIntentString = mIntent.getStringExtra(getString(R.string.detail_intent));
    }

    /**
     * Message when updating a restaurant (add or removed) failed.
     */
    @Override
    public void onUpdateFirebaseFailed() {
        Toast.makeText(this, (R.string.error_unknown_error), Toast.LENGTH_LONG).show();
    }

    /**
     * Set the like btn "null" color when clicked.
     */
    @Override
    public void likeBtnNullColor() {
        mLikeBtn.setColorFilter(getResources().getColor(R.color.colorAccent));
    }

    /**
     * Set the like btn "add" color when clicked.
     */
    @Override
    public void likeBtnAddedColor() {
        mLikeBtn.setColorFilter(getResources().getColor(R.color.quantum_yellow));
    }

    /**
     * Set the floating btn "null" state.
     */
    @Override
    public void floatingBtnNullStyle() {
        mFloatingActionButton.setImageResource(R.drawable.add_icon);
        mFloatingActionButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
    }

    /**
     * Set the floating btn "add" state.
     */
    @Override
    public void floatingBtnAddedStyle() {
        mFloatingActionButton.setImageResource(R.drawable.done_icon);
        mFloatingActionButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.quantum_vanillagreen500)));
    }

    /**
     * Register Eventbus onStart.
     */
    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    /**
     * Update infos on Resuming.
     */
    @Override
    public void onResume() {
        super.onResume();
        getAnyIntent();
        configureInfo();
    }

    /**
     * Unregister Eventbus onStop.
     */
    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    /**
     * Check for null information on restaurant (ex: no website) and Hide the view.
     */
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

    /**
     * This method set informations when the intent comes from My Lunch.
     *
     * @param currentUser currentUser
     */
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
        showInfo();
    }

    /**
     * Set the info by the corresponding intent (from adapter = from ListViewFragment)
     */
    @Override
    public void configureInfo() {

        if (mIntentString != null && mIntentString.equals(getString(R.string.adapter))) {    //  Intent from Adapter click

            mName = mResult.getName();
            mAddress = mResult.getFormattedAddress();
            mPhone = mResult.getFormattedPhoneNumber();
            mWebsite = mResult.getWebsite();
            mStar = mResult.getRatingStars();
            mID = mResult.getPlaceId();
            showInfo();
        }
        if (mIntentString != null && mIntentString.equals(getString(R.string.workers))) {

            mName = mRestaurantPublic.getDetails().getName();
            mAddress = mRestaurantPublic.getDetails().getAddress();
            mPhone = mRestaurantPublic.getDetails().getPhone();
            mWebsite = mRestaurantPublic.getDetails().getWebsite();
            mStar = mRestaurantPublic.getDetails().getRating();
            mID = mRestaurantPublic.getDetails().getPlaceID();
            mUrl = mRestaurantPublic.getDetails().getImageUrl();
            showInfo();

        }
        if (mIntentString == null || mIntentString.equals(getString(R.string.lunch))) {      //  Intent from My Lunch click
            mPresenter.lunchIntentSetInfos();
        }
    }

    /**
     * Updating the view with the information from ConfigureInfo method.
     */
    @Override
    public void showInfo() {
        mRestaurant.setAddress(mAddress);
        mRestaurant.setName(mName);
        mRestaurant.setPhone(mPhone);
        mRestaurant.setPlaceID(mID);
        mRestaurant.setWebsite(mWebsite);
        mRestaurant.setImageUrl(mUrl);
        mRestaurant.setRating(mStar);
        showStar(mStar);
        setName.setText(mName);
        setAddress.setText(mAddress);
        mPresenter.resetPlaceChoiceIfNewDay();
        mPresenter.setIconTintWithFirebaseInfos(mID, mRestaurant);
        loadPlaceImageWithGlide();
        nullCheck();
        selectBtnListener();
        likeBtnListener();
        callBtnListener();
        websiteBtnListener();
        startGettingUserList();
    }

    /**
     * Load the restaurant picture with Glide.
     *
     * @see Glide
     */
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

    /**
     * Add a specific view if no lunch is selected when clicked on nav drawer.
     */
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

    /**
     * Call btn listener for calling the place.
     */
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

    /**
     * Website btn open in the phone's browser.
     */
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

    /**
     * Select place btn listener, add or remove the place for lunch and store/Remove it in Firestore.
     */
    @Override
    public void selectBtnListener() {

        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.onClickSelectFloatingBtn(mID, mRestaurant);
            }
        });
    }

    /**
     * Toast to inform user adding place.
     */
    @Override
    public void toastAddPlace() {
        Toast.makeText(this, getString(R.string.you_added) + mRestaurant.getName() + getString(R.string.for_lunch), Toast.LENGTH_LONG).show();
    }

    /**
     * Toast to inform user removing place.
     */
    @Override
    public void toastRemovePlace() {

        Toast.makeText(this, getString(R.string.you_removed) + mRestaurant.getName() + getString(R.string.for_lunch), Toast.LENGTH_LONG).show();
    }

    /**
     * The like btn listener method.
     */
    @Override
    public void likeBtnListener() {
        mLikeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.onClickLike(mName);

            }
        });
    }

    /**
     * Toast to inform user removing liked place.
     */
    @Override
    public void toastRemoveLike() {
        Toast.makeText(this, getString(R.string.you_dont_like) + mRestaurant.getName() + getString(R.string.anymore), Toast.LENGTH_LONG).show();
    }

    /**
     * Toast to inform user adding liked place.
     */
    @Override
    public void toastAddLike() {
        Toast.makeText(this, getString(R.string.you_now_like) + mRestaurant.getName() + getString(R.string.exclam), Toast.LENGTH_LONG).show();
    }

    /**
     * Show the rank of the place.
     *
     * @param star number of star to show
     */
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

    /**
     * On fail listener.
     *
     * @return fail message.
     * @see BaseActivity
     */
    @Override
    protected OnFailureListener onFailureListener() {
        return super.onFailureListener();
    }

    /**
     * Get the current logged user.
     *
     * @return current user
     * @see BaseActivity
     */
    @Nullable
    @Override
    protected FirebaseUser getCurrentUser() {
        return super.getCurrentUser();
    }

    /**
     * Check if the current user is logged.
     *
     * @return current user logged
     * @see BaseActivity
     */
    @Override
    protected Boolean isCurrentUserLogged() {
        return super.isCurrentUserLogged();
    }

    /**
     * Get Layout.
     *
     * @return layout
     * @see BaseActivity
     */
    @Override
    public int getActivityLayout() {
        return R.layout.activity_places_detail;
    }

    /**
     * Configure the recyclerview for the actual place, with co-worker who have the same are showing.
     */
    @Override
    public void configureRecyclerView() {
        mWorkersJoiningAdapter = new WorkersJoiningAdapter(mUserJoinning, Glide.with(this), getApplicationContext());
        mRecyclerView.setAdapter(mWorkersJoiningAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        if (mUserList == null || mUserJoinning == null) {
            mRecyclerView.setVisibility(View.GONE);
        }
    }

    /**
     * Eventbus to get ListViewFragment Intent
     *
     * @param detail detailed restaurant clicked
     */
    @Subscribe(sticky = true)
    public void onPlacesDetailEvent(PlacesDetailEvent detail) {
        mResult = detail.mDetail;
        mUrl = detail.mUrl;
        mID = detail.mDetail.getPlaceId();
    }

    /**
     * Eventbus to get WormatesFragment Intent
     *
     * @param restaurantPublic detailed restaurant clicked
     */
    @Subscribe(sticky = true)
    public void onRestaurantPublicEvent(RestaurantPublicEvent restaurantPublic) {
        mRestaurantPublic = restaurantPublic.mRestaurantPublic;
    }

    /**
     * Eventbus used for updating the view when user add or removed a restaurant.
     * Apply when startGettingUserList() is done.
     *
     * @param refreshEvent simple callback
     */
    @Subscribe
    public void onUserJoiningRefreshEvent(UserJoiningRefreshEvent refreshEvent) {
        mPresenter.setIconTintWithFirebaseInfos(mID, mRestaurant);
        notifyDataChanged();
    }

    /**
     * Get the public userlist on firestore, to compare it and show if user join your lunch.
     */
    @Override
    public void startGettingUserList() {
        mUserList = mPresenter.getUserList();
        mUserJoinning = mPresenter.getUserJoiningList(mName);
        notifyDataChanged();
    }

    /**
     * Updating recyclerview when all data are ready.
     */
    @Override
    public void notifyDataChanged() {
        configureRecyclerView();
    }
}
