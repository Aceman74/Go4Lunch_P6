package com.aceman.go4lunch.activities.login;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.aceman.go4lunch.R;
import com.aceman.go4lunch.activities.core.CoreActivity;
import com.aceman.go4lunch.utils.AnimationClass;
import com.aceman.go4lunch.utils.base.BaseActivity;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.listener.multi.DialogOnAnyDeniedMultiplePermissionsListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.Arrays;

import butterknife.BindView;
import okhttp3.Cache;
import timber.log.Timber;

/**
 * Created by Lionel JOFFRAY - on 02/05/2019.
 * <p>
 * This is the main activity, used for creating account, logging and permission check.
 * All users have to be logged to access the application.
 */
public class MainActivity extends BaseActivity implements LoginContract.LoginViewInterface {

    private static final int RC_SIGN_IN = 111;
    public static Cache mCache;
    @BindView(R.id.main_activity_coordinator_layout)
    CoordinatorLayout coordinatorLayout;
    @BindView(R.id.main_activity_button_login)
    Button mBtnLogin;
    @BindView(R.id.main_background)
    ImageView mBackground;
    Animation mClickAnim;
    AlertDialog.Builder alertDialog;
    private LoginPresenter mPresenter;

    /**
     * When created, the presenter is initialized, permissions asked, and listener for button set.
     *
     * @param savedInstanceState savedInstanceState
     */
    @Override
    protected void onCreate(@android.support.annotation.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new LoginPresenter();
        mPresenter.attachView(this);
        checkPermission();
        mCache = mPresenter.configureCache(mCache);
        mPresenter.isCurrentUserLogged();
        updateUIWhenResuming();

        mBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getApplicationContext(),
                        android.Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    alertDialog = null;
                    askPermission();
                } else {
                    mClickAnim = AnimationClass.animClickFadeOut(getApplicationContext());
                    mBtnLogin.startAnimation(mClickAnim);
                    mClickAnim.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            mBtnLogin.setVisibility(View.INVISIBLE);
                            if (isCurrentUserLogged()) {
                                mPresenter.alreadyLogged();
                            } else {
                                startSignInActivity();
                            }
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {
                        }
                    });
                }
            }
        });
        updateUIWhenResuming();
    }

    /**
     * Get Activity Layout.
     *
     * @return layout
     */
    @Override
    public int getActivityLayout() {
        return R.layout.activity_main;
    }

    /**
     * Inform user when creation failed.
     */
    @Override
    public void onUserCreationFailed() {
        Toast.makeText(this, getString(R.string.error_unknown_error), Toast.LENGTH_LONG).show();
        Timber.tag("Firebase UserCreation").e("Error creation user");
    }

    /**
     * Inform user when creation succeeded.
     */
    @Override
    public void onUserCreationSucceeded() {
        Toast.makeText(this, getString(R.string.new_user_created), Toast.LENGTH_LONG).show();
        Timber.tag("Firebase UserCreation").e("New User created");
    }

    /**
     * Check if user already grant permission/
     */
    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            askPermission();
        }
    }

    /**
     * Ask user for location and call permission.
     *
     * @see Dexter
     */
    @Override
    public void askPermission() {
        if (alertDialog == null) {
            alertDialog = new AlertDialog.Builder(MainActivity.this);
            alertDialog.setTitle(R.string.application_permission);
            alertDialog.setMessage(R.string.permision_text);
            alertDialog.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    dexterInit();
                }
            });
            alertDialog.show();

        }
    }

    /**
     * Intent for starting Core Activity
     *
     * @see CoreActivity
     */
    @Override
    public void startCoreActivity() {
        Intent intent = new Intent(this, CoreActivity.class);
        this.startActivity(intent);
    }

    /**
     * Update btn text, if already logged or new.
     */
    @Override
    public void updateUIWhenResuming() {
        mBtnLogin.setText(isCurrentUserLogged() ? getApplicationContext().getString(R.string.button_login_text_logged) : getApplicationContext().getString(R.string.button_login_text_not_logged));
    }

    /**
     * Show a snackbar when needed.
     *
     * @param coordinatorLayout coordinatorLayout
     * @param message           message
     */
    @Override
    public void showSnackBar(CoordinatorLayout coordinatorLayout, String message) {
        Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_SHORT).show();
    }

    /**
     * A little hello from the app.
     *
     * @param name username
     */
    @Override
    public void welcomeUser(String name) {
        Toast.makeText(this, getString(R.string.user_already_logged) + " " + name, Toast.LENGTH_LONG).show();

    }

    /**
     * On Resume override for updating view.
     */
    @Override
    protected void onResume() {
        super.onResume();
        updateUIWhenResuming();
        checkPermission();
        mBtnLogin.setVisibility(View.VISIBLE);
    }

    /**
     * On activity result for new Account Creation.
     *
     * @param requestCode requestCode
     * @param resultCode  resultCode
     * @param data        data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        this.handleResponseAfterSignIn(requestCode, resultCode, data);
    }

    /**
     * Launch the sign in Activity for result.
     */
    public void startSignInActivity() {

        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setTheme(R.style.LoginTheme)
                        .setAvailableProviders(
                                Arrays.asList(new AuthUI.IdpConfig.EmailBuilder().build(),
                                        new AuthUI.IdpConfig.GoogleBuilder().build(),
                                        new AuthUI.IdpConfig.FacebookBuilder().build(),
                                        new AuthUI.IdpConfig.TwitterBuilder().build()))
                        .setIsSmartLockEnabled(false, true)
                        .setLogo(R.drawable.logo)
                        .build(),
                RC_SIGN_IN);   // Sign In
    }

    /**
     * Dexter library used for permissions.
     */
    private void dexterInit() {
        MultiplePermissionsListener dialogMultiplePermissionsListener =
                DialogOnAnyDeniedMultiplePermissionsListener.Builder
                        .withContext(this)
                        .withTitle(R.string.perm_required)
                        .withMessage(R.string.perm_required_1)
                        .withButtonText(android.R.string.ok)
                        .build();

        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.INTERNET,
                        Manifest.permission.ACCESS_NETWORK_STATE,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.CALL_PHONE
                ).withListener(dialogMultiplePermissionsListener)
                .check();
    }

    /**
     * Handle the response after Signed in.
     *
     * @param requestCode requestCode
     * @param resultCode  resultCode
     * @param data        data
     */
    private void handleResponseAfterSignIn(int requestCode, int resultCode, Intent data) {

        IdpResponse response = IdpResponse.fromResultIntent(data);

        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) { // SUCCESS
                mPresenter.createUserInFirestore();
                showSnackBar(coordinatorLayout, getString(R.string.connection_succeed));
            } else { // ERRORS
                if (response == null) {
                    showSnackBar(this.coordinatorLayout, getString(R.string.error_authentication_canceled));
                } else if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                    showSnackBar(coordinatorLayout, getString(R.string.error_no_internet));
                } else if (response.getError().getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    showSnackBar(coordinatorLayout, getString(R.string.error_unknown_error));
                }
            }
        }
    }
}


