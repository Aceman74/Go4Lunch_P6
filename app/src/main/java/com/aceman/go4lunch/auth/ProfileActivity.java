package com.aceman.go4lunch.auth;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.aceman.go4lunch.R;
import com.aceman.go4lunch.api.RestaurantPublicHelper;
import com.aceman.go4lunch.api.UserHelper;
import com.aceman.go4lunch.base.BaseActivity;
import com.aceman.go4lunch.models.User;
import com.aceman.go4lunch.login.MainActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Objects;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Lionel JOFFRAY - on 02/05/2019.
 */
public class ProfileActivity extends BaseActivity {
    //FOR DATA
    // 2 - Identify each Http Request
    private static final int SIGN_OUT_TASK = 10;
    private static final int DELETE_USER_TASK = 20;
    private static final int UPDATE_USERNAME = 30;
    private static final int UPDATE_USERNAME_PUBLIC = 40;
    private static final int PENDING_ID = 69;

    //FOR DESIGN
    @BindView(R.id.profile_activity_imageview_profile)
    ImageView imageViewProfile;
    @BindView(R.id.profile_activity_edit_text_username)
    TextInputEditText textInputEditTextUsername;
    @BindView(R.id.profile_activity_edit_text_email)
    TextInputEditText textInputEditTextEmail;
    @BindView(R.id.profile_activity_progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.profile_activity_check_box_is_private)
    CheckBox checkBoxIsPrivate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.configureToolbar();
        this.updateUIWhenCreating(); // 2 - Update UI
    }

    @Override
    public int getFragmentLayout() {
        return R.layout.activity_profile;
    }

    // --------------------
    // ACTIONS
    // --------------------

    // 5 - Update onClick Listeners

    @OnClick(R.id.profile_activity_button_update)
    public void onClickUpdateButton() {
        this.updateUsernameInFirebase();
        this.updateEmailInFirebase();
    }

    @OnClick(R.id.profile_activity_check_box_is_private)
    public void onClickCheckBoxIsPrivate() {
        this.updateUserIsPrivate();
    }

    // 4 - Adding requests to button listeners

    @OnClick(R.id.profile_activity_button_sign_out)
    public void onClickSignOutButton() {
        this.signOutUserFromFirebase();
        restartApp();
    }

    @OnClick(R.id.profile_activity_button_delete)
    public void onClickDeleteButton() {
        new AlertDialog.Builder(this)
                .setMessage(R.string.popup_message_confirmation_delete_account)
                .setPositiveButton(R.string.popup_message_choice_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteUserFromFirebase();
                    }
                })
                .setNegativeButton(R.string.popup_message_choice_no, null)
                .show();
        restartApp();
    }

    private void restartApp() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        int mPendingIntentId = PENDING_ID;
        PendingIntent mPendingIntent = PendingIntent.getActivity(getApplicationContext(), mPendingIntentId, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager mgr = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
        System.exit(0);
    }
    // --------------------
    // REST REQUESTS
    // --------------------
    // 1 - Create http requests (SignOut & Delete)

    private void signOutUserFromFirebase() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnSuccessListener(this, this.updateUIAfterRESTRequestsCompleted(SIGN_OUT_TASK));
    }

    private void deleteUserFromFirebase() {
        if (this.getCurrentUser() != null) {
            //4 - We also delete user from firestore storage
            UserHelper.deleteUser(this.getCurrentUser().getUid()).addOnFailureListener(this.onFailureListener());
            RestaurantPublicHelper.deleteUser(this.getCurrentUser().getUid()).addOnFailureListener(this.onFailureListener());
            AuthUI.getInstance()
                    .delete(this)
                    .addOnSuccessListener(this, this.updateUIAfterRESTRequestsCompleted(DELETE_USER_TASK));
        }
        Intent start = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(start);
    }

    // --------------------
    // UI
    // --------------------

    // 3 - Update User Username
    private void updateUsernameInFirebase() {

        this.progressBar.setVisibility(View.VISIBLE);
        String username = this.textInputEditTextUsername.getText().toString();
        String usernameBase = Objects.requireNonNull(getCurrentUser()).getDisplayName();

        if (this.getCurrentUser() != null) {
            if (!username.isEmpty() && !username.equals(getString(R.string.info_no_username_found)) && !username.equals(usernameBase)) {
                RestaurantPublicHelper.updateUsername(username,this.getCurrentUser().getUid()).addOnFailureListener(this.onFailureListener()).addOnSuccessListener(this.updateUIAfterRESTRequestsCompleted(UPDATE_USERNAME_PUBLIC));
                UserHelper.updateUsername(username, this.getCurrentUser().getUid()).addOnFailureListener(this.onFailureListener()).addOnSuccessListener(this.updateUIAfterRESTRequestsCompleted(UPDATE_USERNAME));
            }
        }
    }
    private void updateEmailInFirebase() {

        this.progressBar.setVisibility(View.VISIBLE);
        String email = this.textInputEditTextEmail.getText().toString();
        String emailBase = getCurrentUser().getEmail();

        if (this.getCurrentUser() != null) {
            if (!email.isEmpty() && !email.equals(getString(R.string.info_no_email_found)) && !email.equals(emailBase)) {
                UserHelper.updateEmail(email, this.getCurrentUser().getUid()).addOnFailureListener(this.onFailureListener()).addOnSuccessListener(this.updateUIAfterRESTRequestsCompleted(UPDATE_USERNAME));
            }
        }
    }
    // 2 - Update User Private (is or not)
    private void updateUserIsPrivate() {
        if (this.getCurrentUser() != null) {
            UserHelper.updateIsPrivate(this.getCurrentUser().getUid(), this.checkBoxIsPrivate.isChecked()).addOnFailureListener(this.onFailureListener());
        }
    }

    // 6 - Arranging method that updating UI with Firestore data
    private void updateUIWhenCreating() {

        if (this.getCurrentUser() != null) {

            if (this.getCurrentUser().getPhotoUrl() != null) {
                Glide.with(this)
                        .load(this.getCurrentUser().getPhotoUrl())
                        .apply(RequestOptions.circleCropTransform())
                        .into(imageViewProfile);
            }



            // 7 - Get additional data from Firestore (isPrivate & Username)
            UserHelper.getUser(this.getCurrentUser().getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    User currentUser = documentSnapshot.toObject(User.class);
                    String username = TextUtils.isEmpty(currentUser.getUsername()) ? getString(R.string.info_no_username_found) : currentUser.getUsername();
                    String email = TextUtils.isEmpty(currentUser.getEmail()) ? getString(R.string.info_no_email_found) : currentUser.getEmail();

                    textInputEditTextEmail.setText(email);
                    checkBoxIsPrivate.setChecked(currentUser.getIsPrivate());
                    textInputEditTextUsername.setText(username);
                }
            });
        }
    }

    private OnSuccessListener<Void> updateUIAfterRESTRequestsCompleted(final int origin) {
        return new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                switch (origin) {
                    // 8 - Hiding Progress bar after request completed
                    case UPDATE_USERNAME:
                        progressBar.setVisibility(View.INVISIBLE);
                        break;
                    case SIGN_OUT_TASK:
                        finish();
                        break;
                    case DELETE_USER_TASK:
                        finish();
                        break;
                    default:
                        break;
                }
            }
        };
    }
}
