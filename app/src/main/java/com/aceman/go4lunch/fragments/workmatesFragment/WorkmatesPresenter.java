package com.aceman.go4lunch.fragments.workmatesFragment;

import android.support.annotation.NonNull;

import com.aceman.go4lunch.activities.coreActivity.CoreContract;
import com.aceman.go4lunch.api.UserHelper;
import com.aceman.go4lunch.models.User;
import com.aceman.go4lunch.utils.BasePresenter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

/**
 * Created by Lionel JOFFRAY - on 04/06/2019.
 */
public class WorkmatesPresenter extends BasePresenter implements WorkmatesContract.WorkmatesPresenterInterface {

    @Override
    public void getUserList() {

        UserHelper.getUser(getCurrentUser().getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                FirebaseFirestore.getInstance().collection("restaurant").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ((WorkmatesContract.WorkmatesViewInterface) getView()).setUserListFromFirebase(task);
                        } else {
                            ((WorkmatesContract.WorkmatesViewInterface) getView()).errorGettingUserListFromFirebase(task);
                        }
                    }
                });
                ((WorkmatesContract.WorkmatesViewInterface) getView()).updateRecyclerView();
            }
        });
    }

    public FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }
}
