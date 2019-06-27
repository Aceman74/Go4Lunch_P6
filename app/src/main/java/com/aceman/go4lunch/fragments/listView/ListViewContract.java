package com.aceman.go4lunch.fragments.listView;

import com.aceman.go4lunch.data.models.RestaurantPublic;
import com.aceman.go4lunch.utils.BaseView;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

/**
 * Created by Lionel JOFFRAY - on 04/06/2019.
 * <p>
 * ListView Contracts.
 */
public interface ListViewContract {

    interface ListViewPresenterInterface {

        List<RestaurantPublic> getUserList();

        FirebaseUser getCurrentUser();

    }

    interface ListViewViewInterface extends BaseView {
        void loadingView();

        void configureBtn();

        void configureRecyclerView();

        void sortByName();

        void sortByDistance();


    }
}
