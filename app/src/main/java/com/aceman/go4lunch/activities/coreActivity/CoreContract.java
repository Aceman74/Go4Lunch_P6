package com.aceman.go4lunch.activities.coreActivity;

import com.aceman.go4lunch.data.details.PlacesDetails;
import com.aceman.go4lunch.models.User;
import com.aceman.go4lunch.utils.BaseView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.QuerySnapshot;

import io.reactivex.disposables.Disposable;

/**
 * Created by Lionel JOFFRAY - on 04/06/2019.
 */
public interface CoreContract {

    interface CorePresenterInterface {

        void getUserList();

        void updateUIWhenCreating();

        void getSearchRestaurant(Disposable mSearchDisposable, String mSearchID);

        FirebaseUser getCurrentUser();

    }

    interface CoreViewInterface extends BaseView {

        void onClickProfile();

        void configureToolBar();

        void configureViewPager();

        void navigationDrawerListener();

        void signOutUserFromFirebase();

        void configureHamburgerBtn();

        void configureNavigationView();

        void pagerListener();

        void autocompleteIntent();

        void addDetail(PlacesDetails details);

        OnSuccessListener<Void> updateUIAfterRESTRequestsCompleted(final int origin);

        void setUserListFromFirebase(Task<QuerySnapshot> task);

        void errorGettingUserListFromFirebase(Task<QuerySnapshot> task);

        void postStickyEventBusUserList();

        void loadUserImgWithGlide();

        void loadUserEmail();

        void loadUserUsername(User currentUser);

        void zoomOnMapLocation();


    }
}
