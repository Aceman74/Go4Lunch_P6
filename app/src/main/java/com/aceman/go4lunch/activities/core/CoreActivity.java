package com.aceman.go4lunch.activities.core;

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
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

import com.aceman.go4lunch.R;
import com.aceman.go4lunch.activities.login.MainActivity;
import com.aceman.go4lunch.activities.placesDetail.PlacesDetailActivity;
import com.aceman.go4lunch.activities.profile.ProfileActivity;
import com.aceman.go4lunch.activities.settings.SettingsActivity;
import com.aceman.go4lunch.adapter.PageAdapter;
import com.aceman.go4lunch.data.models.RestaurantPublic;
import com.aceman.go4lunch.data.models.User;
import com.aceman.go4lunch.utils.AnimationClass;
import com.aceman.go4lunch.utils.base.BaseActivity;
import com.aceman.go4lunch.utils.events.SearchRefreshEvent;
import com.aceman.go4lunch.utils.events.UserListEvent;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
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
import timber.log.Timber;

/**
 * Created by Lionel JOFFRAY - on 03/05/2019.
 * <p>
 * Core Activity is the app base, after login.
 * This activity handle all 3 Fragments (Maps, ListView and Workmates).
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
    Animation mClickAnim;
    private String mIntent;
    private CorePresenter mPresenter;

    /**
     * When created, presenter is initialized, and a LocationServices (sFusedLocationProviderClient) is set for the Maps use.
     *
     * @param savedInstanceState savedInstanceState
     * @see com.aceman.go4lunch.fragments.maps.MapsFragment
     */
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

    /**
     * Method who launch the Profile Activity when the user Profile is clicked, on the navigation drawer header.
     */
    @Override
    public void onClickProfile() {
        mProfileImage = mNavigationView.getHeaderView(0).findViewById(R.id.profile_image_nav_header);
        mProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClickAnim = AnimationClass.animClick(getApplicationContext());
                mProfileImage.startAnimation(mClickAnim);
                mClickAnim.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        Intent profile = new Intent(getApplicationContext(), ProfileActivity.class);
                        profile.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        getApplicationContext().startActivity(profile);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

            }
        });
    }

    /**
     * Setting the toolbar.
     *
     * @see BaseActivity
     */
    @Override
    public void configureToolBar() {
        setSupportActionBar(mToolbar);
    }

    /**
     * Configure viewpager for the 3 fragments.
     *
     * @see com.aceman.go4lunch.fragments.listView.ListViewFragment
     * @see com.aceman.go4lunch.fragments.maps.MapsFragment
     * @see com.aceman.go4lunch.fragments.workmates.WorkmatesFragment
     */
    @Override
    public void configureViewPager() {
        pager.setAdapter(new PageAdapter(getSupportFragmentManager(), getApplicationContext()));
    }

    /**
     * Listener for settings/updating information in the navigation drawer.
     */
    @Override
    public void navigationDrawerListener() {
        NavigationView navigationView = findViewById(R.id.core_nav_view);
        View headerView = navigationView.getHeaderView(0);
        textViewUsername = headerView.findViewById(R.id.name_nav_header);
        textViewEmail = headerView.findViewById(R.id.email_nav_header);
        imageViewProfile = headerView.findViewById(R.id.profile_image_nav_header);
        mPresenter.updateUIWhenCreating();
    }

    /**
     * Get activity Layout
     *
     * @return activity layout
     * @see BaseActivity
     */
    @Override
    public int getActivityLayout() {
        return R.layout.activity_core;
    }

    /**
     * Sign out user from firebase method.
     */
    @Override
    public void signOutUserFromFirebase() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnSuccessListener(this, this.updateUIAfterRESTRequestsCompleted(SIGN_OUT_TASK));
        Intent start = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(start);
    }

    /**
     * Set the navigation drawer button on toolbar.
     */
    @Override
    public void configureHamburgerBtn() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.popup_message_choice_yes, R.string.popup_message_choice_no);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    /**
     * Configure the navigation drawer view and the bottom navigation view items listener.
     */
    @Override
    public void configureNavigationView() {
        mNavigationView.setNavigationItemSelectedListener(this);
        mBottomNavigationView.setOnNavigationItemSelectedListener(this);
    }

    /**
     * Set the page listener and set the title for each.
     */
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
                        mToolbar.setTitle(R.string.im_hungry);
                        break;
                    case 1:
                        mBottomNavigationView.setSelectedItemId(R.id.bottom_list_view);
                        mToolbar.setTitle(R.string.im_hungry);
                        break;
                    case 2:
                        mBottomNavigationView.setSelectedItemId(R.id.bottom_workmates);
                        mToolbar.setTitle(R.string.available_workm);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });
    }

    /**
     * Method who handle the click on items.
     *
     * @param item item clicked
     * @return always true
     */
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        //  Navigation Drawer item settings
        int id = item.getItemId();

        switch (id) {
            case R.id.drawer_your_lunch:
                Intent lunch = new Intent(this, PlacesDetailActivity.class);
                mIntent = getString(R.string.lunch);
                lunch.putExtra(getString(R.string.detail_intent), mIntent);
                this.startActivity(lunch);
                Timber.i("Click Your Lunch");
                break;
            case R.id.drawer_settings:
                Intent settings = new Intent(this, SettingsActivity.class);
                this.startActivity(settings);
                Timber.i("Click Settings");
                break;
            case R.id.drawer_logout:
                signOutUserFromFirebase();
                Timber.i("Sign Out");
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

    /**
     * Inflate the Google search menu.
     *
     * @param menu the menu
     * @return always true
     */
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

    /**
     * The Google Autocomplete intent method.
     */
    @Override
    public void autocompleteIntent() {
        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME);

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

    /**
     * On fail listener.
     *
     * @return fail message.
     * @see BaseActivity
     */
    @Override
    public OnFailureListener onFailureListener() {
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
    public FirebaseUser getCurrentUser() {
        return super.getCurrentUser();
    }

    /**
     * Check if the current user is logged.
     *
     * @return current user logged
     * @see BaseActivity
     */
    @Override
    public Boolean isCurrentUserLogged() {
        return super.isCurrentUserLogged();
    }

    /**
     * On success listener for sign out.
     *
     * @param origin the sign out task
     * @return new onSucces Listener
     */
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

    /**
     * Get the userlist From firestore for fragments use.
     *
     * @param task task
     */
    @Override
    public void setUserListFromFirebase(Task<QuerySnapshot> task) {
        for (QueryDocumentSnapshot document : task.getResult()) {
            RestaurantPublic userP = document.toObject(RestaurantPublic.class);
            mUserList.add(userP);
            Timber.tag("Task To List").i("Sucess");
        }
    }

    /**
     * Error message if list couldn't be downloaded.
     *
     * @param task task
     */
    @Override
    public void errorGettingUserListFromFirebase(Task<QuerySnapshot> task) {
        Timber.tag("Task Exeption").d(task.getException(), "Error getting documents: ");
    }

    /**
     * Post the UserList via EventBus Sticky event.
     */
    @Override
    public void postStickyEventBusUserList() {
        EventBus.getDefault().postSticky(new UserListEvent(mUserList));
    }

    /**
     * Load User Profile picture in navigation drawer.
     */
    @Override
    public void loadUserImgWithGlide() {
        Glide.with(this)
                .load(this.getCurrentUser().getPhotoUrl())
                .apply(RequestOptions.circleCropTransform())
                .into(imageViewProfile);
    }

    /**
     * Load User Mail in navigation drawer.
     */
    @Override
    public void loadUserEmail() {
        String email = TextUtils.isEmpty(this.getCurrentUser().getEmail()) ? getString(R.string.info_no_email_found) : this.getCurrentUser().getEmail();
        this.textViewEmail.setText(email);
    }

    /**
     * Load User Name in navigation drawer.
     */
    @Override
    public void loadUserUsername(User currentUser) {
        String username = TextUtils.isEmpty(currentUser.getUsername()) ? getString(R.string.info_no_username_found) : currentUser.getUsername();
        textViewUsername.setText(username);
    }

}
