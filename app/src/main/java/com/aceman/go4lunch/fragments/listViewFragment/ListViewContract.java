package com.aceman.go4lunch.fragments.listViewFragment;

import com.aceman.go4lunch.utils.BaseView;
import com.aceman.go4lunch.utils.events.RefreshEvent;
import com.aceman.go4lunch.utils.events.ResultListEvent;
import com.aceman.go4lunch.utils.events.UserListEvent;

/**
 * Created by Lionel JOFFRAY - on 04/06/2019.
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
