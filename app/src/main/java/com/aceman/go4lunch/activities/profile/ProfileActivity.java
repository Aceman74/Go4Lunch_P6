package com.aceman.go4lunch.activities.profile;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.aceman.go4lunch.R;
import com.aceman.go4lunch.activities.login.MainActivity;
import com.aceman.go4lunch.api.RestaurantPublicHelper;
import com.aceman.go4lunch.api.UserHelper;
import com.aceman.go4lunch.data.models.User;
import com.aceman.go4lunch.utils.AnimationClass;
import com.aceman.go4lunch.utils.base.BaseActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnSuccessListener;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Lionel JOFFRAY - on 02/05/2019.
 * <p>
 * Profile Activity is for all account related update/delete for users.
 */
public class ProfileActivity extends BaseActivity implements ProfileContract.ProfileViewInterface {

    private static final int SIGN_OUT_TASK = 10;
    private static final int DELETE_USER_TASK = 20;
    private static final int UPDATE_USERNAME = 30;
    private static final int UPDATE_USERNAME_PUBLIC = 40;

    @BindView(R.id.profile_activity_imageview_profile)
    ImageView imageViewProfile;
    @BindView(R.id.profile_activity_edit_text_username)
    TextInputEditText textInputEditTextUsername;
    @BindView(R.id.profile_activity_edit_text_email)
    TextInputEditText textInputEditTextEmail;
    @BindView(R.id.profile_activity_progress_bar)
    ProgressBar progressBar;
    ProfilePresenter mPresenter;
    Animation mClickAnim;
    @BindView(R.id.profile_activity_button_update)
    Button mUpdateBtn;
    @BindView(R.id.profile_activity_button_sign_out)
    Button mLogOut;
    @BindView(R.id.profile_activity_button_delete)
    Button mDelete;

    /**
     * When created, the presenter is initialized, toolbar is set and UI is updating.
     *
     * @param savedInstanceState savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new ProfilePresenter();
        mPresenter.attachView(this);
        configureToolbar();
        mPresenter.updateUIWhenCreating();
    }

    /**
     * Get Activity Layout.
     *
     * @return layout
     */
    @Override
    public int getActivityLayout() {
        return R.layout.activity_profile;
    }

    /**
     * On click on update button listener. Send data to firebase when user edit his profile.
     */
    @Override
    @OnClick(R.id.profile_activity_button_update)
    public void onClickUpdateButton() {

        mUpdateBtn.startAnimation(AnimationClass.animClick(getApplicationContext()));
        mPresenter.updateUsernameInFirebase(textInputEditTextUsername);
        mPresenter.updateEmailInFirebase(textInputEditTextEmail);
    }

    /**
     * On click Log Out bouton listener. Restart the app.
     */
    @Override
    @OnClick(R.id.profile_activity_button_sign_out)
    public void onClickSignOutButton() {
        mClickAnim = AnimationClass.animClick(getApplicationContext());
        mLogOut.startAnimation(mClickAnim);
        mClickAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                Toast.makeText(getApplicationContext(), getString(R.string.see_you), Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                signOutUserFromFirebase();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    /**
     * On click delete account. User is double checked with a dialog, to avoid unfortunate click.
     */
    @Override
    @OnClick(R.id.profile_activity_button_delete)
    public void onClickDeleteButton() {

        mClickAnim = AnimationClass.animClick(getApplicationContext());
        mDelete.startAnimation(mClickAnim);
        mClickAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                new AlertDialog.Builder(ProfileActivity.this)
                        .setMessage(R.string.popup_message_confirmation_delete_account)
                        .setPositiveButton(R.string.popup_message_choice_yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                deleteUserFromFirebase();
                                Toast.makeText(getApplicationContext(), "Account Deleted !", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton(R.string.popup_message_choice_no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .show();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    /**
     * Signing out user from firebase, restart activity.
     */
    @Override
    public void signOutUserFromFirebase() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnSuccessListener(this, this.updateUIAfterRESTRequestsCompleted(SIGN_OUT_TASK));
        Intent start = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(start);
    }

    /**
     * Deleting user from firebase, restart activity.
     */
    @Override
    public void deleteUserFromFirebase() {
        if (this.getCurrentUser() != null) {
            UserHelper.deleteUser(this.getCurrentUser().getUid()).addOnFailureListener(this.onFailureListener());
            RestaurantPublicHelper.deleteUser(this.getCurrentUser().getUid()).addOnFailureListener(this.onFailureListener());
            AuthUI.getInstance()
                    .delete(this)
                    .addOnSuccessListener(this, this.updateUIAfterRESTRequestsCompleted(DELETE_USER_TASK));
        }
        Intent start = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(start);
    }

    /**
     * Show a progress bar.
     */
    @Override
    public void progressBarStart() {
        this.progressBar.setVisibility(View.VISIBLE);
    }

    /**
     * Hide the progress bar.
     */
    @Override
    public void progressBarStop() {
        progressBar.setVisibility(View.GONE);
    }

    /**
     * Update UI once sign out or delete user REST is done.
     *
     * @param origin origin
     * @return success listener
     */
    @Override
    public OnSuccessListener<Void> updateUIAfterRESTRequestsCompleted(final int origin) {
        return new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                switch (origin) {
                    // 8 - Hiding Progress bar after request completed
                    case UPDATE_USERNAME:
                        progressBarStop();
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

    /**
     * Message on fail update.
     */
    @Override
    public void onUpdateFirebaseFailed() {
        Toast.makeText(this, R.string.error_unknown_error, Toast.LENGTH_LONG).show();
    }

    /**
     * Load the profile pic with Glide.
     *
     * @see Glide
     */
    @Override
    public void loadProfilePictureWithGlide() {
        if (this.getCurrentUser().getPhotoUrl() != null) {
            Glide.with(this)
                    .load(this.getCurrentUser().getPhotoUrl())
                    .apply(RequestOptions.circleCropTransform())
                    .into(imageViewProfile);
        }
    }

    /**
     * On success update UI.
     *
     * @param currentUser current user
     */
    @Override
    public void onSuccessUpdateUI(User currentUser) {
        String username = TextUtils.isEmpty(currentUser.getUsername()) ? getString(R.string.info_no_username_found) : currentUser.getUsername();
        String email = TextUtils.isEmpty(currentUser.getEmail()) ? getString(R.string.info_no_email_found) : currentUser.getEmail();
        textInputEditTextEmail.setText(email);
        textInputEditTextUsername.setText(username);
    }

    /**
     * On update username, with empty check.
     *
     * @param username     new username
     * @param usernameBase old username
     */
    @Override
    public void updateUserName(String username, String usernameBase) {
        if (!username.isEmpty() && !username.equals(getString(R.string.info_no_username_found)) && !username.equals(usernameBase)) {
            RestaurantPublicHelper.updateUsername(username, this.getCurrentUser().getUid()).addOnFailureListener(this.onFailureListener()).addOnSuccessListener(this.updateUIAfterRESTRequestsCompleted(UPDATE_USERNAME_PUBLIC));
            UserHelper.updateUsername(username, this.getCurrentUser().getUid()).addOnFailureListener(this.onFailureListener()).addOnSuccessListener(this.updateUIAfterRESTRequestsCompleted(UPDATE_USERNAME));
            Toast.makeText(getApplicationContext(), getString(R.string.username_update), Toast.LENGTH_SHORT).show();
        }
        if (username.isEmpty()) {
            Toast.makeText(getApplicationContext(), getString(R.string.username_empty), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * On update Email, with empty check.
     *
     * @param email     new email
     * @param emailBase old email
     */
    @Override
    public void updateEmail(String email, String emailBase) {
        if (!email.isEmpty() && !email.equals(getString(R.string.info_no_email_found)) && !email.equals(emailBase)) {
            UserHelper.updateEmail(email, this.getCurrentUser().getUid()).addOnFailureListener(this.onFailureListener()).addOnSuccessListener(this.updateUIAfterRESTRequestsCompleted(UPDATE_USERNAME));
            Toast.makeText(getApplicationContext(), getString(R.string.email_update), Toast.LENGTH_SHORT).show();
        }
        if (email.isEmpty()) {
            Toast.makeText(getApplicationContext(), getString(R.string.email_empty), Toast.LENGTH_SHORT).show();
        }
    }
}
