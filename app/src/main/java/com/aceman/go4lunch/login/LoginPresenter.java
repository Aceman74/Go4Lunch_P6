package com.aceman.go4lunch.login;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Button;
import android.widget.Toast;

import com.aceman.go4lunch.R;
import com.aceman.go4lunch.api.RestaurantPublicHelper;
import com.aceman.go4lunch.api.UserHelper;
import com.aceman.go4lunch.navigation.activities.CoreActivity;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.annotations.Nullable;

import java.util.Arrays;

import okhttp3.Cache;
import timber.log.Timber;

import static android.app.Activity.RESULT_OK;
import static com.aceman.go4lunch.navigation.fragments.MapsFragment.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;
import static com.aceman.go4lunch.navigation.fragments.MapsFragment.mMaps;
import static com.facebook.FacebookSdk.getApplicationContext;
import static com.facebook.FacebookSdk.getCacheDir;

/**
 * Created by Lionel JOFFRAY - on 28/05/2019.
 */
public class LoginPresenter implements LoginContract.LoginPresenterInterface {

    private LoginContract.LoginViewInterface mLoginViewInterface;

    public LoginPresenter(LoginContract.LoginViewInterface mLoginViewInterface){
        this.mLoginViewInterface = mLoginViewInterface;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data, int request, CoordinatorLayout layout, Context context) {
        this.handleResponseAfterSignIn(requestCode, resultCode, data,request,layout,context);
    }

    /**
     * Check if user Already Logged
     * @param activity
     */
    public void alreadyLogged(Activity activity) {
        if (this.isCurrentUserLogged())
            this.startCoreActivity(activity);
    }

    /**
     * Get Current User on Firebase
     * @return
     */
    @Nullable
    public FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    /**
     * Onclick the login button
     * @param request
     * @param activity
     */
    public void onClickLoginButton( int request, Activity activity) {
        //  Start appropriate activity
        if (isCurrentUserLogged()) {
            startCoreActivity(activity);
        } else {
            startSignInActivity(activity, request, activity);
        }
    }

    /**
     * Star Activity for Result if new user sign in
     * @param context
     * @param request
     * @param activity
     */
    public void startSignInActivity(Context context, int request, Activity activity) {

         activity.startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setTheme(R.style.LoginTheme)
                        .setAvailableProviders(
                                Arrays.asList(new AuthUI.IdpConfig.EmailBuilder().build(),
                                        new AuthUI.IdpConfig.GoogleBuilder().build(),
                                        new AuthUI.IdpConfig.FacebookBuilder().build()))
                        .setIsSmartLockEnabled(false, true)
                        .setLogo(R.drawable.logo)
                        .build(),
                request);   // Sign In
    }

    /**
     * Dialog who inform user that the app need his location and start locationPermission()
     * @param context
     * @param activity
     */
    public void askPermission( Context context, final Activity activity) {
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            new AlertDialog.Builder(context)
                    .setTitle(R.string.location_permission)
                    .setMessage(R.string.location_text)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            locationPermission(getApplicationContext(), activity);
                        }
                    })
                    .show();
        }
    }

    /**
     * Grant location permission to Go4Lunch
     * @param context
     * @param activity
     */
    private void locationPermission(Context context, Activity activity) {

        if (ContextCompat.checkSelfPermission(context,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMaps.setMyLocationEnabled(true);
        } else {
            ActivityCompat.requestPermissions(activity,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    /**
     * Cache setting
     */
    public void configureCache() {
        int cacheSize = 20 * 1024 * 1024; // 20 MB
       Cache mCache = new Cache(getCacheDir(), cacheSize);   //  For API requests
    }

    /**
     * Response for singing In new user (MVP issue)
     * @param requestCode
     * @param resultCode
     * @param data
     * @param request
     * @param layout
     * @param context
     */
    public void handleResponseAfterSignIn(int requestCode, int resultCode, Intent data, int request, CoordinatorLayout layout, Context context) {

        IdpResponse response = IdpResponse.fromResultIntent(data);

        if (requestCode == request) {   // Sign In
            if (resultCode == RESULT_OK) { // SUCCESS
                // 2 - CREATE USER IN FIRESTORE
                this.createUserInFirestore(context);
                showSnackBar(layout, context.getString(R.string.connection_succeed));
            } else { // ERRORS
                if (response == null) {
                    showSnackBar(layout, context.getString(R.string.error_authentication_canceled));
                } else if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                    showSnackBar(layout, context.getString(R.string.error_no_internet));
                } else if (response.getError().getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    showSnackBar(layout, context.getString(R.string.error_unknown_error));
                }
            }
        }
    }

    /**
     * Change btn text if logged or new
     * @param context
     * @param button
     */
    public void updateUIWhenResuming(Context context, Button button) {
        button.setText(this.isCurrentUserLogged() ? getApplicationContext().getString(R.string.button_login_text_logged) : getApplicationContext().getString(R.string.button_login_text_not_logged));
    }

    /**
     * Intent core activity ( maps)
     * @param context
     */
    public void startCoreActivity(Context context) {
        Intent intent = new Intent(getApplicationContext(), CoreActivity.class);
        getApplicationContext().startActivity(intent);
    }

    /**
     * Check if current user logged
     * @return
     */
    private Boolean isCurrentUserLogged() {
        return (this.getCurrentUser() != null);
    }

    /**
     * Create new user on firestore
     * @param context
     */
    private void createUserInFirestore(Context context) {

        if (this.getCurrentUser() != null) {

            String urlPicture = (this.getCurrentUser().getPhotoUrl() != null) ? this.getCurrentUser().getPhotoUrl().toString() : null;
            String username = this.getCurrentUser().getDisplayName();
            String uid = this.getCurrentUser().getUid();
            String email = this.getCurrentUser().getEmail();

            UserHelper.createUser(uid, username, urlPicture, email).addOnFailureListener(onFailureListener(context));
            RestaurantPublicHelper.createPublicUser(uid,username, urlPicture).addOnFailureListener(onFailureListener(context));
        }
    }

    /**
     * Show snackbar
     * @param coordinatorLayout
     * @param message
     */
    private void showSnackBar(CoordinatorLayout coordinatorLayout, String message) {
        Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_SHORT).show();
    }

    /**
     * Failure listener
     * @param context
     * @return
     */
    private OnFailureListener onFailureListener(final Context context) {
        return new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, context.getString(R.string.error_unknown_error), Toast.LENGTH_LONG).show();
                Timber.tag("Firebase ERROR").e(e);
            }
        };
    }
}
