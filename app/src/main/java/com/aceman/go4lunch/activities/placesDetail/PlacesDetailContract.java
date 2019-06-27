package com.aceman.go4lunch.activities.placesDetail;

import com.aceman.go4lunch.data.models.Restaurant;
import com.aceman.go4lunch.data.models.RestaurantPublic;
import com.aceman.go4lunch.utils.BaseView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

/**
 * Created by Lionel JOFFRAY - on 04/06/2019.
 * <p>
 * The contracts for Places Detail Activity.
 */
public interface PlacesDetailContract {


    interface PlacesDetailPresenterInterface {

        FirebaseUser getCurrentUser();

        OnFailureListener onFailureListener();

        void onClickLike(String mID);

        void lunchIntentSetInfos();

        void setIconTintWithFirebaseInfos(String mID, Restaurant mRestaurant);

        void onClickSelectFloatingBtn(String mID, Restaurant mRestaurant);

        void resetPlaceChoiceIfNewDay();

        List<RestaurantPublic> getUserList();

        List<RestaurantPublic> getUserJoiningList(String name);

    }

    interface PlacesDetailViewInterface extends BaseView {

        void nullCheck();

        void configureInfo();

        void selectBtnListener();

        void likeBtnListener();

        void callBtnListener();

        void websiteBtnListener();

        void showStar(int star);

        void configureRecyclerView();

        void toastAddLike();

        void toastRemoveLike();

        void toastAddPlace();

        void toastRemovePlace();

        void noLunchCase();

        void getAnyIntent();

        void onUpdateFirebaseFailed();

        void likeBtnNullColor();

        void likeBtnAddedColor();

        void setInfosWithSnapshot(RestaurantPublic currentUser);

        void loadPlaceImageWithGlide();

        void floatingBtnNullStyle();

        void floatingBtnAddedStyle();

        void notifyDataChanged();

        void startGettingUserList();

        void showInfo();
    }
}
