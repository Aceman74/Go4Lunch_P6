package com.aceman.go4lunch.models;

import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

/**
 * Created by Lionel JOFFRAY - on 23/05/2019.
 */
public class Restaurant {
    private String name;
    private String address;
    private String phone;
    private String website;
    private String placeID;
    private int rating;
    private String imageUrl;

    public Restaurant() {
    }

    public Restaurant(String name, String address, String phone, String website, String placeID, int rating, String imageUrl) {
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.website = website;
        this.placeID = placeID;
        this.rating = rating;
        this.imageUrl = imageUrl;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getPlaceID() {
        return placeID;
    }

    public void setPlaceID(String placeID) {
        this.placeID = placeID;
    }

    public int getRating() {
        return rating;
    }
    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
