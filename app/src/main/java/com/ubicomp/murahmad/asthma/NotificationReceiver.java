package com.ubicomp.murahmad.asthma;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

/**
 * Created by muradahmad on 31/08/2018.
 */

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent activityIntent = new Intent(context, MainActivity.class);
        activityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent contentIntent = PendingIntent.getActivity(context, 100, activityIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, App.CHANNEL_1_ID)
                .setContentIntent(contentIntent)
                .setSmallIcon(R.drawable.ic_notification_24dp)
                .setContentTitle(context.getResources().getString(R.string.notification_title_daily_symptoms))
                .setContentText(context.getResources().getString(R.string.notification_action_daily_symptoms))
                .setShowWhen(true)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE);

        notificationManager.notify(100, mBuilder.build());
    }
}
