package com.aceman.go4lunch.data.models;

import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

/**
 * Created by Lionel JOFFRAY - on 21/05/2019.
 * RestaurantPublic is a restaurant object to save for user data (public).
 * Used for multiple class.
 */
public class RestaurantPublic {

    @Nullable
    private String urlPicture;
    private String username;
    private String restaurantID;
    private String restaurantName;
    private String like;
    private Date dateCreated;
    private String date;
    private Details mDetails;
    private History mHistory;

    public RestaurantPublic() {
    }

    public RestaurantPublic(String username, String urlPicture) {
        this.username = username;
        this.urlPicture = urlPicture;
    }

    public Details getDetails() {
        return mDetails;
    }

    public void setDetails(Details details) {
        mDetails = details;
    }

    public String getUrlPicture() {
        return urlPicture;
    }

    public void setUrlPicture(String urlPicture) {
        this.urlPicture = urlPicture;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRestaurantID() {
        return restaurantID;
    }

    public void setRestaurantID(String restaurantID) {
        this.restaurantID = restaurantID;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @ServerTimestamp
    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getLike() {
        return like;
    }

    public void setLike(String like) {
        this.like = like;
    }

    public History getHistory() {
        return mHistory;
    }

    public void setHistory(History history) {
        mHistory = history;
    }
}
