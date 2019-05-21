package com.aceman.go4lunch.models;

import com.google.firebase.database.annotations.Nullable;

/**
 * Created by Lionel JOFFRAY - on 21/05/2019.
 */
public class UserPublic {

    @Nullable
    private String urlPicture;
    private String username;
    private String uid;
    private String restaurantID;
    private String restaurantName;
    private String like;

    public UserPublic() {
    }

    public UserPublic(String username, String urlPicture) {
        this.username = username;
        this.urlPicture = urlPicture;
    }

    public String getUrlPicture() {
        return urlPicture;
    }

    public String getUsername() {
        return username;
    }

    public String getRestaurantID() {
        return restaurantID;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getLike() {
        return like;
    }

}
