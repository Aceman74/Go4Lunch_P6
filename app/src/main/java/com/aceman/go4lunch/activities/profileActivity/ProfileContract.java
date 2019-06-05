package com.aceman.go4lunch.activities.profileActivity;

import android.support.design.widget.TextInputEditText;
import android.widget.CheckBox;

import com.aceman.go4lunch.models.User;
import com.aceman.go4lunch.utils.BaseView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by Lionel JOFFRAY - on 05/06/2019.
 */
public interface ProfileContract {

    interface ProfilePresenterInterface {

        FirebaseUser getCurrentUser();

        void updateUIWhenCreating();

        OnFailureListener onFailureListener();

       void updateUsernameInFirebase(TextInputEditText textInputEditTextUsername);

        void updateEmailInFirebase(TextInputEditText textInputEditTextEmail);

        void updateUserIsPrivate(CheckBox checkBoxIsPrivate);

    }

    interface ProfileViewInterface extends BaseView {
        void onClickUpdateButton();

        void onClickCheckBoxIsPrivate();

        void onClickSignOutButton();

        void onClickDeleteButton();

        void restartApp();

        void signOutUserFromFirebase();

        void deleteUserFromFirebase();

        OnSuccessListener<Void> updateUIAfterRESTRequestsCompleted(final int origin);

        void progressBarStart();

        void progressBarStop();

        void onUpdateFirebaseFailed();

        void loadProfilePictureWithGlide();

        void onSuccessUpdateUI(User currentUser);

        void updateUserName(String username, String usernameBase);

        void updateEmail(String email, String emailBase);
    }
}
