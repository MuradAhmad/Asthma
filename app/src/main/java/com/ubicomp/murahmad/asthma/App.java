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
 * Last modification:
 * 12.12.2018 Denzil: Code clean-up, remove redundant global variables
 */
public class App extends Application {

    static final String CHANNEL_1_ID = "extrema";

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannels();

        Database handler = new Database(this);
        SQLiteDatabase db = handler.getReadableDatabase();

        String userName = null, password = null;

        Cursor cursor = db.query(Database.REGISTRATION_TABLE, null, null, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            userName = cursor.getString(cursor.getColumnIndex(Database.USERNAME));
            password = cursor.getString(cursor.getColumnIndex(Database.PASSWORD));
            cursor.close();
        }

        /**
         * @Murad: TODO: missing actual username and password validation. Right now, only checks whether there is a user and password on the database...
         */
        if (userName != null && password != null && userName.length() > 0 && password.length() > 0) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, Login.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel1 = new NotificationChannel(CHANNEL_1_ID, getString(R.string.app_name), NotificationManager.IMPORTANCE_HIGH);
            channel1.setDescription(getString(R.string.app_name));
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel1);
        }
    }
}
