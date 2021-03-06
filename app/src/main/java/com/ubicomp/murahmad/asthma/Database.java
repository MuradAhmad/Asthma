
package com.ubicomp.murahmad.asthma;

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

    // Settings  Table

    public static final String SETTING_TABLE = "Setting_Table";
    public static final String MORNING_TIME = "MorningTime";
    public static final String EVENING_TIME = "EveningTime";
    public static final String LOGIN_TOKEN = "LoginToken";
    public static final String Setting_timestamp = "Timestamp";

    // Registration table
    public static final String REGISTRATION_TABLE = "Registration_Table";
    public static final String USERNAME = "Name";
    public static final String DOB = "DateOfBirth";
    public static final String EMAIL = "Email";
    public static final String PASSWORD = "Password";
    public static final String reg_timestamp = "Timestamp";
    public static final String UUID = "Uuid";
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
    public static final String SYMPTOMS = "Symptoms";


    // Location Table

    public static final String LOCATION_TABLE = "Location_Table";
    public static final String LOCATION_timestamp = "Timestamp";
    public static final String LOCATION = "Location";


    public static Database instance;


    public static Database getInstance(Context context) {
        if (instance == null) {
            instance = new Database(context);
        }
        return instance;
    }


    public Database(Context context) {
        super(context, USER_DATABASE, null, 10);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + DEVICE_TABLE + "(Id TEXT, Url TEXT, Rssi TEXT, Temperature TEXT, Humidity TEXT, Date REAL)");
        db.execSQL("create table " + REGISTRATION_TABLE + "(Name TEXT, DateOfBirth TEXT, Email TEXT, Password TEXT ,Uuid TEXT, Timestamp REAL)");
        db.execSQL("create table " + MEDICATION_TABLE + "(Date REAL, Drugs TEXT, Other_Drugs TEXT, New_Drugs TEXT, Asthma_Visits TEXT, Allergy_Visits TEXT, Other TEXT)");
        db.execSQL("create table " + SYMPTOMS_TABLE + "(Symptoms TEXT, Timestamp REAL)");
        db.execSQL("create table " + LOCATION_TABLE + "(Location TEXT, Timestamp REAL)");
        db.execSQL("create table " + SETTING_TABLE + "(MorningTime TEXT, EveningTime TEXT, LoginToken TEXT, Timestamp REAL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(" DROP TABLE IF EXISTS " + DEVICE_TABLE);
        db.execSQL(" DROP TABLE IF EXISTS " + REGISTRATION_TABLE);
        db.execSQL(" DROP TABLE IF EXISTS " + MEDICATION_TABLE);
        db.execSQL(" DROP TABLE IF EXISTS " + SYMPTOMS_TABLE);
        db.execSQL(" DROP TABLE IF EXISTS " + LOCATION_TABLE);
        db.execSQL(" DROP TABLE IF EXISTS " + SETTING_TABLE);
        onCreate(db);
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


    public boolean insertLocationData(ContentValues contentValues) {
        SQLiteDatabase db = this.getWritableDatabase();

        long result = db.insert(LOCATION_TABLE, null, contentValues);
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }


    public boolean insertSettingData(ContentValues contentValues) {
        SQLiteDatabase db = this.getWritableDatabase();

        long result = db.insert(SETTING_TABLE, null, contentValues);
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }
}

