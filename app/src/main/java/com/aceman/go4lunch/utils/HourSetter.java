package com.aceman.go4lunch.utils;

import java.util.Calendar;

/**
 * Created by Lionel JOFFRAY - on 05/06/2019.
 */
public class HourSetter {

    private static int actualHour;

    public static int getHour(){

    Calendar hour = Calendar.getInstance();
    actualHour = hour.get(Calendar.HOUR_OF_DAY); // return the hour in 24 hrs format (ranging from 0-23)

        return actualHour;
    }
}
