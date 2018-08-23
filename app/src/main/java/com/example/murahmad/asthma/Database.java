
package com.example.murahmad.asthma;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Database extends SQLiteOpenHelper {


    public static final String USER_DATABASE = "database.db";

    // RuuviTag Device Table

    public static final String DEVICE_TABLE = "Device_Table";
    public static final String DEVICE_ID = "Id";
    public static final String URL = "Url";
    public static final String RSSI = "Rssi";
    public static final String TEMPERATURE = "Temperature";
    public static final String HUMIDITY = "Humidity";
    public static final String DATE = "Date";


    // Registration table
    public static final String REGISTRATION_TABLE = "Registration_Table";
    public static final String USERNAME = "Name";
    public static final String DOB = "DateOfBirth";
    public static final String EMAIL = "Email";
    public static final String PASSWORD = "Password";
    public static final String reg_timestamp = "Timestamp";
   // public static final String CONSENT = "Consent";



    // Medication Table


    public static final String MEDICATION_TABLE = "Medication_Table";
    public static final String MED_DATE = "Date";
    public static final String DRUGS = "Drugs";
    public static final String OTHER_DRUGS = "Other_Drugs";
    public static final String NEW_DRUGS = "New_Drugs";
    public static final String DOC_VISITS_ASTHMA = "Asthma_Visits";
    public static final String DOC_VISITS_ALLERGY = "Allergy_Visits";
    public static final String OTHER = "Other";


    // Symptoms table maybe needed


    public static Database instance;


    public static  Database getInstance(Context context) {
        if (instance == null) {
            instance = new Database(context);
        }
        return instance;
    }


    public Database(Context context) {
        super(context, USER_DATABASE, null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + DEVICE_TABLE + "(Id TEXT, Url TEXT, Rssi TEXT, Temperature TEXT, Humidity TEXT, Date TEXT )");
        db.execSQL("create table " + REGISTRATION_TABLE + "(Name TEXT, DateOfBirth TEXT, Email TEXT, Password TEXT , Timestamp REAL)");
        db.execSQL("create table " + MEDICATION_TABLE + "(Datae TEXT, Drugs TEXT, Other_Drugs TEXT, New_Drugs TEXT, Asthma_Visits TEXT, Allergy_Visits TEXT, Other TEXT)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL(" DROP TABLE IF EXISTS " + DEVICE_TABLE);
        db.execSQL(" DROP TABLE IF EXISTS " + REGISTRATION_TABLE);
        db.execSQL(" DROP TABLE IF EXISTS " + MEDICATION_TABLE);

        onCreate(db);

    }

    public boolean insertDeviceData(DBModel dbModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DEVICE_ID, dbModel.getDeviceId());
        contentValues.put(URL, dbModel.getUrl());
        contentValues.put(RSSI, dbModel.getRssi());
        contentValues.put(TEMPERATURE, dbModel.getTemperature());
        contentValues.put(HUMIDITY,dbModel.getHumidity());
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

    public boolean insertRegistrationData(ContentValues contentValues) {
        SQLiteDatabase db = this.getWritableDatabase();

        long result = db.insert(REGISTRATION_TABLE, null, contentValues);
        if (result == -1) {

            return false;
        } else {
            return true;
        }

    }

    public boolean insertMedicationData(ContentValues contentValues) {
        SQLiteDatabase db = this.getWritableDatabase();

        long result = db.insert(MEDICATION_TABLE, null, contentValues);
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

    public Cursor viewRegistrationData(){
        String selectQuery= "SELECT * FROM " + REGISTRATION_TABLE;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        return cursor;
        // close cursor
        //close database


    }

    public Cursor viewMedicationData(){
        String selectQuery= "SELECT * FROM " + MEDICATION_TABLE;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        return cursor;
        // close cursor
        //close database


    }


}

