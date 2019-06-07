package com.aceman.go4lunch.activities.coreActivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.aceman.go4lunch.R;
import com.aceman.go4lunch.activities.settingsActivity.SettingsActivity;
import com.aceman.go4lunch.activities.profileActivity.ProfileActivity;
import com.aceman.go4lunch.utils.base.BaseActivity;
import com.aceman.go4lunch.data.details.PlacesDetails;
import com.aceman.go4lunch.utils.events.SearchRefreshEvent;
import com.aceman.go4lunch.utils.events.UserListEvent;
import com.aceman.go4lunch.activities.loginActivity.MainActivity;
import com.aceman.go4lunch.models.RestaurantPublic;
import com.aceman.go4lunch.models.User;
import com.aceman.go4lunch.adapter.PageAdapter;
import com.aceman.go4lunch.activities.placesDetailActivity.PlacesDetailActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import io.reactivex.disposables.Disposable;
import timber.log.Timber;

import static com.aceman.go4lunch.fragments.mapsFragment.MapsFragment.mMaps;

/**
 * Created by Lionel JOFFRAY - on 03/05/2019.
 */
public class CoreActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener, BottomNavigationView.OnNavigationItemSelectedListener, CoreContract.CoreViewInterface {
    private static final int SIGN_OUT_TASK = 10;
    public static FusedLocationProviderClient sFusedLocationProviderClient;
    public List<RestaurantPublic> mUserList = new ArrayList<>();
    int AUTOCOMPLETE_REQUEST_CODE = 8;
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
    private String mIntent;
    private CorePresenter mPresenter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new CorePresenter();
        mPresenter.attachView(this);
        sFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        navigationDrawerListener();
        configureToolBar();
        configureNavigationView();
        configureViewPager();
        pagerListener();
        configureHamburgerBtn();
        onClickProfile();
        mPresenter.getUserList();
    }

    @Override
    public void onClickProfile() {
        mProfileImage = mNavigationView.getHeaderView(0).findViewById(R.id.profile_image_nav_header);
        mProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent profile = new Intent(getApplicationContext(), ProfileActivity.class);
                profile.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(profile);

            }
        });
    }

    @Override
    public void configureToolBar() {
        setSupportActionBar(mToolbar);
    }

    @Override
    public void configureViewPager() {
        pager.setAdapter(new PageAdapter(getSupportFragmentManager(), getApplicationContext()));
    }

    @Override
    public void navigationDrawerListener() {
        NavigationView navigationView = findViewById(R.id.core_nav_view);
        View headerView = navigationView.getHeaderView(0);
        textViewUsername = headerView.findViewById(R.id.name_nav_header);
        textViewEmail = headerView.findViewById(R.id.email_nav_header);
        imageViewProfile = headerView.findViewById(R.id.profile_image_nav_header);
        mPresenter.updateUIWhenCreating();
    }

    @Override
    public int getFragmentLayout() {
        return R.layout.activity_core;
    }

    @Override
    public void signOutUserFromFirebase() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnSuccessListener(this, this.updateUIAfterRESTRequestsCompleted(SIGN_OUT_TASK));
        Intent start = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(start);
    }

    @Override
    public void configureHamburgerBtn() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.popup_message_choice_yes, R.string.popup_message_choice_no);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    @Override
    public void configureNavigationView() {
        mNavigationView.setNavigationItemSelectedListener(this);
        mBottomNavigationView.setOnNavigationItemSelectedListener(this);
    }

    @Override
    public void pagerListener() {
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
                mIntent = getString(R.string.lunch);
                lunch.putExtra("detail_intent", mIntent);
                this.startActivity(lunch);
                break;
            case R.id.drawer_settings:
                Intent settings = new Intent(this, SettingsActivity.class);
                this.startActivity(settings);
                break;
            case R.id.drawer_logout:
                signOutUserFromFirebase();
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

    @Override
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
                EventBus.getDefault().post(new SearchRefreshEvent(mSearchID));
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



    @Override
    public OnFailureListener onFailureListener() {
        return super.onFailureListener();
    }

    @Nullable
    @Override
    public FirebaseUser getCurrentUser() {
        return super.getCurrentUser();
    }

    @Override
    public Boolean isCurrentUserLogged() {
        return super.isCurrentUserLogged();
    }

    @Override
    public OnSuccessListener<Void> updateUIAfterRESTRequestsCompleted(final int origin) {
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
    public void setUserListFromFirebase(Task<QuerySnapshot> task) {
        for (QueryDocumentSnapshot document : task.getResult()) {
            RestaurantPublic userP = document.toObject(RestaurantPublic.class);     // < == GET LIST FIRESTORE
            mUserList.add(userP);
            Timber.tag("Task To List").i("Sucess");
        }
    }

    @Override
    public void errorGettingUserListFromFirebase(Task<QuerySnapshot> task){
        Timber.tag("Task Exeption").d(task.getException(), "Error getting documents: ");
    }

    @Override
    public void postStickyEventBusUserList() {
        EventBus.getDefault().postSticky(new UserListEvent(mUserList));
    }

    @Override
    public void loadUserImgWithGlide() {
        Glide.with(this)
            .load(this.getCurrentUser().getPhotoUrl())
            .apply(RequestOptions.circleCropTransform())
            .into(imageViewProfile);
    }

    @Override
    public void loadUserEmail() {
        String email = TextUtils.isEmpty(this.getCurrentUser().getEmail()) ? getString(R.string.info_no_email_found) : this.getCurrentUser().getEmail();
        this.textViewEmail.setText(email);
    }

    @Override
    public void loadUserUsername(User currentUser) {
        String username = TextUtils.isEmpty(currentUser.getUsername()) ? getString(R.string.info_no_username_found) : currentUser.getUsername();
        textViewUsername.setText(username);
    }

}
