package com.aceman.go4lunch.activities.placesDetailActivity;

import android.support.annotation.NonNull;

import com.aceman.go4lunch.api.RestaurantPublicHelper;
import com.aceman.go4lunch.api.UserHelper;
import com.aceman.go4lunch.models.History;
import com.aceman.go4lunch.models.HistoryDetails;
import com.aceman.go4lunch.models.Restaurant;
import com.aceman.go4lunch.models.RestaurantPublic;
import com.aceman.go4lunch.models.User;
import com.aceman.go4lunch.utils.BasePresenter;
import com.aceman.go4lunch.utils.DateSetter;
import com.aceman.go4lunch.utils.FirestoreUserList;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * Created by Lionel JOFFRAY - on 04/06/2019.
 */
public class PlacesDetailPresenter extends BasePresenter implements PlacesDetailContract.PlacesDetailPresenterInterface {
    public static List<RestaurantPublic> mUserList = new ArrayList<>();
    public static String date = DateSetter.getFormattedDate();

    @Override
    public void lunchIntentSetInfos() {

        RestaurantPublicHelper.getUser(Objects.requireNonNull(getCurrentUser()).getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                RestaurantPublic currentUser = documentSnapshot.toObject(RestaurantPublic.class);
                if (currentUser.getDetails() != null && currentUser.getDate().equals(DateSetter.getFormattedDate())) {
                    ((PlacesDetailContract.PlacesDetailViewInterface) getView()).setInfosWithSnapshot(currentUser);
                } else {
                    ((PlacesDetailContract.PlacesDetailViewInterface) getView()).noLunchCase();

                }
                ((PlacesDetailContract.PlacesDetailViewInterface) getView()).startGettingUserList();
                ((PlacesDetailContract.PlacesDetailViewInterface) getView()).showInfos();
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
        final History history = new History();
        HistoryDetails historyDetails = new HistoryDetails();
        historyDetails.setDate(date);
        historyDetails.setName(mRestaurant.getName());
        history.setDate(date);
        history.setHistory(historyDetails);
        UserHelper.getUser(getCurrentUser().getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                User currentUser = documentSnapshot.toObject(User.class);

                if (currentUser.getRestaurant() != null && currentUser.getRestaurant().equals(mID)) {

                    RestaurantPublicHelper.restaurantPublic(getCurrentUser().getUid(), null, null, null, null, null).addOnFailureListener(onFailureListener());
                    UserHelper.updateRestaurantID(getCurrentUser().getUid(), null, null).addOnFailureListener(onFailureListener());
                    ((PlacesDetailContract.PlacesDetailViewInterface) getView()).floatingBtnNullStyle();
                    ((PlacesDetailContract.PlacesDetailViewInterface) getView()).toastRemovePlace();
                } else {
                    String date = DateSetter.getFormattedDate();
                    RestaurantPublicHelper.restaurantPublic(getCurrentUser().getUid(), mRestaurant.getPlaceID(), mRestaurant.getName(), mRestaurant,history, date).addOnFailureListener(onFailureListener());
                    UserHelper.updateRestaurantID(getCurrentUser().getUid(), mRestaurant.getPlaceID(), mRestaurant.getName()).addOnFailureListener(onFailureListener());
                    ((PlacesDetailContract.PlacesDetailViewInterface) getView()).floatingBtnAddStyle();
                    ((PlacesDetailContract.PlacesDetailViewInterface) getView()).toastAddPlace();

                }

                ((PlacesDetailContract.PlacesDetailViewInterface) getView()).startGettingUserList();
            }
        });
    }

    @Override
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
    public List<RestaurantPublic> setNewUserListIfJoinin(List<RestaurantPublic> mUserList, List<RestaurantPublic> mUserJoinning, String mName) {
        mUserJoinning.clear();
        int index = 0;
        for (int i = 0; i < mUserList.size(); i++) {
            if (mUserList.get(i).getRestaurantName() != null && mUserList.get(i).getRestaurantName().equals(mName)) {
                mUserJoinning.add(index, mUserList.get(i));
                index++;
            }
        }
        return mUserJoinning;
    }

    @Override
    public void resetPlaceChoiceIfNewDay() {
        UserHelper.getUser(getCurrentUser().getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                User currentUser = documentSnapshot.toObject(User.class);
                RestaurantPublic restaurant = documentSnapshot.toObject(RestaurantPublic.class);

                if (currentUser.getRestaurant() != null && restaurant.getDate() != null && !restaurant.getDate().equals(date)) {
                    RestaurantPublicHelper.restaurantPublic(getCurrentUser().getUid(), null, null, null, null, null).addOnFailureListener(onFailureListener());
                    UserHelper.updateRestaurantID(getCurrentUser().getUid(), null, null).addOnFailureListener(onFailureListener());
                    ((PlacesDetailContract.PlacesDetailViewInterface) getView()).floatingBtnNullStyle();
                    ((PlacesDetailContract.PlacesDetailViewInterface) getView()).configureInfos();
                }
            }
        });
    }

    @Override
    public List<RestaurantPublic> getUserList() {

        mUserList = FirestoreUserList.getUserList(getCurrentUser());
        return mUserList;
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