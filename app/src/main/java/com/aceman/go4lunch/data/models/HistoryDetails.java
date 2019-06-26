package com.aceman.go4lunch.data.models;

/**
 * Created by Lionel JOFFRAY - on 10/06/2019.
 * History is a simple object with a name (restaurant) and a date.
 * Used for User History.
 */
public class HistoryDetails {

    private String name;
    private String date;

    public HistoryDetails() {
    }

    public HistoryDetails(String name, String date) {
        this.name = name;
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
