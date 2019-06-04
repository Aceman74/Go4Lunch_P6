package com.aceman.go4lunch.activities.placesDetailActivity;

import android.support.annotation.NonNull;

import com.aceman.go4lunch.api.RestaurantPublicHelper;
import com.aceman.go4lunch.api.UserHelper;
import com.aceman.go4lunch.models.Restaurant;
import com.aceman.go4lunch.models.RestaurantPublic;
import com.aceman.go4lunch.models.User;
import com.aceman.go4lunch.utils.BasePresenter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

/**
 * Created by Lionel JOFFRAY - on 04/06/2019.
 */
public class PlacesDetailPresenter extends BasePresenter implements PlacesDetailContract.PlacesDetailPresenterInterface {


    public void lunchIntentSetInfos() {

        RestaurantPublicHelper.getUser(Objects.requireNonNull(getCurrentUser()).getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                RestaurantPublic currentUser = documentSnapshot.toObject(RestaurantPublic.class);
                if (currentUser.getDetails() != null) {
                    ((PlacesDetailContract.PlacesDetailViewInterface) getView()).setInfosWithSnapshot(currentUser);
                } else {
                    ((PlacesDetailContract.PlacesDetailViewInterface) getView()).noLunchCase();

                }
            }
        });
    }

    @Override
    public void setIconTintWithFirebaseInfos(final String mID, final Restaurant mRestaurant) {

        UserHelper.getUser(getCurrentUser().getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User currentUser = documentSnapshot.toObject(User.class);
                if (currentUser.getRestaurant() != null && currentUser.getRestaurant().equals(mID)) {
                    ((PlacesDetailContract.PlacesDetailViewInterface) getView()).setFloatingBtnTint();
                }
                if (currentUser.getLike() != null && currentUser.getLike().equals(mRestaurant.getPlaceID())) {
                    ((PlacesDetailContract.PlacesDetailViewInterface) getView()).setLikeBtnTint();
                }
            }
        });
    }

    @Override
    public void onClickSelectFloatingBtn(final String mID, final Restaurant mRestaurant) {
        UserHelper.getUser(getCurrentUser().getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                User currentUser = documentSnapshot.toObject(User.class);

                if (currentUser.getRestaurant() != null && currentUser.getRestaurant().equals(mID)) {

                    RestaurantPublicHelper.restaurantPublic(getCurrentUser().getUid(), null, null, null, null).addOnFailureListener(onFailureListener());
                    UserHelper.updateRestaurantID(getCurrentUser().getUid(), null, null).addOnFailureListener(onFailureListener());
                    ((PlacesDetailContract.PlacesDetailViewInterface) getView()).floatingBtnNullStyle();
                    ((PlacesDetailContract.PlacesDetailViewInterface) getView()).toastRemovePlace();
                } else {
                    Date todayDate = Calendar.getInstance().getTime();
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                    String date = formatter.format(todayDate);
                    RestaurantPublicHelper.restaurantPublic(getCurrentUser().getUid(), mRestaurant.getPlaceID(), mRestaurant.getName(), mRestaurant, date).addOnFailureListener(onFailureListener());
                    UserHelper.updateRestaurantID(getCurrentUser().getUid(), mRestaurant.getPlaceID(), mRestaurant.getName()).addOnFailureListener(onFailureListener());
                    ((PlacesDetailContract.PlacesDetailViewInterface) getView()).floatingBtnAddStyle();
                    ((PlacesDetailContract.PlacesDetailViewInterface) getView()).toastAddPlace();
                }
            }
        });
    }


    public void onClickLike(final String mID) {

        UserHelper.getUser(getCurrentUser().getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User currentUser = documentSnapshot.toObject(User.class);
                if (currentUser.getLike() != null && currentUser.getLike().equals(mID)) {
                    UserHelper.updateLikeRestaurant(getCurrentUser().getUid(), null).addOnFailureListener(onFailureListener());
                    RestaurantPublicHelper.updateLikeRestaurant(getCurrentUser().getUid(), null).addOnFailureListener(onFailureListener());
                    ((PlacesDetailContract.PlacesDetailViewInterface) getView()).likeBtnRemoveColor();
                    ((PlacesDetailContract.PlacesDetailViewInterface) getView()).toastRemoveLike();
                } else {
                    UserHelper.updateLikeRestaurant(getCurrentUser().getUid(), mID).addOnFailureListener(onFailureListener());
                    RestaurantPublicHelper.updateLikeRestaurant(getCurrentUser().getUid(), mID).addOnFailureListener(onFailureListener());
                    ((PlacesDetailContract.PlacesDetailViewInterface) getView()).likeBtnAddColor();
                    ((PlacesDetailContract.PlacesDetailViewInterface) getView()).toastAddLike();
                }
            }
        });
    }

    @Override
    public FirebaseUser getCurrentUser() {

        return FirebaseAuth.getInstance().getCurrentUser();
    }

    @Override
    public OnFailureListener onFailureListener() {
        return new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                ((PlacesDetailContract.PlacesDetailViewInterface) getView()).onUpdateFirebaseFailed();
            }
        };
    }
}