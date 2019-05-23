package com.aceman.go4lunch.jobs;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import androidx.work.Worker;
import androidx.work.WorkerParameters;


import com.aceman.go4lunch.R;

import java.util.Calendar;

import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import timber.log.Timber;

import static android.app.PendingIntent.FLAG_CANCEL_CURRENT;

public class DailyWorker extends Worker {
    private final String mSearchQuery = getInputData().getString("Query");
    private final String mCategorie = getInputData().getString("Categorie");
    private final Context mContext;

    public DailyWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        mContext = context;
    }

    /**
     * Worker execution
     *
     * @return result of job
     */
    @NonNull
    @Override
    public Result doWork() {

        try {
            clear();

            return
                    Result.success();
        } catch (Exception e) {
            return
                    Result.failure();
        }
    }


    private void clear() {

/**
                Intent intent = new Intent(mContext, NotificationActivity.class);
                intent.putExtra("Search", mSearchQuery);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, intent, FLAG_CANCEL_CURRENT);

                Bitmap largeIcon = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.newyorktimes_thumb);

                NotificationCompat.Builder notifTest = new NotificationCompat.Builder(mContext, CHANNEL_ID)
                        .setSmallIcon(R.drawable.notification)
                        .setContentTitle(mAppTitle)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(mNeg1 + mSearchQuery + mNeg2))
                        .setContentIntent(pendingIntent)
                        .setLargeIcon(largeIcon)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setAutoCancel(true)
                        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

                NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(mContext);
                notificationManagerCompat.notify(2, notifTest.build());


 */
            }

}
