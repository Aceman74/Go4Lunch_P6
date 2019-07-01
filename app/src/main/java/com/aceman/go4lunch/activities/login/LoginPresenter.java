package com.aceman.go4lunch.activities.login;

import android.support.annotation.NonNull;

import com.aceman.go4lunch.api.RestaurantPublicHelper;
import com.aceman.go4lunch.api.UserHelper;
import com.aceman.go4lunch.data.models.User;
import com.aceman.go4lunch.utils.BasePresenter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.DocumentSnapshot;

import okhttp3.Cache;

import static com.facebook.FacebookSdk.getCacheDir;

/**
 * Created by Lionel JOFFRAY - on 28/05/2019.
 * <p>
 * The presenter for Login Activity.
 */
public class LoginPresenter extends BasePresenter implements LoginContract.LoginPresenterInterface {

    /**
     * Check if user Already Logged
     */
    @Override
    public void alreadyLogged() {
        if (this.isCurrentUserLogged()) {
            ((LoginContract.LoginViewInterface) getView()).startCoreActivity();
            String name = getCurrentUser().getDisplayName();
            ((LoginContract.LoginViewInterface) getView()).welcomeUser(name);
        }
    }

    /**
     * Get Current User on Firebase
     *
     * @return user
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
     * @return user
     */
    @Override
    public Boolean isCurrentUserLogged() {
        return (this.getCurrentUser() != null);
    }

    /**
     * Create new user on firestore, check if already existing to avoid Erasing data.
     */
    @Override
    public void createUserInFirestore() {

        if (this.getCurrentUser() != null) {

            final String urlPicture = (this.getCurrentUser().getPhotoUrl() != null) ? this.getCurrentUser().getPhotoUrl().toString() : null;
            final String username = this.getCurrentUser().getDisplayName();
            final String uid = this.getCurrentUser().getUid();
            final String email = this.getCurrentUser().getEmail();

            UserHelper.getUser(this.getCurrentUser().getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    User currentUser = documentSnapshot.toObject(User.class);
                    if (currentUser == null) {
                        UserHelper.createUser(uid, username, urlPicture, email).addOnFailureListener(onFailureListener()).addOnSuccessListener(onSuccessListener());
                        RestaurantPublicHelper.createPublicUser(uid, username, urlPicture).addOnFailureListener(onFailureListener());
                    }
                }
            });
        }
    }

    /**
     * Success listener
     *
     * @return listener
     */
    @Override
    public OnSuccessListener onSuccessListener() {
        return new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                ((LoginContract.LoginViewInterface) getView()).onUserCreationSucceeded();
            }
        };
    }

    /**
     * Failure listener
     *
     * @return listener
     */
    @Override
    public OnFailureListener onFailureListener() {
        return new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                ((LoginContract.LoginViewInterface) getView()).onUserCreationFailed();
            }
        };
    }
}
