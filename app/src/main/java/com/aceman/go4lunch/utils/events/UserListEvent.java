package com.aceman.go4lunch.utils.events;

import com.aceman.go4lunch.data.models.RestaurantPublic;

import java.util.List;

/**
 * Created by Lionel JOFFRAY - on 17/05/2019.
 * <p>
 * Event to call a RestaurantPublic object.
 *
 * @see com.aceman.go4lunch.activities.core.CoreActivity
 * @see com.aceman.go4lunch.fragments.maps.MapsFragment
 * @see com.aceman.go4lunch.activities.placesDetail.PlacesDetailActivity
 * @see com.aceman.go4lunch.fragments.listView.ListViewFragment
 */
public class UserListEvent {

    public List<RestaurantPublic> mUserList;

    public UserListEvent(List<RestaurantPublic> userlist) {
        mUserList = userlist;
    }
}
