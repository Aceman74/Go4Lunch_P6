package com.aceman.go4lunch.jobs;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.aceman.go4lunch.R;
import com.aceman.go4lunch.activities.login.MainActivity;
import com.aceman.go4lunch.api.RestaurantPublicHelper;
import com.aceman.go4lunch.data.models.RestaurantPublic;
import com.aceman.go4lunch.utils.DateSetter;
import com.aceman.go4lunch.utils.FirestoreUserList;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import timber.log.Timber;

import static android.app.PendingIntent.FLAG_CANCEL_CURRENT;

/**
 * Created by Lionel JOFFRAY - on 07/06/2019.
 * DailyWorker class is used to collect the data to set the notification to user at 12PM.
 * It check if another workmates lunch at the same place and send a notification.
 *
 * @see Alarm
 */
public class DailyWorker extends Worker {
    private final Context mContext;
    public List<RestaurantPublic> mUserList = new ArrayList<>();
    int j = 0;
    private String CHANNEL_ID = "22";
    private String mCoWorker_1 = "";
    private String mCoWorker_2 = "";
    private String mCoWorker_3 = "";
    private String mRestaurant;
    private String mAppTitle = "Go4Lunch: it's lunch time !";
    private String mText1 = "You lunch at ";
    private String mText2 = " and you're not alone ! ";
    private String mText3 = " joinnig you !";
    private String mNoRestaurant = "You haven't selected a restaurant today! ";
    private NotificationCompat.Builder mNotification;

    public DailyWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        mContext = context;
    }

    /**
     * Worker execution.
     *
     * @return result of job
     */
    @NonNull
    @Override
    public Result doWork() {

        try {
            createNotificationChannel();
            getDataFromFirebasae();
            return
                    Result.success();
        } catch (Exception e) {
            Timber.tag("Alarm User Lunch").e(e);
            return
                    Result.failure();
        }
    }

    /**
     * Get current user for Firestore.
     *
     * @return user
     */
    @Nullable
    protected FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    /**
     * Get the data and adapt the notification message.
     */
    private void getDataFromFirebasae() {
        RestaurantPublicHelper.getUser(Objects.requireNonNull(getCurrentUser()).getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                RestaurantPublic currentUser = documentSnapshot.toObject(RestaurantPublic.class);
                if (currentUser.getDetails() != null && currentUser.getDate().equals(DateSetter.getFormattedDate())) {
                    mRestaurant = currentUser.getRestaurantName();
                    Timber.tag("Alarm User Lunch").e(mRestaurant);
                    mUserList = FirestoreUserList.getUserList(getCurrentUser());
                    for (int i = 0; i < mUserList.size(); i++) {
                        RestaurantPublic user = mUserList.get(i);
                        if (user.getRestaurantName() != null && user.getRestaurantName().equals(mRestaurant) && !user.getUsername().equals(currentUser.getUsername())
                                && user.getDate().equals(DateSetter.getFormattedDate())) {
                            switch (j) {
                                case 0:
                                    mCoWorker_1 = user.getUsername();
                                    break;
                                case 1:
                                    mCoWorker_2 = user.getUsername();
                                    break;
                                case 2:
                                    mCoWorker_3 = user.getUsername();
                                    break;
                            }
                            j++;
                        }
                    }
                }else{
                    mRestaurant = null;
                }
                SendNotification();
            }
        });
    }

    /**
     * Notification channel creation.
     */
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = mContext.getString(R.string.channel_name);
            String description = mContext.getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = mContext.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
            Timber.tag("Notification_Channel").i("Is created");
        }
    }

    /**
     * Notify the user with all information.
     */
    private void SendNotification() {

        Intent intent = new Intent(mContext, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, intent, FLAG_CANCEL_CURRENT);

        Bitmap largeIcon = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.logo);

        if (mCoWorker_1.equals("") && mCoWorker_2.equals("") && mCoWorker_3.equals("")) {
            mNotification = new NotificationCompat.Builder(mContext, CHANNEL_ID)
                    .setSmallIcon(R.drawable.logo)
                    .setContentTitle(mAppTitle)
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText(mText1 + mRestaurant))
                    .setContentIntent(pendingIntent)
                    .setLargeIcon(largeIcon)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
        }
        if (mRestaurant == null) {
            mNotification = new NotificationCompat.Builder(mContext, CHANNEL_ID)
                    .setSmallIcon(R.drawable.logo)
                    .setContentTitle(mAppTitle)
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText(mNoRestaurant))
                    .setContentIntent(pendingIntent)
                    .setLargeIcon(largeIcon)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
        } else {
            mNotification = new NotificationCompat.Builder(mContext, CHANNEL_ID)
                    .setSmallIcon(R.drawable.logo)
                    .setContentTitle(mAppTitle)
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText(mText1 + mRestaurant + mText2 + mCoWorker_1 + " " + mCoWorker_2 + " " + mCoWorker_3 + mText3))
                    .setContentIntent(pendingIntent)
                    .setLargeIcon(largeIcon)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
        }

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(mContext);
        notificationManagerCompat.notify(2, mNotification.build());

    }

}
