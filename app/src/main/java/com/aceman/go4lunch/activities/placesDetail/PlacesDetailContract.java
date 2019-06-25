package com.aceman.go4lunch.activities.placesDetail;

import com.aceman.go4lunch.models.Restaurant;
import com.aceman.go4lunch.models.RestaurantPublic;
import com.aceman.go4lunch.utils.BaseView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

/**
 * Created by Lionel JOFFRAY - on 04/06/2019.
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

        List<RestaurantPublic> getUserJoinningList(String name);

    }

    interface PlacesDetailViewInterface extends BaseView {

        void nullCheck();

        void configureInfos();

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

        void likeBtnRemoveColor();

        void likeBtnAddColor();

        void setInfosWithSnapshot(RestaurantPublic currentUser);

        void loadPlaceImageWithGlide();

        void setFloatingBtnTint();

        void setLikeBtnTint();

        void floatingBtnNullStyle();

        void floatingBtnAddStyle();

        void notifyDataChanged();

        void startGettingUserList();

        void showInfos();
    }
}
