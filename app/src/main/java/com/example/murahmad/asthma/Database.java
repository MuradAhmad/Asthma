
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
   // public static final String PATIENT_NAME = "Name";
    public static final String MED_DATE = "Date";
    public static final String DRUGS = "Drugs";
    public static final String OTHER_DRUGS = "Other_Drugs";
    public static final String NEW_DRUGS = "New_Drugs";
    public static final String DOC_VISITS_ASTHMA = "Asthma_Visits";
    public static final String DOC_VISITS_ALLERGY = "Allergy_Visits";
    public static final String OTHER = "Other";


    // Symptoms table

    public static final String SYMPTOMS_TABLE = "Symptoms_Table";
    public static final String SYMPTOMS_timestamp = "Timestamp";
    public static final String Q1 = "Q1";
    public static final String A1 = "A1";
/*    public static final String Q2 = "Q2";
    public static final String A2 = "A2";
    public static final String Q3 = "Q3";
    public static final String A3 = "A3";
    public static final String Q4 = "Q4";
    public static final String A4 = "A4";
    public static final String Q5 = "Q5";
    public static final String A5 = "A5";
    public static final String Q6 = "Q6";
    public static final String A6 = "A6";
    public static final String Q7 = "Q7";
    public static final String A7 = "A7";*/



    // Feedback table
    public static final String FEEDBACK_TABLE = "Feedback_Table";
    public static final String FEEDBACK_timestamp = "Timestamp";
    public static final String FQ1 = "FQ1";
    public static final String FA1 = "FA1";
    public static final String FQ2 = "FQ2";
    public static final String FA2 = "FA2";


    public static Database instance;


    public static  Database getInstance(Context context) {
        if (instance == null) {
            instance = new Database(context);
        }
        return instance;
    }


    public Database(Context context) {
        super(context, USER_DATABASE, null, 5);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + DEVICE_TABLE + "(Id TEXT, Url TEXT, Rssi TEXT, Temperature TEXT, Humidity TEXT, Date TEXT )");
        db.execSQL("create table " + REGISTRATION_TABLE + "(Name TEXT, DateOfBirth TEXT, Email TEXT, Password TEXT , Timestamp REAL)");
        db.execSQL("create table " + MEDICATION_TABLE + "(Date TEXT, Drugs TEXT, Other_Drugs TEXT, New_Drugs TEXT, Asthma_Visits TEXT, Allergy_Visits TEXT, Other TEXT)");
        //db.execSQL("create table " + SYMPTOMS_TABLE + "(Q1 TEXT, A1 TEXT, Q2 TEXT, A2 TEXT ,Q3 TEXT, A3 TEXT,Q4 TEXT, A4 TEXT,Q5 TEXT, A5 TEXT,Q6 TEXT, A6 TEXT,Q7 TEXT, A7 TEXT, Timestamp REAL)");

        db.execSQL("create table " + SYMPTOMS_TABLE + "(Q1 TEXT, A1 TEXT, Timestamp REAL)");


        db.execSQL("create table " + FEEDBACK_TABLE + "(FQ1 TEXT, FA1 TEXT, FQ2 TEXT, FA2 TEXT , Timestamp REAL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL(" DROP TABLE IF EXISTS " + DEVICE_TABLE);
        db.execSQL(" DROP TABLE IF EXISTS " + REGISTRATION_TABLE);
        db.execSQL(" DROP TABLE IF EXISTS " + MEDICATION_TABLE);
        db.execSQL(" DROP TABLE IF EXISTS " + SYMPTOMS_TABLE);
        db.execSQL(" DROP TABLE IF EXISTS " + FEEDBACK_TABLE);

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
    public boolean insertSymptomsData(ContentValues contentValues) {
        SQLiteDatabase db = this.getWritableDatabase();

        long result = db.insert(SYMPTOMS_TABLE, null, contentValues);
        if (result == -1) {

            return false;
        } else {
            return true;
        }

    }
    public boolean insertFeedbackData(ContentValues contentValues) {
        SQLiteDatabase db = this.getWritableDatabase();

        long result = db.insert(FEEDBACK_TABLE, null, contentValues);
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

    public Cursor viewSymptomsData(){
        String selectQuery= "SELECT * FROM " + SYMPTOMS_TABLE;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        return cursor;
        // close cursor
        //close database


    }

    public Cursor viewFeedbackData(){
        String selectQuery= "SELECT * FROM " + FEEDBACK_TABLE;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        return cursor;
        // close cursor
        //close database


    }


}

