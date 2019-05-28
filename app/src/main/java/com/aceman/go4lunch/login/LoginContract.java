package com.aceman.go4lunch.login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.CoordinatorLayout;
import android.widget.Button;

import com.google.firebase.auth.FirebaseUser;

/**
 * Created by Lionel JOFFRAY - on 28/05/2019.
 */
public interface LoginContract {

    interface LoginPresenterInterface {

        void alreadyLogged(Activity activity);

        FirebaseUser getCurrentUser();

        void onClickLoginButton(int request, Activity activity);

        void startSignInActivity(Context context, int request, Activity activity);

        void askPermission(final Context context, final Activity activity);

        void configureCache();

        void updateUIWhenResuming(Context context, Button button);

        void onActivityResult(int requestCode, int resultCode, Intent data, int request, CoordinatorLayout layout, Context context);

        void handleResponseAfterSignIn(int requestCode, int resultCode, Intent data, int request, CoordinatorLayout layout, Context context);

        void startCoreActivity(Context context);
    }

    interface LoginViewInterface {

    }
}
