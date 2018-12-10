package com.ubicomp.murahmad.asthma;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;

/**
 * Created by muradahmad on 06/09/2018.
 */

public class App extends Application {

    public static final String CHANNEL_1_ID = "channel1";
    public static final String CHANNEL_2_ID = "channel2";


    Database handler;
    SQLiteDatabase db;
    Cursor cursor;
    private String userName, password;

    @Override
    public void onCreate() {
        super.onCreate();


        handler = new Database(this);
        db = handler.getReadableDatabase();

        createNotificationChannels();

        cursor = db.rawQuery("SELECT * FROM " + Database.REGISTRATION_TABLE, null);

        if (cursor != null) {
            cursor.moveToFirst();

            if (cursor.getCount() > 0) {
// get values from cursor here

                userName = cursor.getString(cursor.getColumnIndex(Database.USERNAME));
                password = cursor.getString(cursor.getColumnIndex(Database.PASSWORD));


            }

        }
        cursor.close();

        if (userName != null && password != null) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

        } else {
            Intent intent = new Intent(this, Login.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    private void createNotificationChannels(){

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){

            NotificationChannel channel1 = new NotificationChannel(CHANNEL_1_ID,"Channel 1",
                    NotificationManager.IMPORTANCE_HIGH);
            channel1.setDescription("This is Channel 1");


            NotificationChannel channel2 = new NotificationChannel(CHANNEL_2_ID,"Channel 2",
                    NotificationManager.IMPORTANCE_HIGH);
            channel2.setDescription("This is Channel 2");

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel1);
            notificationManager.createNotificationChannel(channel2);

        }



    }
}
