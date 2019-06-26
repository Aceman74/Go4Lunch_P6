package com.aceman.go4lunch.fragments.listView;

import com.aceman.go4lunch.utils.BaseView;

/**
 * Created by Lionel JOFFRAY - on 04/06/2019.
 * <p>
 * ListView Contracts.
 */
public interface ListViewContract {

    interface ListViewPresenterInterface {

    }

    interface ListViewViewInterface extends BaseView {
        void loadingView();

        void configureBtn();

        void configureRecyclerView();

        void sortByName();

        void sortByDistance();


    }
}
