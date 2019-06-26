package com.aceman.go4lunch.data.models;

/**
 * Created by Lionel JOFFRAY - on 06/06/2019.
 * History is a simple object who contain a date and an History Detail object.
 * Used for User History.
 */
public class History {

    private HistoryDetails mHistoryDetails;
    private String date;

    public History() {
    }

    public History(HistoryDetails mDetails) {
        this.mHistoryDetails = mDetails;
    }

    public HistoryDetails getHistory() {
        return mHistoryDetails;
    }

    public void setHistory(HistoryDetails history) {
        mHistoryDetails = history;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
