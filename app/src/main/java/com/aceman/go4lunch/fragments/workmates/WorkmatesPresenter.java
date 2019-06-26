package com.aceman.go4lunch.fragments.workmates;

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
 * Workmates Presenter.
 */
public class WorkmatesPresenter extends BasePresenter implements WorkmatesContract.WorkmatesPresenterInterface {
    public static List<RestaurantPublic> mUserList = new ArrayList<>();

    /**
     * Get the userlist from Firebase.
     */
    @Override
    public List<RestaurantPublic> getUserList() {

        mUserList = FirestoreUserList.getUserList(getCurrentUser());
        return mUserList;
    }

    /**
     * Get current user.
     *
     * @return user
     */
    public FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }
}
