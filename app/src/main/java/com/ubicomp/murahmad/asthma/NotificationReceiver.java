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


    NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);




    //
        Intent activityIntent = new Intent(context, MainActivity.class);
        activityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent contentIntent = PendingIntent.getActivity(context,100,activityIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        // Build notification
            // Actions
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context,App.CHANNEL_1_ID)
                    .setContentIntent(contentIntent)
                    .setSmallIcon(R.drawable.ic_notification_24dp)
                    //.addAction(R.drawable.ic_notification_24dp, "Call", pendingIntent)
                    .setContentTitle("FEEDBACK")
                    .setContentText("Submit daily symptoms")
                    .setShowWhen(true)
                    .setAutoCancel(true)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setCategory(NotificationCompat.CATEGORY_MESSAGE);

            // hide the notification after its selected
           // notification.flags |= Notification.FLAG_AUTO_CANCEL;

            notificationManager.notify(100, mBuilder.build());

/*

        PendingIntent contentIntent1 = PendingIntent.getActivity(context,101,activityIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        // Build notification
        // Actions
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,App.CHANNEL_1_ID)
                .setContentIntent(contentIntent1)
                .setSmallIcon(R.drawable.ic_notification_24dp)
                //.addAction(R.drawable.ic_notification_24dp, "Call", pendingIntent)
                .setContentTitle("Evening")
                .setContentText("Submit daily symptoms")
                .setShowWhen(true)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE);

        // hide the notification after its selected
        // notification.flags |= Notification.FLAG_AUTO_CANCEL;

        notificationManager.notify(101, builder.build());
*/


    }

}
