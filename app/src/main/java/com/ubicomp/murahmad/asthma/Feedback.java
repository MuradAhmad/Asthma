package com.ubicomp.murahmad.asthma;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.GpsSatellite;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by muradahmad on 20/08/2018.
 */

public class Feedback extends Fragment  {

    private QuestionLibrary questionLibrary;
    private int feedbackQuestion = 0;


    private TextView txtFeedback;
    private Button btn1, btn2;


    private List<String> qList;
    private List<String> aList;

    Database dbHandler;
    SQLiteDatabase db;
    Cursor cursor;


    private String stringSymptoms;
    private String stringLocation;
    private String stringDeviceData;

    String deviceId;
    String temperature;
    String humidity;


    //location




    Context context;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_feedback, container, false);


        context = this.getContext();

        questionLibrary = new QuestionLibrary(context);


        txtFeedback = (TextView) view.findViewById(R.id.txtFeedback);
        btn1 = (Button) view.findViewById(R.id.btn1);
        btn2 = (Button) view.findViewById(R.id.btn2);


        dbHandler = new Database(getContext());
        db = dbHandler.getWritableDatabase();


        getDeviceData();


        qList = new ArrayList<String>();
        aList = new ArrayList<String>();


        // receiving data from Symptoms class

        stringSymptoms = getArguments().getString("symptoms");


        updateQuestion();

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                aList.add(btn1.getText().toString());


                updateQuestion();


            }
        });


        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                aList.add(btn2.getText().toString());


                updateQuestion();


            }
        });



        return view;
    }


    public void updateQuestion() {


        if (feedbackQuestion < 1) {


            txtFeedback.setText(questionLibrary.getFeedbackQuestion(feedbackQuestion));
            btn1.setText(questionLibrary.getFeedbackOption(0));
            btn2.setText(questionLibrary.getFeedbackOption(1));

            qList.add(txtFeedback.getText().toString());

            feedbackQuestion++;

        } else if (feedbackQuestion < 2) {

            txtFeedback.setText(questionLibrary.getFeedbackQuestion(feedbackQuestion));
            btn1.setText(questionLibrary.getFeedbackOption(2));
            btn2.setText(questionLibrary.getFeedbackOption(3));


            qList.add(txtFeedback.getText().toString());

            feedbackQuestion++;

        } else {

            saveSymptomsFeedback();

            Intent intent = new Intent(getContext(), MainActivity.class);
            startActivity(intent);


        }

    }

    public void saveSymptomsFeedback() {

        JSONArray data = new JSONArray();

        // ruuvitag device data

        final JSONObject ruuvitagJsonObject = new JSONObject();
        try {
            ruuvitagJsonObject.put("Device Id", deviceId);
            ruuvitagJsonObject.put("Temperature", temperature);
            ruuvitagJsonObject.put("Humidity", humidity);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        data.put(ruuvitagJsonObject);



        final JSONObject feedbackJsonObject = new JSONObject();
        for (int index = 0; index < qList.size(); index++) {
            try {
                feedbackJsonObject.put(qList.get(index), aList.get(index));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        data.put(feedbackJsonObject);

        Calendar calendar = Calendar.getInstance();
        Log.d("time feedback:", String.valueOf(calendar.getTimeInMillis()));

        ContentValues values = new ContentValues();
        values.put(Database.SYMPTOMS, data.toString());
        values.put(Database.SYMPTOMS_timestamp, calendar.getTimeInMillis());

        dbHandler.insertSymptomsData(values);

        dbHandler.close();

    }




    public void getDeviceData() {


        cursor = db.rawQuery("SELECT * FROM " + Database.DEVICE_TABLE + " ORDER BY Date desc limit 1", null);


        if (cursor != null) {
            cursor.moveToFirst();

            if (cursor.getCount() > 0) {

                // get values from cursor here

                deviceId = cursor.getString(cursor.getColumnIndex(Database.DEVICE_ID));
                temperature = cursor.getString(cursor.getColumnIndex(Database.TEMPERATURE));
                humidity = cursor.getString(cursor.getColumnIndex(Database.HUMIDITY));


                Log.d("Feedbackdevice", deviceId);
                Log.d("Feedbacktemperature ", temperature);
                Log.d("Feedbackhumidity", humidity);


            }
        }
        cursor.close();


    }


}
