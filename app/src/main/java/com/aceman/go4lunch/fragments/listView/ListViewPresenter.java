package com.aceman.go4lunch.fragments.listView;

import com.aceman.go4lunch.data.models.RestaurantPublic;
import com.aceman.go4lunch.utils.BasePresenter;
import com.aceman.go4lunch.utils.FirestoreUserList;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lionel JOFFRAY - on 04/06/2019.
 * <p>
 * List View Presenter.
 */
public class ListViewPresenter extends BasePresenter implements ListViewContract.ListViewPresenterInterface {
    public static List<RestaurantPublic> mUserList = new ArrayList<>();

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
     * get the current user
     *
     * @return current user
     */
    @Override
    public FirebaseUser getCurrentUser() {

        return FirebaseAuth.getInstance().getCurrentUser();
    }
}
