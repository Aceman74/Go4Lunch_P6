package com.aceman.go4lunch.activities.profileActivity;

import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.widget.CheckBox;

import com.aceman.go4lunch.R;
import com.aceman.go4lunch.api.UserHelper;
import com.aceman.go4lunch.models.User;
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
 */
public class ProfilePresenter extends BasePresenter implements ProfileContract.ProfilePresenterInterface {


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

    @Nullable
    public FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    @Override
    public OnFailureListener onFailureListener() {
        return new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                ((ProfileContract.ProfileViewInterface) getView()).onUpdateFirebaseFailed();
            }
        };
    }

    @Override
    public void updateUsernameInFirebase(TextInputEditText textInputEditTextUsername) {

        ((ProfileContract.ProfileViewInterface) getView()).progressBarStart();
        String username = textInputEditTextUsername.getText().toString();
        String usernameBase = Objects.requireNonNull(getCurrentUser()).getDisplayName();

        if (this.getCurrentUser() != null) {
            ((ProfileContract.ProfileViewInterface) getView()).updateUserName(username, usernameBase);

        }
    }

    @Override
    public void updateEmailInFirebase(TextInputEditText textInputEditTextEmail) {

        ((ProfileContract.ProfileViewInterface) getView()).progressBarStart();
        String email = textInputEditTextEmail.getText().toString();
        String emailBase = getCurrentUser().getEmail();

        if (this.getCurrentUser() != null) {
            ((ProfileContract.ProfileViewInterface) getView()).updateEmail(email, emailBase);

        }
    }

    @Override
    public void updateUserIsPrivate(CheckBox checkBoxIsPrivate) {
        if (this.getCurrentUser() != null) {
            UserHelper.updateIsPrivate(this.getCurrentUser().getUid(), checkBoxIsPrivate.isChecked()).addOnFailureListener(this.onFailureListener());
        }
    }

}
