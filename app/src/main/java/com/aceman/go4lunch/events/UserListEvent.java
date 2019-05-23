package com.aceman.go4lunch.events;

import com.aceman.go4lunch.models.RestaurantPublic;

import java.util.List;

/**
 * Created by Lionel JOFFRAY - on 17/05/2019.
 */
public class UserListEvent {

    public List<RestaurantPublic> mUserList;

    public UserListEvent(List<RestaurantPublic> userlist) {
        mUserList = userlist;
    }
}
