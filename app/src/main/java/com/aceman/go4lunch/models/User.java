package com.aceman.go4lunch.models;

import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

/**
 * Created by Lionel JOFFRAY - on 02/05/2019.
 */
public class User {

    private String uid;
    private String username;
    private Boolean isPrivate;
    private String mRestaurant;
    private String mLike;
    private String email;
    @Nullable
    private String urlPicture;
    private Date dateCreated;
    private String restaurantName;

    public User() {
    }

    public User(String uid, String username, String urlPicture, String email) {
        this.uid = uid;
        this.username = username;
        this.urlPicture = urlPicture;
        this.isPrivate = false;
        this.email = email;
    }

    // --- GETTERS ---

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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUrlPicture() {
        return urlPicture;
    }

    public void setUrlPicture(String urlPicture) {
        this.urlPicture = urlPicture;
    }

    @ServerTimestamp
    public Date getDateCreated() { return dateCreated; }

    public void setDateCreated(Date dateCreated) { this.dateCreated = dateCreated; }
    public Boolean getIsPrivate() {
        return isPrivate;
    }

    public void setIsPrivate(Boolean mentor) {
        isPrivate = mentor;

    }

    public String getRestaurant() {
        return mRestaurant;
    }

    public void setRestaurant(String restaurant) {
        mRestaurant = restaurant;
    }

    public String getLike() {
        return mLike;
    }

    public void setLike(String like) {
        mLike = like;
    }
}
