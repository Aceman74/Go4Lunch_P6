package com.aceman.go4lunch.fragments.workmatesFragment;

import com.aceman.go4lunch.utils.BaseView;

/**
 * Created by Lionel JOFFRAY - on 04/06/2019.
 */
public interface WorkmatesContract {

    interface WorkmatesPresenterInterface{

    }

    interface WorkmatesViewInterface extends BaseView {

        void configureRecyclerView();
    }
}
