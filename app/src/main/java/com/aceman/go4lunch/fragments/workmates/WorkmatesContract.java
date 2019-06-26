package com.aceman.go4lunch.fragments.workmates;

import com.aceman.go4lunch.data.models.RestaurantPublic;
import com.aceman.go4lunch.utils.BaseView;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

/**
 * Created by Lionel JOFFRAY - on 04/06/2019.
 * <p>
 * Workmates Contracts.
 */
public interface WorkmatesContract {

    interface WorkmatesPresenterInterface {

        List<RestaurantPublic> getUserList();

        FirebaseUser getCurrentUser();
    }

    interface WorkmatesViewInterface extends BaseView {

        void configureRecyclerView();
    }
}
