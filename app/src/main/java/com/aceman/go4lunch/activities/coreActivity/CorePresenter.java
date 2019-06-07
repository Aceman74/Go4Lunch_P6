package com.aceman.go4lunch.activities.coreActivity;

import android.support.annotation.NonNull;
import android.util.Log;

import com.aceman.go4lunch.api.PlacesApi;
import com.aceman.go4lunch.api.UserHelper;
import com.aceman.go4lunch.data.details.PlacesDetails;
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

import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import timber.log.Timber;

/**
 * Created by Lionel JOFFRAY - on 04/06/2019.
 */
public class CorePresenter extends BasePresenter implements CoreContract.CorePresenterInterface {


    @Override
    public void getUserList() {

        UserHelper.getUser(getCurrentUser().getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                FirebaseFirestore.getInstance().collection("restaurant").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ((CoreContract.CoreViewInterface) getView()).setUserListFromFirebase(task);
                    } else {
                            ((CoreContract.CoreViewInterface) getView()).errorGettingUserListFromFirebase(task);
                    }
                    }
                });
            }
        });
        ((CoreContract.CoreViewInterface) getView()).postStickyEventBusUserList();
    }

    @Override
    public void updateUIWhenCreating() {

        if (this.getCurrentUser() != null) {

            if (this.getCurrentUser().getPhotoUrl() != null) {
                ((CoreContract.CoreViewInterface) getView()).loadUserImgWithGlide();
            }

            ((CoreContract.CoreViewInterface) getView()).loadUserEmail();

            // 7 - Get additional data from Firestore (isPrivate & Username)
            UserHelper.getUser(this.getCurrentUser().getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    User currentUser = documentSnapshot.toObject(User.class);
                    if(currentUser == null){    //  logout if no username set (account delete by admin )
                        ((CoreContract.CoreViewInterface) getView()).signOutUserFromFirebase();
                    }else{
                        ((CoreContract.CoreViewInterface) getView()).loadUserUsername(currentUser);
                    }
                }
            });
        }
    }


    public FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }


}
