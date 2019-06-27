package com.aceman.go4lunch.activities.placesDetail;

import android.support.annotation.NonNull;

import com.aceman.go4lunch.api.RestaurantPublicHelper;
import com.aceman.go4lunch.api.UserHelper;
import com.aceman.go4lunch.data.models.History;
import com.aceman.go4lunch.data.models.HistoryDetails;
import com.aceman.go4lunch.data.models.Restaurant;
import com.aceman.go4lunch.data.models.RestaurantPublic;
import com.aceman.go4lunch.data.models.User;
import com.aceman.go4lunch.utils.BasePresenter;
import com.aceman.go4lunch.utils.DateSetter;
import com.aceman.go4lunch.utils.FirestoreUserList;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by Lionel JOFFRAY - on 04/06/2019.
 * <p>
 * The presenter for Places Detail Activity.
 */
public class PlacesDetailPresenter extends BasePresenter implements PlacesDetailContract.PlacesDetailPresenterInterface {
    public static List<RestaurantPublic> mUserList = new ArrayList<>();
    public static List<RestaurantPublic> mUserJoinning = new ArrayList<>();
    public static String date = DateSetter.getFormattedDate();

    /**
     * Get the User infos when My Lunch is clicked.
     */
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
            }
        });

    }

    /**
     * Set the icon tint/state when setting the view.
     *
     * @param mID         restaurant ID
     * @param mRestaurant restaurant object
     */
    @Override
    public void setIconTintWithFirebaseInfos(final String mID, final Restaurant mRestaurant) {

        UserHelper.getUser(getCurrentUser().getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User currentUser = documentSnapshot.toObject(User.class);
                if (currentUser.getRestaurant() != null && currentUser.getRestaurant().equals(mID)) {
                    ((PlacesDetailContract.PlacesDetailViewInterface) getView()).floatingBtnAddedStyle();
                }
                if (currentUser.getLike() != null && currentUser.getLike().equals(mRestaurant.getName())) {
                    ((PlacesDetailContract.PlacesDetailViewInterface) getView()).likeBtnAddedColor();
                }
            }
        });
    }

    /**
     * Handle the click on select btn (add or remove place for lunch and update btn state)
     *
     * @param mID         restaurant ID
     * @param mRestaurant restaurant object
     */
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
                    RestaurantPublicHelper.restaurantPublic(getCurrentUser().getUid(), mRestaurant.getPlaceID(), mRestaurant.getName(), mRestaurant, history, date).addOnFailureListener(onFailureListener());
                    UserHelper.updateRestaurantID(getCurrentUser().getUid(), mRestaurant.getPlaceID(), mRestaurant.getName()).addOnFailureListener(onFailureListener());
                    ((PlacesDetailContract.PlacesDetailViewInterface) getView()).floatingBtnAddedStyle();
                    ((PlacesDetailContract.PlacesDetailViewInterface) getView()).toastAddPlace();
                }
            }
        });
        ((PlacesDetailContract.PlacesDetailViewInterface) getView()).startGettingUserList();
    }

    /**
     * Handle the click on Like btn (add or remove place for like and update btn state)
     *
     * @param name place name
     */
    @Override
    public void onClickLike(final String name) {

        UserHelper.getUser(getCurrentUser().getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User currentUser = documentSnapshot.toObject(User.class);
                if (currentUser.getLike() != null && currentUser.getLike().equals(name)) {
                    UserHelper.updateLikeRestaurant(getCurrentUser().getUid(), null).addOnFailureListener(onFailureListener());
                    RestaurantPublicHelper.updateLikeRestaurant(getCurrentUser().getUid(), null).addOnFailureListener(onFailureListener());
                    ((PlacesDetailContract.PlacesDetailViewInterface) getView()).likeBtnNullColor();
                    ((PlacesDetailContract.PlacesDetailViewInterface) getView()).toastRemoveLike();
                } else {
                    UserHelper.updateLikeRestaurant(getCurrentUser().getUid(), name).addOnFailureListener(onFailureListener());
                    RestaurantPublicHelper.updateLikeRestaurant(getCurrentUser().getUid(), name).addOnFailureListener(onFailureListener());
                    ((PlacesDetailContract.PlacesDetailViewInterface) getView()).likeBtnAddedColor();
                    ((PlacesDetailContract.PlacesDetailViewInterface) getView()).toastAddLike();
                }
            }
        });
    }

    /**
     * Reset the btn state if date is different.
     */
    @Override
    public void resetPlaceChoiceIfNewDay() {
        RestaurantPublicHelper.getUser(getCurrentUser().getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                RestaurantPublic restaurant = documentSnapshot.toObject(RestaurantPublic.class);

                if (restaurant.getRestaurantName() != null && restaurant.getDate() != null && !restaurant.getDate().equals(date)) {
                    RestaurantPublicHelper.restaurantPublic(getCurrentUser().getUid(), null, null, null, null, null).addOnFailureListener(onFailureListener());
                    UserHelper.updateRestaurantID(getCurrentUser().getUid(), null, null).addOnFailureListener(onFailureListener());
                    ((PlacesDetailContract.PlacesDetailViewInterface) getView()).floatingBtnNullStyle();
                    ((PlacesDetailContract.PlacesDetailViewInterface) getView()).configureInfo();
                }
            }
        });
    }

    /**
     * Get user list from Firestore
     *
     * @return public userlist
     */
    @Override
    public List<RestaurantPublic> getUserList() {

        mUserList = FirestoreUserList.getUserList(getCurrentUser());
        return mUserList;
    }

    /**
     * Get the user joining the place.
     *
     * @param name restaurant name
     * @return list of user same place
     * @see FirestoreUserList
     */
    @Override
    public List<RestaurantPublic> getUserJoiningList(String name) {

        mUserJoinning = FirestoreUserList.getUserJoinningList(getCurrentUser(), name);
        return mUserJoinning;
    }

    /**
     * get the current user
     *
     * @return current user
     */
    @Override
    public FirebaseUser getCurrentUser() {

        return FirebaseAuth.getInstance().getCurrentUser();
    }

    /**
     * Failure listener for REST
     *
     * @return error msg
     */
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