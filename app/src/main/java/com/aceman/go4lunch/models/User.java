package com.aceman.go4lunch.models;

import com.google.firebase.database.annotations.Nullable;

/**
 * Created by Lionel JOFFRAY - on 02/05/2019.
 */
public class User {

    private String uid;
    private String username;
    private Boolean isPrivate;
    @Nullable
    private String urlPicture;

    public User() { }

    public User(String uid, String username, String urlPicture) {
        this.uid = uid;
        this.username = username;
        this.urlPicture = urlPicture;
        this.isPrivate = false;
    }

    // --- GETTERS ---
    public String getUid() { return uid; }
    public String getUsername() { return username; }
    public String getUrlPicture() { return urlPicture; }
    public Boolean getIsPrivate() { return isPrivate; }

    // --- SETTERS ---
    public void setUsername(String username) { this.username = username; }
    public void setUid(String uid) { this.uid = uid; }
    public void setUrlPicture(String urlPicture) { this.urlPicture = urlPicture; }
    public void setIsPrivate(Boolean mentor) { isPrivate = mentor; }
}
