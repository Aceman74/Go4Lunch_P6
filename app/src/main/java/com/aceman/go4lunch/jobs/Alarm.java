package com.aceman.go4lunch.jobs;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.aceman.go4lunch.models.RestaurantPublic;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import timber.log.Timber;

import static android.content.Context.ALARM_SERVICE;

/**
 * Created by Lionel JOFFRAY - on 07/06/2019.
 */
public class Alarm extends BroadcastReceiver {

    public static List<RestaurantPublic> mUserList = new ArrayList<>();
    public AlarmManager alarmMgr;
    PendingIntent alarmKillIntent;
    PendingIntent alarmCreateIntent;
    int ALARM_REQUEST_CODE = 123;
    Intent mIntent;

    @Override
    public void onReceive(Context context, Intent intent) {
        createNotification();
    }

    private void createNotification() {
        OneTimeWorkRequest notifRequestDay = new OneTimeWorkRequest.Builder(DailyWorker.class)
                .addTag("RequestDaliy")
                .build();
        WorkManager.getInstance().enqueue(notifRequestDay);
    }

    public boolean checkIfAlarmIsSet(Context context) {

        mIntent = new Intent(context, Alarm.class);
        boolean alarmUp = (PendingIntent.getBroadcast(context, ALARM_REQUEST_CODE,
                mIntent,
                PendingIntent.FLAG_NO_CREATE) != null);

        if (alarmUp) {
            Timber.d("Alarm is already active");
        }
        return alarmUp;
    }

    public void setAlarm(Context context) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 12);

        mIntent = new Intent(context, Alarm.class);
        alarmCreateIntent = PendingIntent.getBroadcast(
                context, ALARM_REQUEST_CODE, mIntent, 0);

        alarmMgr = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        alarmMgr.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime(),
                2 * 60 * 1000,
                alarmCreateIntent);
        Timber.tag("Alarm Created").e(alarmCreateIntent.toString());

        // alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
        //         AlarmManager.INTERVAL_DAY, alarmIntent);
    }

    public void cancelAlarm(Context context) {
        try {
            mIntent = new Intent(context, Alarm.class);
            alarmKillIntent = PendingIntent.getBroadcast(context, ALARM_REQUEST_CODE, mIntent, 0);
            alarmMgr = (AlarmManager) context.getSystemService(ALARM_SERVICE);
            alarmMgr.cancel(alarmKillIntent);
            alarmKillIntent.cancel();
            Timber.tag("Alarm Disabled").e(alarmKillIntent.toString());
        } catch (Error e) {
            Timber.tag("Alarm Error disabling").e(alarmKillIntent.toString());
        }
    }
}