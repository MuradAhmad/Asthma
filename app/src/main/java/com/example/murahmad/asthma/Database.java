
package com.example.murahmad.asthma;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Database extends SQLiteOpenHelper {


    public static final String USER_DATABASE = "database.db";
    public static final String DEVICE_TABLE = "Database_Table";
    public static final String DEVICE_ID = "Id";
    public static final String URL = "Url";
    public static final String RSSI = "Rssi";
    public static final String TEMPERATURE = "Temperature";
    public static final String DATE = "Date";


    public static Database instance;


    public static  Database getInstance(Context context) {
        if (instance == null) {
            instance = new Database(context);
        }
        return instance;
    }


    public Database(Context context) {
        super(context, USER_DATABASE, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + DEVICE_TABLE + "(Id TEXT, Url TEXT, Rssi TEXT, Temperature TEXT, Date TEXT )");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL(" DROP TABLE IF EXISTS " + DEVICE_TABLE);
        onCreate(db);

    }

    public boolean insertDeviceData(DBModel dbModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DEVICE_ID, dbModel.getDeviceId());
        contentValues.put(URL, dbModel.getUrl());
        contentValues.put(RSSI, dbModel.getRssi());
        contentValues.put(TEMPERATURE, dbModel.getTemperature());
        contentValues.put(DATE, dbModel.getDate());


        long result = db.insert(DEVICE_TABLE, null, contentValues);
        if (result == -1) {

            return false;
        } else {
            return true;
        }


    }

    public boolean insertDeviceData(ContentValues contentValues) {
        SQLiteDatabase db = this.getWritableDatabase();

        long result = db.insert(DEVICE_TABLE, null, contentValues);
        if (result == -1) {

            return false;
        } else {
            return true;
        }


    }
    public Cursor viewData(){
        String selectQuery= "SELECT * FROM " + DEVICE_TABLE +" ORDER BY DATE DESC LIMIT 1";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        return cursor;
        // close cursor
        //close database



    }

}

