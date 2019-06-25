package com.aceman.go4lunch.utils;

import android.support.annotation.NonNull;

import com.aceman.go4lunch.api.RestaurantPublicHelper;
import com.aceman.go4lunch.api.UserHelper;
import com.aceman.go4lunch.models.RestaurantPublic;
import com.aceman.go4lunch.utils.events.RefreshEvent;
import com.aceman.go4lunch.utils.events.UserJoiningRefreshEvent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * Created by Lionel JOFFRAY - on 06/06/2019.
 */
public class FirestoreUserList {

    public static List<RestaurantPublic> mUserList = new ArrayList<>();
    public static List<RestaurantPublic> mUserJoinning = new ArrayList<>();

    public static List<RestaurantPublic> getUserList(FirebaseUser user) {

        UserHelper.getUser(user.getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                RestaurantPublicHelper.getRestaurantCollection().get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            mUserList = setUserListFromFirebase(task);
                        } else {
                            Timber.tag("UserListGet").e("ERROR GETTING LIST");
                        }
                    }
                });
            }
        });
        return mUserList;
    }

    public static List<RestaurantPublic> setUserListFromFirebase(Task<QuerySnapshot> task) {
        mUserList.clear();
        for (QueryDocumentSnapshot document : task.getResult()) {
            RestaurantPublic userP = document.toObject(RestaurantPublic.class);     // < == GET LIST FIRESTORE
            mUserList.add(userP);
            Timber.tag("Task To List").i("Sucess");
        }
        Timber.tag("TEST POS_3").e("THIS IS DATA USERLIST");
        return mUserList;
    }

    public static List<RestaurantPublic> getUserJoinningList(FirebaseUser user, final String name) {

        UserHelper.getUser(user.getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                RestaurantPublicHelper.getRestaurantCollection().get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            mUserJoinning = setUserJoiningListFromFirebase(task, name);
                        } else {
                            Timber.tag("UserListGet").e("ERROR GETTING LIST");
                        }
                    }
                });
            }
        });
        return mUserJoinning;
    }

    public static List<RestaurantPublic> setUserJoiningListFromFirebase(Task<QuerySnapshot> task, String name) {
            mUserJoinning.clear();
            for (QueryDocumentSnapshot document : task.getResult()) {
            RestaurantPublic userP = document.toObject(RestaurantPublic.class);     // < == GET LIST FIRESTORE
                if(userP.getRestaurantName() != null && userP.getRestaurantName().equals(name))
                mUserJoinning.add(userP);
            Timber.tag("Task To JoinList").i("Sucess");
        }
        Timber.tag("TEST POS_4").e("THIS IS DATA USER JOINING");
        EventBus.getDefault().post(new UserJoiningRefreshEvent());
        return mUserJoinning;
        }
}

