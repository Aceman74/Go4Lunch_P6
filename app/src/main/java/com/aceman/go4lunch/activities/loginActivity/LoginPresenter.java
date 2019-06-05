package com.aceman.go4lunch.activities.loginActivity;

import android.support.annotation.NonNull;

import com.aceman.go4lunch.api.RestaurantPublicHelper;
import com.aceman.go4lunch.api.UserHelper;
import com.aceman.go4lunch.utils.BasePresenter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.annotations.Nullable;

import okhttp3.Cache;

import static com.facebook.FacebookSdk.getCacheDir;

/**
 * Created by Lionel JOFFRAY - on 28/05/2019.
 */
public class LoginPresenter extends BasePresenter implements LoginContract.LoginPresenterInterface {

    /**
     * Check if user Already Logged
     *
     */
    @Override
    public void alreadyLogged() {
        if (this.isCurrentUserLogged()){
            ((LoginContract.LoginViewInterface)getView()).startCoreActivity();
        String name = getCurrentUser().getDisplayName();
        ((LoginContract.LoginViewInterface)getView()).welcomeBackUser(name);
        }
    }

    /**
     * Get Current User on Firebase
     *
     * @return
     */
    @Nullable
    @Override
    public FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }


    /**
     * Cache setting
     */
    @Override
    public Cache configureCache(Cache mCache) {
        int cacheSize = 20 * 1024 * 1024; // 20 MB
      return mCache = new Cache(getCacheDir(), cacheSize);   //  For API requests
    }


    /**
     * Check if current user logged
     *
     * @return
     */
    @Override
    public Boolean isCurrentUserLogged() {
        return (this.getCurrentUser() != null);
    }

    /**
     * Create new user on firestore
     *
     */
    @Override
    public void createUserInFirestore() {

        if (this.getCurrentUser() != null) {

            String urlPicture = (this.getCurrentUser().getPhotoUrl() != null) ? this.getCurrentUser().getPhotoUrl().toString() : null;
            String username = this.getCurrentUser().getDisplayName();
            String uid = this.getCurrentUser().getUid();
            String email = this.getCurrentUser().getEmail();

            UserHelper.createUser(uid, username, urlPicture, email).addOnFailureListener(onFailureListener()).addOnSuccessListener(onSuccessListener());
            RestaurantPublicHelper.createPublicUser(uid, username, urlPicture).addOnFailureListener(onFailureListener());
        }
    }

    /**
     * Success listener
     * @return
     */
    @Override
    public OnSuccessListener onSuccessListener() {
        return new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                ((LoginContract.LoginViewInterface)getView()).onUserCreationSucceeded();
            }
        };
    }
    /**
     * Failure listener
     *
     * @return
     */
    @Override
    public OnFailureListener onFailureListener() {
        return new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                ((LoginContract.LoginViewInterface)getView()).onUserCreationFailed();
            }
        };
    }
}
