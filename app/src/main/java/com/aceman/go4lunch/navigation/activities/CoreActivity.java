package com.aceman.go4lunch.navigation.activities;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.TextView;

import com.aceman.go4lunch.R;
import com.aceman.go4lunch.api.PlacesApi;
import com.aceman.go4lunch.api.UserHelper;
import com.aceman.go4lunch.auth.ProfileActivity;
import com.aceman.go4lunch.base.BaseActivity;
import com.aceman.go4lunch.data.details.PlacesDetails;
import com.aceman.go4lunch.data.nearby_search.Result;
import com.aceman.go4lunch.models.User;
import com.aceman.go4lunch.navigation.adapter.ListViewAdapter;
import com.aceman.go4lunch.navigation.adapter.PageAdapter;
import com.aceman.go4lunch.navigation.adapter.WorkersAdapter;
import com.aceman.go4lunch.navigation.fragments.ListViewFragment;
import com.aceman.go4lunch.utils.ProgressBarCallback;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import timber.log.Timber;

import static com.aceman.go4lunch.navigation.fragments.MapsFragment.mMaps;

/**
 * Created by Lionel JOFFRAY - on 03/05/2019.
 */
public class CoreActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener, BottomNavigationView.OnNavigationItemSelectedListener, ProgressBarCallback {
    private static final int SIGN_OUT_TASK = 10;
    int AUTOCOMPLETE_REQUEST_CODE = 8;
    public static List<Result> mResults;
    public static List<User> mUserList = new ArrayList<>();
    public static ListViewAdapter mListViewAdapter;
    public static WorkersAdapter mWorkersAdapter;
    public static FusedLocationProviderClient sFusedLocationProviderClient;
    public static LatLng mSearchLatLng;
    public static String mSearchName;
    String mLastSearch = "";
    String mSearchID;
    @BindView(R.id.core_nav_view)
    NavigationView mNavigationView;
    @BindView(R.id.core_bottom_navigation)
    BottomNavigationView mBottomNavigationView;
    @BindView(R.id.activity_core_drawer_layout)
    DrawerLayout mDrawerLayout;
    TextView textViewUsername;
    TextView textViewEmail;
    ImageView imageViewProfile;
    @BindView(R.id.core_viewpager)
    ViewPager pager;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    ImageView mProfileImage;
    Disposable mSearchDisposable;




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        navigationDrawerListener();
        configureToolBar();
        configureNavigationView();
        configureViewPager();
        pagerListener();
        configureHamburgerBtn();
        onClickProfile();
    }

    private void onClickProfile() {
        mProfileImage = mNavigationView.getHeaderView(0).findViewById(R.id.profile_image_nav_header);
        mProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent profile = new Intent(getApplicationContext(), ProfileActivity.class);
                getApplicationContext().startActivity(profile);

            }
        });
    }

    private void configureToolBar() {
        setSupportActionBar(mToolbar);
    }

    private void configureViewPager() {
        pager.setAdapter(new PageAdapter(getSupportFragmentManager(), getApplicationContext()));
    }

    private void navigationDrawerListener() {
        NavigationView navigationView = findViewById(R.id.core_nav_view);
        View headerView = navigationView.getHeaderView(0);
        textViewUsername = headerView.findViewById(R.id.name_nav_header);
        textViewEmail = headerView.findViewById(R.id.email_nav_header);
        imageViewProfile = headerView.findViewById(R.id.profile_image_nav_header);
        updateUIWhenCreating();
    }

    @Override
    public int getFragmentLayout() {
        return R.layout.activity_core;
    }

    private void updateUIWhenCreating() {

        if (this.getCurrentUser() != null) {

            if (this.getCurrentUser().getPhotoUrl() != null) {
                Glide.with(this)
                        .load(this.getCurrentUser().getPhotoUrl())
                        .apply(RequestOptions.circleCropTransform())
                        .into(imageViewProfile);
            }

            String email = TextUtils.isEmpty(this.getCurrentUser().getEmail()) ? getString(R.string.info_no_email_found) : this.getCurrentUser().getEmail();

            this.textViewEmail.setText(email);

            // 7 - Get additional data from Firestore (isPrivate & Username)
            UserHelper.getUser(this.getCurrentUser().getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    User currentUser = documentSnapshot.toObject(User.class);
                    String username = TextUtils.isEmpty(currentUser.getUsername()) ? getString(R.string.info_no_username_found) : currentUser.getUsername();
                    textViewUsername.setText(username);
                }
            });
        }
    }

    private void configureHamburgerBtn() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.popup_message_choice_yes, R.string.popup_message_choice_no);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void configureNavigationView() {
        mNavigationView.setNavigationItemSelectedListener(this);
        mBottomNavigationView.setOnNavigationItemSelectedListener(this);
    }

    void pagerListener() {
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                switch (i) {
                    case 0:
                        mBottomNavigationView.setSelectedItemId(R.id.bottom_maps);
                        mToolbar.setTitle("I'm Hungry !");
                        break;
                    case 1:
                        mBottomNavigationView.setSelectedItemId(R.id.bottom_list_view);
                        mToolbar.setTitle("I'm Hungry !");
                        break;
                    case 2:
                        mBottomNavigationView.setSelectedItemId(R.id.bottom_workmates);
                        mToolbar.setTitle("Avaiable Workmates");
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        //  Navigation Drawer item settings
        int id = item.getItemId();

        switch (id) {
            case R.id.drawer_your_lunch:
                Intent lunch = new Intent(this, PlacesDetailActivity.class);
                this.startActivity(lunch);
                break;
            case R.id.drawer_settings:
                Intent notification = new Intent(this, ProfileActivity.class);
                this.startActivity(notification);
                break;
            case R.id.drawer_logout:
                AuthUI.getInstance()
                        .signOut(this)
                        .addOnSuccessListener(this, this.updateUIAfterRESTRequestsCompleted(SIGN_OUT_TASK));
                break;
            case R.id.bottom_maps:
                pager.setCurrentItem(0);
                Timber.i("Click Maps");
                break;
            case R.id.bottom_list_view:
                pager.setCurrentItem(1);
                Timber.i("Click ListView");
                break;
            case R.id.bottom_workmates:
                pager.setCurrentItem(2);
                Timber.i("Click Workmates");
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);

        searchItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                autocompleteIntent();
                return false;
            }
        });

        return true;
    }

    public void autocompleteIntent() {

        // Set the fields to specify which types of place data to
// return after the user has made a selection.
        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME);

// Start the autocomplete intent.
        Intent intent = new Autocomplete.IntentBuilder(
                AutocompleteActivityMode.OVERLAY, fields)
                .setInitialQuery(mLastSearch)
                .setTypeFilter(TypeFilter.ESTABLISHMENT)
                .setCountry("fr")
                .build(getApplicationContext());
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                mSearchID = place.getId();
                getSearchRestaurant();
                mLastSearch = place.getName();
                Timber.i("Place: " + place.getName() + ", " + place.getId());
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
                Timber.i(status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    void getSearchRestaurant(){


                this.mSearchDisposable = PlacesApi.getInstance().getRestaurantsDetails(mSearchID).subscribeWith(new DisposableObserver<PlacesDetails>() {
                    @Override
                    public void onNext(PlacesDetails details) {
                        Timber.tag("PLACES_Next").i("On Next");
                        addDetail(details);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.tag("PLACES_Error").e("On Error%s", Log.getStackTraceString(e));
                    }

                    @Override
                    public void onComplete() {
                        Timber.tag("PLACES_Complete").i("On Complete !!");

                        mMaps.animateCamera(CameraUpdateFactory.newLatLngZoom(mSearchLatLng, 14));
                    }
                });
    }

    private void addDetail(PlacesDetails details) {
        mSearchLatLng = new LatLng(details.getResult().getGeometry().getLocation().getLat(), details.getResult().getGeometry().getLocation().getLng());
        mSearchName = details.getResult().getName();
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

    private OnSuccessListener<Void> updateUIAfterRESTRequestsCompleted(final int origin) {
        return new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                if (origin == SIGN_OUT_TASK) {
                    finish();
                }
            }
        };
    }

    @Override
    public void onProgressCallback() {
        ListViewFragment frag = (ListViewFragment)
                getSupportFragmentManager().findFragmentById(R.id.list_linear_layout);

    }

    @Override
    public void onFinishCallback() {
        ListViewFragment frag = (ListViewFragment)
                getSupportFragmentManager().findFragmentById(R.id.list_linear_layout);
    }
}
