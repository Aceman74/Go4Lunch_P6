package com.aceman.go4lunch.navigation.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.aceman.go4lunch.R;
import com.aceman.go4lunch.api.UserHelper;
import com.aceman.go4lunch.base.BaseActivity;
import com.aceman.go4lunch.models.User;
import com.aceman.go4lunch.navigation.adapter.WorkersJoiningAdapter;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Objects;

import butterknife.BindView;

import static com.aceman.go4lunch.navigation.activities.CoreActivity.mResults;

public class PlacesDetailActivity extends BaseActivity {

    @BindView(R.id.places_details_recycler_view)
    RecyclerView mRecyclerView;
    WorkersJoiningAdapter mWorkersJoiningAdapter;
    Context mContext;
    @BindView(R.id.detail_fragment_banner_name)
    TextView setName;
    @BindView(R.id.detail_fragment_banner_address)
    TextView setAdress;
    String mName;
    String mAddress;
    @BindView(R.id.detail_fragment_btn)
    FloatingActionButton mActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        configureRecyclerView();

        Intent details = getIntent();
        mName = details.getStringExtra("name");
        mAddress = details.getStringExtra("address");
        if (mAddress == null)
            mAddress = "Adresse:";
        configureInfos();
    }

    private void configureInfos() {
        setName.setText(mName);
        setAdress.setText(mAddress);
mActionButton.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {

        UserHelper.getUser(getCurrentUser().getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User currentUser = documentSnapshot.toObject(User.class);
               if(currentUser.getRestaurant() != null && currentUser.getRestaurant().equals(mName)) {
                   UserHelper.updateRestaurant(getCurrentUser().getUid(), null).addOnFailureListener(onFailureListener());
               }else{
                   UserHelper.updateRestaurant(getCurrentUser().getUid(), mName).addOnFailureListener(onFailureListener());
               }
            }
        });
    }
});
    }

    @Override
    protected OnFailureListener onFailureListener() {
        return super.onFailureListener();
    }

    @Nullable
    @Override
    protected FirebaseUser getCurrentUser() {
        return super.getCurrentUser();
    }

    @Override
    protected Boolean isCurrentUserLogged() {
        return super.isCurrentUserLogged();
    }

    @Override
    public int getFragmentLayout() {
        return R.layout.activity_places_detail;
    }

    public void configureRecyclerView() {
        if (mResults != null) {
            mWorkersJoiningAdapter = new WorkersJoiningAdapter(mResults, Glide.with(this), mContext);
            mRecyclerView.setAdapter(mWorkersJoiningAdapter);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
            mRecyclerView.addItemDecoration(new DividerItemDecoration(Objects.requireNonNull(this.getApplicationContext()), DividerItemDecoration.VERTICAL));
        }
    }
}
