package com.aceman.go4lunch.models;

/**
 * Created by Lionel JOFFRAY - on 06/06/2019.
 */
public class History {

    private String name;
    private String date;

    public History(){
    }

    public History(String name, String date){
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
