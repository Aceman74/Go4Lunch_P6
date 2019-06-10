package com.aceman.go4lunch.activities.settingsActivity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;
import android.widget.Toast;

import androidx.work.WorkManager;

import com.aceman.go4lunch.R;
import com.aceman.go4lunch.adapter.HistoryAdapter;
import com.aceman.go4lunch.api.RestaurantPublicHelper;
import com.aceman.go4lunch.api.UserHelper;
import com.aceman.go4lunch.jobs.Alarm;
import com.aceman.go4lunch.models.HistoryDetails;
import com.aceman.go4lunch.models.RestaurantPublic;
import com.aceman.go4lunch.models.User;
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

public class SettingsActivity extends BaseActivity implements SettingsContract.SettingsViewInterface {

    SettingsPresenter mPresenter;
    @BindView(R.id.setting_enable_btn)
    Button mNotificationBtn;
    @BindView(R.id.settings_clear_history_btn)
    Button mClearHistoryBtn;
    @BindView(R.id.settings_recycler_view)
    RecyclerView mRecyclerView;
    HistoryAdapter mHistoryAdapter;
    Alarm mAlarm = new Alarm();
    boolean mCheckBtn;
    List<HistoryDetails> mHistory = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new SettingsPresenter();
        mPresenter.attachView(this);
        configureRecyclerView();
        getHistoryList();
        checkBtnState();
        setHistoryBtnState();

    }

    public void getHistoryList() {

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

    public void setHistoryList(Task<QuerySnapshot> task) {
        mHistory.clear();
        for (QueryDocumentSnapshot document : task.getResult()) {
            RestaurantPublic userP = document.toObject(RestaurantPublic.class);     // < == GET LIST FIRESTORE
            if (userP.getHistory() != null && userP.getUsername().equals(getCurrentUser().getDisplayName()))
                mHistory.add(userP.getHistory().getHistory());
        }
        setHistoryBtnState();
        Timber.tag("Task To List").i("Success");
        mHistoryAdapter.notifyDataSetChanged();
    }

    public void configureRecyclerView() {
        if (mHistory != null) {
            mHistoryAdapter = new HistoryAdapter(mHistory, this);
            mRecyclerView.setAdapter(mHistoryAdapter);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        }
    }

    public void setHistoryBtnState(){
        if(mHistory.size()>0){
            mClearHistoryBtn.setAlpha(1);
        }else{
            mClearHistoryBtn.setAlpha(0.5f);
        }
    }

   public boolean checkBtnState(){

        if (!mAlarm.checkIfAlarmIsSet(this)) {
            mNotificationBtn.setText("Enable");
            mNotificationBtn.setBackground(getResources().getDrawable(R.drawable.button_radius_green_color));
            mCheckBtn = true;
        } else {
            mNotificationBtn.setText("Disable");
            mNotificationBtn.setBackground(getResources().getDrawable(R.drawable.button_radius_error_color));
            mCheckBtn = false;
        }

        return mCheckBtn;
    }
    @OnClick(R.id.setting_enable_btn)
    void onClickNotificationBtn() {
        if(mCheckBtn){
            setNotificationOn();
        }else{
            setNotificationOff();
        }
    }

    @OnClick(R.id.settings_clear_history_btn)
    void onClickClearHistoryBtn() {
        Toast.makeText(this, "Clicked!", Toast.LENGTH_SHORT).show();
        UserHelper.getUser(getCurrentUser().getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User currentUser = documentSnapshot.toObject(User.class);
                RestaurantPublicHelper.resetUserHistory(currentUser.getUid()).addOnFailureListener(onFailureListener());
                mClearHistoryBtn.setAlpha(0.5f);
                mHistoryAdapter.notifyDataSetChanged();
            }
        });
        setHistoryBtnState();
    }


    void setNotificationOn() {
        mAlarm.setAlarm(this);
        Toast.makeText(this, "Notifications set !", Toast.LENGTH_SHORT).show();
        checkBtnState();
    }

    public void setNotificationOff() {

        mAlarm.cancelAlarm(this);
        WorkManager.getInstance().cancelAllWorkByTag("RequestDaliy");
        Toast.makeText(this, "Notifications Canceled !", Toast.LENGTH_SHORT).show();
        checkBtnState();
    }

    @Override
    public int getFragmentLayout() {
        return R.layout.activity_settings;
    }
}
