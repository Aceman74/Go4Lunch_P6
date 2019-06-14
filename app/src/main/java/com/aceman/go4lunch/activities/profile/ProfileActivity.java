package com.aceman.go4lunch.activities.profile;

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
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.aceman.go4lunch.R;
import com.aceman.go4lunch.activities.login.MainActivity;
import com.aceman.go4lunch.api.RestaurantPublicHelper;
import com.aceman.go4lunch.api.UserHelper;
import com.aceman.go4lunch.models.User;
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
 */
public class ProfileActivity extends BaseActivity implements ProfileContract.ProfileViewInterface {
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
    ProfilePresenter mPresenter;
    Animation mClickAnim;
    @BindView(R.id.profile_activity_button_update)
    Button mUpdateBtn;
    @BindView(R.id.profile_activity_button_sign_out)
    Button mLogOut;
    @BindView(R.id.profile_activity_button_delete)
    Button mDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new ProfilePresenter();
        mPresenter.attachView(this);
        configureToolbar();
        mPresenter.updateUIWhenCreating();
    }

    @Override
    public int getFragmentLayout() {
        return R.layout.activity_profile;
    }

    @Override
    @OnClick(R.id.profile_activity_button_update)
    public void onClickUpdateButton() {

        mUpdateBtn.startAnimation(AnimationClass.animClick(getApplicationContext()));
        mPresenter.updateUsernameInFirebase(textInputEditTextUsername);
        mPresenter.updateEmailInFirebase(textInputEditTextEmail);
    }

    @Override
    @OnClick(R.id.profile_activity_check_box_is_private)
    public void onClickCheckBoxIsPrivate() {
        mPresenter.updateUserIsPrivate(checkBoxIsPrivate);
    }

    @Override
    @OnClick(R.id.profile_activity_button_sign_out)
    public void onClickSignOutButton() {
        mClickAnim = AnimationClass.animClick(getApplicationContext());
        mLogOut.startAnimation(mClickAnim);
        mClickAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                Toast.makeText(getApplicationContext(), "See you soon !", Toast.LENGTH_SHORT).show();

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

    @Override
    public void restartApp() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        int mPendingIntentId = PENDING_ID;
        PendingIntent mPendingIntent = PendingIntent.getActivity(getApplicationContext(), mPendingIntentId, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager mgr = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 300, mPendingIntent);
        System.exit(0);
    }

    @Override
    public void signOutUserFromFirebase() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnSuccessListener(this, this.updateUIAfterRESTRequestsCompleted(SIGN_OUT_TASK));
        Intent start = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(start);
    }

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

    @Override
    public void progressBarStart() {
        this.progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void progressBarStop() {
        progressBar.setVisibility(View.INVISIBLE);
    }

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

    @Override
    public void onUpdateFirebaseFailed() {
        Toast.makeText(this, "An error occured.", Toast.LENGTH_LONG).show();
    }

    @Override
    public void loadProfilePictureWithGlide() {
        if (this.getCurrentUser().getPhotoUrl() != null) {
            Glide.with(this)
                    .load(this.getCurrentUser().getPhotoUrl())
                    .apply(RequestOptions.circleCropTransform())
                    .into(imageViewProfile);
        }
    }

    @Override
    public void onSuccessUpdateUI(User currentUser) {
        String username = TextUtils.isEmpty(currentUser.getUsername()) ? getString(R.string.info_no_username_found) : currentUser.getUsername();
        String email = TextUtils.isEmpty(currentUser.getEmail()) ? getString(R.string.info_no_email_found) : currentUser.getEmail();
        textInputEditTextEmail.setText(email);
        checkBoxIsPrivate.setChecked(currentUser.getIsPrivate());
        textInputEditTextUsername.setText(username);
    }

    @Override
    public void updateUserName(String username, String usernameBase) {
        if (!username.isEmpty() && !username.equals(getString(R.string.info_no_username_found)) && !username.equals(usernameBase)) {
            RestaurantPublicHelper.updateUsername(username, this.getCurrentUser().getUid()).addOnFailureListener(this.onFailureListener()).addOnSuccessListener(this.updateUIAfterRESTRequestsCompleted(UPDATE_USERNAME_PUBLIC));
            UserHelper.updateUsername(username, this.getCurrentUser().getUid()).addOnFailureListener(this.onFailureListener()).addOnSuccessListener(this.updateUIAfterRESTRequestsCompleted(UPDATE_USERNAME));
            Toast.makeText(getApplicationContext(), "Username updated.", Toast.LENGTH_SHORT).show();
        }
        if (username.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Username can't be empty.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void updateEmail(String email, String emailBase) {
        if (!email.isEmpty() && !email.equals(getString(R.string.info_no_email_found)) && !email.equals(emailBase)) {
            UserHelper.updateEmail(email, this.getCurrentUser().getUid()).addOnFailureListener(this.onFailureListener()).addOnSuccessListener(this.updateUIAfterRESTRequestsCompleted(UPDATE_USERNAME));
            Toast.makeText(getApplicationContext(), "Email updated.", Toast.LENGTH_SHORT).show();
        }
        if (email.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Email can't be empty.", Toast.LENGTH_SHORT).show();
        }
    }
}
