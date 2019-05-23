package com.aceman.go4lunch.models;

import com.google.firebase.Timestamp;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

/**
 * Created by Lionel JOFFRAY - on 21/05/2019.
 */
public class RestaurantPublic {

    @Nullable
    private String urlPicture;
    private String username;
    private String uid;
    private String restaurantID;
    private String restaurantName;
    private String like;
    private Date dateCreated;
    private int day;

    public RestaurantPublic() {
    }

    public RestaurantPublic(String username, String urlPicture) {
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

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    @ServerTimestamp
    public Date getDateCreated() { return dateCreated; }

    public void setDateCreated(Date dateCreated) { this.dateCreated = dateCreated; }

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

    public void setUrlPicture(String urlPicture) {
        this.urlPicture = urlPicture;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setRestaurantID(String restaurantID) {
        this.restaurantID = restaurantID;
    }

    public void setLike(String like) {
        this.like = like;
    }
}
