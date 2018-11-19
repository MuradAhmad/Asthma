package com.example.murahmad.asthma;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by muradahmad on 20/08/2018.
 */

public class Feedback extends Fragment  {

    private QuestionLibrary questionLibrary = new QuestionLibrary();
    private int feedbackQuestion = 0;


    private TextView txtFeedback;
    private Button btn1,btn2;


    private List<String> qList;
    private List<String> aList;

    Database dbHandler;
    SQLiteDatabase db;
    Cursor cursor;


    private String stringSymptoms;

    //location

    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;

    private FusedLocationProviderApi locationProvider = LocationServices.FusedLocationApi;
    private GoogleApiClient googleApiClient;


    private Double myLatitude;
    private Double myLongitude;

    private static final int MY_PERMISSION_REQUEST_FINE_LOCATION = 101;
    private static final int MY_PERMISSION_REQUEST_COARSE_LOCATION = 102;
    private boolean permissionIsGranted = false;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_feedback, container, false);



        txtFeedback = (TextView)view.findViewById(R.id.txtFeedback);
        btn1 = (Button)view.findViewById(R.id.btn1);
        btn2 = (Button)view.findViewById(R.id.btn2);


        dbHandler = new Database(getContext());
        db = dbHandler.getWritableDatabase();




        cursor = db.rawQuery("SELECT * FROM " + Database.DEVICE_TABLE + "ORDER BY Timestamp desc limit 1", null);


        if (cursor != null) {
            cursor.moveToFirst();

            if (cursor.getCount() > 0) {

                // get values from cursor here

                String deviceId = cursor.getString(cursor.getColumnIndex(Database.DEVICE_ID));
                String temperature = cursor.getString(cursor.getColumnIndex(Database.TEMPERATURE));
                String humidity = cursor.getString(cursor.getColumnIndex(Database.HUMIDITY));


                Log.d("Feedbackdevice",deviceId);
                Log.d("Feedbacktemperature ",temperature);
                Log.d("Feedbackhumidity",humidity);


            }
        }
        cursor.close();





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

  /*      //user Location

        fusedLocationProviderClient = new FusedLocationProviderClient(getContext());

        googleApiClient = new GoogleApiClient.Builder(getContext())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        locationRequest = new LocationRequest();
        locationRequest = new LocationRequest();
        locationRequest.setInterval(10 * 1000);
        locationRequest.setFastestInterval(15 * 1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


*/

        return view;


    }


    public void updateQuestion(){


        if(feedbackQuestion<1) {


            txtFeedback.setText(questionLibrary.getFeedbackQuestion(feedbackQuestion));
            btn1.setText(questionLibrary.getFeedbackOption(0));
            btn2.setText(questionLibrary.getFeedbackOption(1));

            qList.add(txtFeedback.getText().toString());

            feedbackQuestion++;

        }
        else if(feedbackQuestion<2) {

            txtFeedback.setText(questionLibrary.getFeedbackQuestion(feedbackQuestion));
            btn1.setText(questionLibrary.getFeedbackOption(2));
            btn2.setText(questionLibrary.getFeedbackOption(3));


            qList.add(txtFeedback.getText().toString());

            feedbackQuestion++;

        }
        else {

            saveSymptomsFeedback();

            Intent intent = new Intent(getContext(), MainActivity.class);
            startActivity(intent);


        }

    }

    public void saveSymptomsFeedback() {

/*

        // get Ruuvitag sensor data from DB

        cursor = db.rawQuery("SELECT * FROM " + Database.DEVICE_TABLE + "ORDER BY Timestamp desc limit 1", null);


        if (cursor != null) {
            cursor.moveToFirst();

            if (cursor.getCount() > 0) {

                // get values from cursor here

                String deviceId = cursor.getString(cursor.getColumnIndex(Database.DEVICE_ID));
                String temperature = cursor.getString(cursor.getColumnIndex(Database.TEMPERATURE));
                String humidity = cursor.getString(cursor.getColumnIndex(Database.HUMIDITY));


                Log.d("device id",deviceId);
                Log.d("temperature ",temperature);
                Log.d("humidity",humidity);


            }
        }


*/





        String time = new SimpleDateFormat("dd-MM-yyyy, hh:mm:ss").format(new Date());

        final JSONObject feedbackJsonObject = new JSONObject();
        for (int index = 0; index < qList.size(); index++) {
            try {
                feedbackJsonObject.put(qList.get(index), aList.get(index));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        String stringFeedback = feedbackJsonObject.toString();

        String mergeSymptomsFeedback = stringSymptoms.concat(stringFeedback);

       /* try {
            final JSONObject symptomsJsonObject = new JSONObject(mergeSymptomsFeedback);
            Log.d("data symptoms:", symptomsJsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
*/

        Log.d("mergesymptomsfeedback:", mergeSymptomsFeedback);


        ContentValues values = new ContentValues();

        values.put(Database.SYMPTOMS, mergeSymptomsFeedback);

        values.put(Database.SYMPTOMS_timestamp, time);

        dbHandler.insertSymptomsData(values);

        dbHandler.close();

    }
/*
    @Override
    public void onStart() {

        super.onStart();

        googleApiClient.connect();
    }


    @Override
    public void onResume() {
        super.onResume();
        // Permission check for Marshmallow and newer
        int permissionCoarseLocation = ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION);

        int permissionWriteExternal = ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        List<String> listPermissionsNeeded = new ArrayList<>();

        if (permissionCoarseLocation != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }

        if (permissionWriteExternal != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(getActivity(), listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), 1);
        }


        if (permissionIsGranted) {
            if (googleApiClient.isConnected()) {
                requestLocationUpdates();
            }
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        if (permissionIsGranted)
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (permissionIsGranted)
            googleApiClient.disconnect();
    }

    @Override
    public void onConnected(Bundle bundle){
        requestLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        myLatitude = location.getLatitude();
        myLongitude = location.getLongitude();
        Toast.makeText(getContext(), String.valueOf(myLatitude) + String.valueOf(myLongitude), Toast.LENGTH_SHORT).show();

    }
    private void requestLocationUpdates(){


        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_REQUEST_FINE_LOCATION);
            } else {
                permissionIsGranted = true;
            }
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }*/




}
