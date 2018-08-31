package com.example.murahmad.asthma;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import java.util.Calendar;

/**
 * Created by muradahmad on 31/08/2018.
 */

public class Notifications extends Activity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);


        Calendar calendar = Calendar.getInstance();


            Intent intent = new Intent(this, Symptoms.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(),100,intent,PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);


        // AlarmManager.Interval_Day set according to settings screen
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),AlarmManager.INTERVAL_DAY,pendingIntent);

            // Build notification
            // Actions
            Notification notification = new Notification.Builder(this)
                    .setContentTitle("FEEDBACK")
                    .setContentText("Submit daily symptoms")
                    .setWhen(System.currentTimeMillis())
                    .setShowWhen(true)
                    .setContentIntent(pendingIntent)
                    .addAction(R.drawable.ic_notification_24dp, "Call", pendingIntent)
                    .build();
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            // hide the notification after its selected
            notification.flags |= Notification.FLAG_AUTO_CANCEL;

            notificationManager.notify(0, notification);




    }
}
