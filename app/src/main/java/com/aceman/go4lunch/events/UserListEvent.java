package com.aceman.go4lunch.events;

import com.aceman.go4lunch.data.nearby_search.Result;
import com.aceman.go4lunch.models.UserPublic;

import java.util.List;

/**
 * Created by Lionel JOFFRAY - on 17/05/2019.
 */
public class UserListEvent {

    public List<UserPublic> mUserList;

    public UserListEvent(List<UserPublic> userlist) {
        mUserList = userlist;
    }
}
