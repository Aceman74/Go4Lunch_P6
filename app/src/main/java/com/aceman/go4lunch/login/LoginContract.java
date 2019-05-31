package com.aceman.go4lunch.login;

import android.support.design.widget.CoordinatorLayout;

import com.aceman.go4lunch.utils.BaseView;
import com.google.firebase.auth.FirebaseUser;

import okhttp3.Cache;

/**
 * Created by Lionel JOFFRAY - on 28/05/2019.
 */
public interface LoginContract {

    interface LoginPresenterInterface {

        void alreadyLogged();

        FirebaseUser getCurrentUser();

        Cache configureCache(Cache cache);
    }

    interface LoginViewInterface extends BaseView {

        void onUserCreationFailed();

        void onUserCreationSucceeded();

        void askPermission();

        void startCoreActivity();

        void updateUIWhenResuming();

        void showSnackBar(CoordinatorLayout coordinatorLayout, String message);

        void welcomeBackUser(String name);
    }
}
