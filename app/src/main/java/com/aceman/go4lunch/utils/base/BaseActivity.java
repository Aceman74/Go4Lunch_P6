package com.aceman.go4lunch.utils.base;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.aceman.go4lunch.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * Created by Lionel JOFFRAY - on 02/05/2019.
 * <p>
 * Base Class for all Activity.
 *
 * @see ButterKnife
 */
public abstract class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(this.getActivityLayout());
        ButterKnife.bind(this); //  Configure Butterknife
    }

    /**
     * Get Layout.
     *
     * @return layout
     */
    public abstract int getActivityLayout();

    protected void configureToolbar() {
        ActionBar ab = getSupportActionBar();
        assert ab != null;
        ab.setDisplayHomeAsUpEnabled(true);
    }

    /**
     * On Failure listener for Firebase REST.
     *
     * @return failure
     */
    protected OnFailureListener onFailureListener() {
        return new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), getString(R.string.error_unknown_error), Toast.LENGTH_LONG).show();
                Timber.tag("Firebase ERROR").e(e);
            }
        };
    }

    /**
     * Get current user on Firebase Auth.
     *
     * @return current user
     */
    @Nullable
    protected FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    /**
     * Check if user is logged.
     *
     * @return log state
     */
    protected Boolean isCurrentUserLogged() {
        return (this.getCurrentUser() != null);
    }
}


