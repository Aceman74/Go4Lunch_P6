package com.aceman.go4lunch.activities.settings;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.work.WorkManager;

import com.aceman.go4lunch.R;
import com.aceman.go4lunch.adapter.HistoryAdapter;
import com.aceman.go4lunch.api.RestaurantPublicHelper;
import com.aceman.go4lunch.api.UserHelper;
import com.aceman.go4lunch.data.models.HistoryDetails;
import com.aceman.go4lunch.data.models.RestaurantPublic;
import com.aceman.go4lunch.data.models.User;
import com.aceman.go4lunch.jobs.Alarm;
import com.aceman.go4lunch.utils.base.BaseActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import timber.log.Timber;

/**
 * Created by Lionel JOFFRAY - on 12/05/2019.
 * <p>
 * Settings Activity is where users can see and clear his history and liked place, enable notification on 12PM.
 */
public class SettingsActivity extends BaseActivity implements SettingsContract.SettingsViewInterface {

    SettingsPresenter mPresenter;
    @BindView(R.id.setting_enable_btn)
    Button mNotificationBtn;
    @BindView(R.id.settings_clear_history_btn)
    Button mClearHistoryBtn;
    @BindView(R.id.settings_recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.settings_reset_like)
    Button mClearLike;
    @BindView(R.id.like_settings)
    TextView mLikeTextView;
    HistoryAdapter mHistoryAdapter;
    Alarm mAlarm = new Alarm();
    boolean mCheckBtn;
    String mLikeName;
    List<HistoryDetails> mHistory = new ArrayList<>();

    /**
     * When created, the presenter is initialized, Data are collected and UI is updating.
     *
     * @param savedInstanceState savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new SettingsPresenter();
        mPresenter.attachView(this);
        configureRecyclerView();
        getHistoryListAndLike();
        checkBtnState();
        setBtnState();

    }

    /**
     * Get the activity layout.
     *
     * @return layout
     */
    @Override
    public int getActivityLayout() {
        return R.layout.activity_settings;
    }

    /**
     * Get the public history on Firestore.
     */
    public void getHistoryListAndLike() {
        UserHelper.getUser(getCurrentUser().getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                FirebaseFirestore.getInstance().collection("restaurant").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            setHistoryList(task);
                        } else {
                            Timber.tag("get Histroy").i("ERROR");
                        }
                    }
                });
            }
        });
    }

    /**
     * Only get the user match history on public history. If empty, disable clear btn.
     *
     * @param task task
     */
    public void setHistoryList(Task<QuerySnapshot> task) {
        mHistory.clear();
        for (QueryDocumentSnapshot document : task.getResult()) {
            RestaurantPublic userP = document.toObject(RestaurantPublic.class);
            if (userP.getHistory() != null && userP.getUsername().equals(getCurrentUser().getDisplayName())) {
                mHistory.add(userP.getHistory().getHistory());
            }
            if (userP.getLike() != null) {
                mLikeName = userP.getLike();
                mLikeTextView.setText(mLikeName);
            }
        }
        setBtnState();
        Timber.tag("Task To List").i("Success");
        mHistoryAdapter.notifyDataSetChanged();
    }

    /**
     * Initialize the recyclerview for history.
     */
    public void configureRecyclerView() {
        if (mHistory != null) {
            mHistoryAdapter = new HistoryAdapter(mHistory, this);
            mRecyclerView.setAdapter(mHistoryAdapter);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        }
    }

    /**
     * Set the btn state for the view.
     */
    public void setBtnState() {
        if (mHistory.size() > 0) {
            mClearHistoryBtn.setAlpha(1);
            mClearHistoryBtn.setClickable(true);
        } else {
            mClearHistoryBtn.setAlpha(0.5f);
            mClearHistoryBtn.setClickable(false);
        }
        if (mLikeName == null) {
            mClearLike.setAlpha(0.5f);
            mClearLike.setClickable(false);
        } else {
            mClearLike.setAlpha(1);
            mClearLike.setClickable(true);
        }

    }

    /**
     * Check Notification btn state.
     *
     * @return boolean state
     */
    public boolean checkBtnState() {

        if (!mAlarm.checkIfAlarmIsSet(this)) {
            mNotificationBtn.setText(getString(R.string.enable));
            mNotificationBtn.setBackground(getResources().getDrawable(R.drawable.button_radius_green_color));
            mCheckBtn = true;
        } else {
            mNotificationBtn.setText(getString(R.string.disable));
            mNotificationBtn.setBackground(getResources().getDrawable(R.drawable.button_radius_error_color));
            mCheckBtn = false;
        }

        return mCheckBtn;
    }

    /**
     * On click Enable/disable notification button.
     */
    @OnClick(R.id.setting_enable_btn)
    void onClickNotificationBtn() {
        if (mCheckBtn) {
            setNotificationOn();
        } else {
            setNotificationOff();
        }
    }

    /**
     * On click clear history btn.
     */
    @OnClick(R.id.settings_clear_history_btn)
    void onClickClearHistoryBtn() {
        Toast.makeText(this, getString(R.string.history_cleared), Toast.LENGTH_SHORT).show();
        UserHelper.getUser(getCurrentUser().getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User currentUser = documentSnapshot.toObject(User.class);
                RestaurantPublicHelper.resetUserHistory(currentUser.getUid()).addOnFailureListener(onFailureListener());
                mClearHistoryBtn.setAlpha(0.5f);
                mHistoryAdapter.notifyDataSetChanged();
            }
        });
        setBtnState();
    }

    /**
     * On click reset like btn.
     */
    @OnClick(R.id.settings_reset_like)
    void onClickResetLike() {
        Toast.makeText(this, getString(R.string.like_cleared), Toast.LENGTH_SHORT).show();
        UserHelper.getUser(getCurrentUser().getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User currentUser = documentSnapshot.toObject(User.class);
                if (currentUser.getLike() != null) {
                    UserHelper.updateLikeRestaurant(getCurrentUser().getUid(), null).addOnFailureListener(onFailureListener());
                    RestaurantPublicHelper.updateLikeRestaurant(getCurrentUser().getUid(), null).addOnFailureListener(onFailureListener());
                }
            }
        });
        setBtnState();
    }

    /**
     * Method who set the notification with AlarmManager.
     *
     * @see Alarm
     * @see com.aceman.go4lunch.jobs.DailyWorker
     */
    void setNotificationOn() {
        mAlarm.setAlarm(this);
        Toast.makeText(this, getString(R.string.notification_set), Toast.LENGTH_SHORT).show();
        checkBtnState();
    }

    /**
     * Method who cancel the notification with AlarmManager.
     *
     * @see Alarm
     * @see com.aceman.go4lunch.jobs.DailyWorker
     */
    public void setNotificationOff() {
        mAlarm.cancelAlarm(this);
        WorkManager.getInstance().cancelAllWorkByTag("RequestDaliy");
        Toast.makeText(this, getString(R.string.notification_canceled), Toast.LENGTH_SHORT).show();
        checkBtnState();
    }

}
