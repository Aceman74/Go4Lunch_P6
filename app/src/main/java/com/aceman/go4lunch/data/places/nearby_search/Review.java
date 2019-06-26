package com.aceman.go4lunch.data.places.nearby_search;

import com.google.gson.annotations.SerializedName;

public class Review {

    @SerializedName("author_name")
    private String mAuthorName;
    @SerializedName("author_url")
    private String mAuthorUrl;
    @SerializedName("language")
    private String mLanguage;
    @SerializedName("profile_photo_url")
    private String mProfilePhotoUrl;
    @SerializedName("rating")
    private Long mRating;
    @SerializedName("relative_time_description")
    private String mRelativeTimeDescription;
    @SerializedName("text")
    private String mText;
    @SerializedName("time")
    private Long mTime;

    public String getAuthorName() {
        return mAuthorName;
    }

    public void setAuthorName(String authorName) {
        mAuthorName = authorName;
    }

    public String getAuthorUrl() {
        return mAuthorUrl;
    }

    public void setAuthorUrl(String authorUrl) {
        mAuthorUrl = authorUrl;
    }

    public String getLanguage() {
        return mLanguage;
    }

    public void setLanguage(String language) {
        mLanguage = language;
    }

    public String getProfilePhotoUrl() {
        return mProfilePhotoUrl;
    }

    public void setProfilePhotoUrl(String profilePhotoUrl) {
        mProfilePhotoUrl = profilePhotoUrl;
    }

    public Long getRating() {
        return mRating;
    }

    public void setRating(Long rating) {
        mRating = rating;
    }

    public String getRelativeTimeDescription() {
        return mRelativeTimeDescription;
    }

    public void setRelativeTimeDescription(String relativeTimeDescription) {
        mRelativeTimeDescription = relativeTimeDescription;
    }

    public String getText() {
        return mText;
    }

    public void setText(String text) {
        mText = text;
    }

    public Long getTime() {
        return mTime;
    }

    public void setTime(Long time) {
        mTime = time;
    }

}
