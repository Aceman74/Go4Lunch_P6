package com.aceman.go4lunch.activities.profile;

import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;

import com.aceman.go4lunch.api.UserHelper;
import com.aceman.go4lunch.data.models.User;
import com.aceman.go4lunch.utils.BasePresenter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Objects;

/**
 * Created by Lionel JOFFRAY - on 05/06/2019.
 * <p>
 * The presenter for Profile Activity.
 */
public class ProfilePresenter extends BasePresenter implements ProfileContract.ProfilePresenterInterface {

    /**
     * Update UI with Firebase data.
     */
    @Override
    public void updateUIWhenCreating() {
        if (this.getCurrentUser() != null) {
            ((ProfileContract.ProfileViewInterface) getView()).loadProfilePictureWithGlide();
            UserHelper.getUser(this.getCurrentUser().getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    User currentUser = documentSnapshot.toObject(User.class);
                    ((ProfileContract.ProfileViewInterface) getView()).onSuccessUpdateUI(currentUser);
                }
            });
        }
    }

    /**
     * Get current user on Firebase.
     *
     * @return user
     */
    @Nullable
    public FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    /**
     * On failure listener for Firebase.
     *
     * @return error msg
     */
    @Override
    public OnFailureListener onFailureListener() {
        return new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                ((ProfileContract.ProfileViewInterface) getView()).onUpdateFirebaseFailed();
            }
        };
    }

    /**
     * Update username on Firebase Database.
     *
     * @param textInputEditTextUsername new username
     */
    @Override
    public void updateUsernameInFirebase(TextInputEditText textInputEditTextUsername) {

        ((ProfileContract.ProfileViewInterface) getView()).progressBarStart();
        String username = textInputEditTextUsername.getText().toString();
        String usernameBase = Objects.requireNonNull(getCurrentUser()).getDisplayName();

        if (this.getCurrentUser() != null) {
            ((ProfileContract.ProfileViewInterface) getView()).updateUserName(username, usernameBase);
        }
        ((ProfileContract.ProfileViewInterface) getView()).progressBarStop();
    }

    /**
     * Update email on Firebase Database.
     *
     * @param textInputEditTextEmail new email
     */
    @Override
    public void updateEmailInFirebase(TextInputEditText textInputEditTextEmail) {

        ((ProfileContract.ProfileViewInterface) getView()).progressBarStart();
        String email = textInputEditTextEmail.getText().toString();
        String emailBase = getCurrentUser().getEmail();

        if (this.getCurrentUser() != null) {
            ((ProfileContract.ProfileViewInterface) getView()).updateEmail(email, emailBase);
        }
        ((ProfileContract.ProfileViewInterface) getView()).progressBarStop();
    }

}
