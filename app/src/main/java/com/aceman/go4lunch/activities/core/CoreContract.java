package com.aceman.go4lunch.activities.core;

import com.aceman.go4lunch.data.models.User;
import com.aceman.go4lunch.utils.BaseView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.QuerySnapshot;

/**
 * Created by Lionel JOFFRAY - on 04/06/2019.
 * <p>
 * The contracts for Core Activity.
 */
public interface CoreContract {

    interface CorePresenterInterface {

        void getUserList();

        void updateUIWhenCreating();

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


        OnSuccessListener<Void> updateUIAfterRESTRequestsCompleted(final int origin);

        void setUserListFromFirebase(Task<QuerySnapshot> task);

        void errorGettingUserListFromFirebase(Task<QuerySnapshot> task);

        void postStickyEventBusUserList();

        void loadUserImgWithGlide();

        void loadUserEmail();

        void loadUserUsername(User currentUser);


    }
}
