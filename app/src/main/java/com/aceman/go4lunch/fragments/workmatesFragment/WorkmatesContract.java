package com.aceman.go4lunch.fragments.workmatesFragment;

import com.aceman.go4lunch.utils.BaseView;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.QuerySnapshot;

/**
 * Created by Lionel JOFFRAY - on 04/06/2019.
 */
public interface WorkmatesContract {

    interface WorkmatesPresenterInterface{

        void getUserList();

        FirebaseUser getCurrentUser();
    }

    interface WorkmatesViewInterface extends BaseView {

        void configureRecyclerView();

        void setUserListFromFirebase(Task<QuerySnapshot> task);

        void errorGettingUserListFromFirebase(Task<QuerySnapshot> task);

        void updateRecyclerView();
    }
}
