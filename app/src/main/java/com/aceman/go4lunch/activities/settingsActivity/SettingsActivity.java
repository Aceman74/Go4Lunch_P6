package com.aceman.go4lunch.activities.settingsActivity;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;
import android.widget.Toast;

import com.aceman.go4lunch.R;
import com.aceman.go4lunch.api.HistoryHelper;
import com.aceman.go4lunch.api.UserHelper;
import com.aceman.go4lunch.models.History;
import com.aceman.go4lunch.models.User;
import com.aceman.go4lunch.utils.DateSetter;
import com.aceman.go4lunch.utils.base.BaseActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;

import butterknife.BindView;
import butterknife.OnClick;

public class SettingsActivity extends BaseActivity implements SettingsContract.SettingsViewInterface {

    SettingsPresenter mPresenter;
    @BindView(R.id.setting_enable_btn)
    Button mNotificationBtn;
    @BindView(R.id.settings_clear_history_btn)
    Button mClearHistoryBtn;
    @BindView(R.id.settings_recycler_view)
    RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new SettingsPresenter();
        mPresenter.attachView(this);
        configureRecyclerView();

    }

    @OnClick(R.id.setting_enable_btn)
    void onClickNotificationBtn() {
        Toast.makeText(this, "Clicked!", Toast.LENGTH_SHORT).show();

        UserHelper.getUser(getCurrentUser().getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

              String date = DateSetter.getFormattedDate();
                User currentUser = documentSnapshot.toObject(User.class);
                History history = documentSnapshot.toObject(History.class);

                    HistoryHelper.addNewPlaceToHistory(currentUser.getUid(),new History("Coucou",date)).addOnFailureListener(onFailureListener());
                }
        });
    }

    @OnClick(R.id.settings_clear_history_btn)
    void onClickClearGHistoryBtn(){
        Toast.makeText(this, "Clicked!", Toast.LENGTH_SHORT).show();
        UserHelper.getUser(getCurrentUser().getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                String date = DateSetter.getFormattedDate();
                User currentUser = documentSnapshot.toObject(User.class);
                History history = documentSnapshot.toObject(History.class);

                HistoryHelper.deleteUserHistory(currentUser.getUid()).addOnFailureListener(onFailureListener());
            }
        });
    }

    private void configureRecyclerView() {


    }

    @Override
    public int getFragmentLayout() {
        return R.layout.activity_settings;
    }
}
