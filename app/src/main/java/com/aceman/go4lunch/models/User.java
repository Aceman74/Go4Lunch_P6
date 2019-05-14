package com.aceman.go4lunch.models;

import com.google.firebase.database.annotations.Nullable;

/**
 * Created by Lionel JOFFRAY - on 02/05/2019.
 */
public class User {

    private String uid;
    private String username;
    private Boolean isPrivate;
    private String mRestaurant;
    @Nullable
    private String urlPicture;

    public User() {
    }

    public User(String uid, String username, String urlPicture) {
        this.uid = uid;
        this.username = username;
        this.urlPicture = urlPicture;
        this.isPrivate = false;
    }

    // --- GETTERS ---
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    // --- SETTERS ---
    public void setUsername(String username) {
        this.username = username;
    }

    public String getUrlPicture() {
        return urlPicture;
    }

    public void setUrlPicture(String urlPicture) {
        this.urlPicture = urlPicture;
    }

    public Boolean getIsPrivate() {
        return isPrivate;
    }

    public void setIsPrivate(Boolean mentor) {
        isPrivate = mentor;

    }    public String getRestaurant() {
        return mRestaurant;
    }

    public void setRestaurant(String restaurant) {
        mRestaurant = restaurant;
    }
}
