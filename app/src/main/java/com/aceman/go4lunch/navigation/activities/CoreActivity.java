package com.aceman.go4lunch.navigation.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.TextView;

import com.aceman.go4lunch.R;
import com.aceman.go4lunch.api.UserHelper;
import com.aceman.go4lunch.auth.ProfileActivity;
import com.aceman.go4lunch.base.BaseActivity;
import com.aceman.go4lunch.models.User;
import com.aceman.go4lunch.navigation.adapter.PageAdapter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;

import butterknife.BindView;
import timber.log.Timber;

/**
 * Created by Lionel JOFFRAY - on 03/05/2019.
 */
public class CoreActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener, BottomNavigationView.OnNavigationItemSelectedListener {
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
    private static final int SIGN_OUT_TASK = 10;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        navigationDrawerListener();
        configureToolBar();
        configureNavigationView();
        configureViewPager();
        pagerListener();
        configureHamburgerBtn();
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

    private void updateUIWhenCreating(){

        if (this.getCurrentUser() != null){

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

    void pagerListener(){
pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
    @Override
    public void onPageScrolled(int i, float v, int i1) {

    }

    @Override
    public void onPageSelected(int i) {
switch (i){
    case 0: mBottomNavigationView.setSelectedItemId(R.id.bottom_maps);
        break;
    case 1: mBottomNavigationView.setSelectedItemId(R.id.bottom_list_view);
        break;
    case 2:mBottomNavigationView.setSelectedItemId(R.id.bottom_workmates);
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
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);

        return true;
    }
    private OnSuccessListener<Void> updateUIAfterRESTRequestsCompleted(final int origin){
        return new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                if (origin == SIGN_OUT_TASK) {
                    finish();
                }
            }
        };
    }
}
