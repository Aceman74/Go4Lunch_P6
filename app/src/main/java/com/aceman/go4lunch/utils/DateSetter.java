package com.aceman.go4lunch.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Lionel JOFFRAY - on 05/06/2019.
 * <p>
 * DateSetter is used to set a date with history.
 */
public class DateSetter {

    private static Date todayDate;
    private static SimpleDateFormat formatter;
    private static String date;

    public static String getFormattedDate() {

        todayDate = Calendar.getInstance().getTime();
        Calendar.getInstance().getTime();
        formatter = new SimpleDateFormat("yyyy-MM-dd");

        return date = formatter.format(todayDate);
    }
}
