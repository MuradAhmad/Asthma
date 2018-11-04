package com.example.murahmad.asthma;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.Region;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.concurrent.ScheduledExecutorService;

import static android.content.Context.BLUETOOTH_SERVICE;
import static android.support.v4.content.ContextCompat.getSystemService;

/**
 * Created by muradahmad on 07/08/2018.
 */

public class Dashboard extends Fragment {


    private class LeScanResult {
        BluetoothDevice device;
        int rssi;
        byte[] scanData;
    }





    // array lists for local database tables
    private List<String> symptomsList;
    private List<String> feedbackList;
    private List<String> deviceList;

    private ArrayList<LeScanResult> scanResults;

    private BluetoothAdapter bluetoothAdapter;
    private boolean scanning;


    private ScheduledExecutorService scheduler;
    private ScheduledExecutorService alertScheduler;

    SharedPreferences settings;

    RuuviTag ruuviTag;



    Database handler;
    SQLiteDatabase db;
    Cursor cursor,cursor1,cursor2;

    private Timer timer;
    private Handler scanTimerHandler;
    private static int MAX_SCAN_TIME_MS = 1000;




    private BeaconManager beaconManager = null;

    private Region beaconRegion = null;




    private TextView txtTemperature, txtHumidity, txtDeviceId, txtRssi;
    private Button btnSync;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       View view = inflater.inflate(R.layout.activity_dashboard, container,false);


        txtTemperature = (TextView)  view.findViewById(R.id.txtTemperature);
        txtDeviceId = (TextView) view.findViewById(R.id.txtDeviceId);
        txtHumidity = (TextView) view.findViewById(R.id.txtHumidity);
        txtRssi = (TextView) view.findViewById(R.id.txtRssi);


        btnSync = (Button) view.findViewById(R.id.btnSync);




        handler = new Database(getContext());
        db = handler.getReadableDatabase();


        // array list to store data from local DB
        symptomsList = new ArrayList<>();
        feedbackList = new ArrayList<>();
        deviceList = new ArrayList<String>();



        final Handler threadHandler = new Handler();
        Runnable runnable = new Runnable()
         {
            public void run() {
                //Whatever task you wish to perform
                //For eg. textView.setText("SOME TEXT")
                cursor = db.rawQuery("SELECT * FROM " + Database.DEVICE_TABLE, null);


                if (cursor != null) {
                    cursor.moveToFirst();

                    if (cursor.getCount() > 0) {

                        // get values from cursor here

                        String deviceId = cursor.getString(cursor.getColumnIndex(Database.DEVICE_ID));
                        String temperature = cursor.getString(cursor.getColumnIndex(Database.TEMPERATURE));
                        String humidity = cursor.getString(cursor.getColumnIndex(Database.HUMIDITY));
                        String rssi = cursor.getString(cursor.getColumnIndex(Database.RSSI));

                        txtDeviceId.setText(deviceId);
                        txtTemperature.setText(temperature + " °C ");
                        txtHumidity.setText(humidity + " % ");
                        txtRssi.setText(rssi + "  dBm");

                        Log.d("Device ID Dashboard: ", deviceId);
                        Log.d("Temperature Dashboard: ", temperature);
                        Log.d("Humidity Dashboard: ", humidity);
                        Log.d("RSSI Dashboard: ", rssi);
                    }
                }
                threadHandler.postDelayed(this, 1000);
            }
        };

        threadHandler.postDelayed(runnable, 1000);


        btnSync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //sendPostRequest();

            }
        });



        return view;

    }






    private void sendPostRequest() {


        cursor = db.rawQuery("SELECT * FROM " + Database.SYMPTOMS_TABLE +" order by Timestamp desc", null);

        while (cursor != null &&  cursor.moveToFirst()) {
            //get the record id from the database
            String symptoms= cursor.getString(cursor.getColumnIndex(Database.SYMPTOMS));
            String timestamp= cursor.getString(cursor.getColumnIndex(Database.SYMPTOMS_timestamp));
            Log.d("Symptoms data", symptoms);

            final JSONObject jsonObject = new JSONObject();
            try {
                //add the record id to the jsonObject
                jsonObject.put("value", symptoms);
                jsonObject.put("timestamp",timestamp);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            symptomsList.add(jsonObject.toString());



        }
        if(cursor!=null)
            cursor.close();

        cursor1 = db.rawQuery("SELECT * FROM " + Database.FEEDBACK_TABLE +" order by Timestamp desc", null);

        while (cursor1 != null &&  cursor1.moveToFirst()) {


            // get values from cursor here


            //get the record id from the database
            String feedback= cursor1.getString(cursor1.getColumnIndex(Database.FEEDBACK));
            String timestamp= cursor1.getString(cursor1.getColumnIndex(Database.FEEDBACK_timestamp));
            final JSONObject jsonObject = new JSONObject();
            try {
                //add the record id to the jsonObject
                jsonObject.put("value", feedback);
                jsonObject.put("timestamp",timestamp);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.d("feedback data", jsonObject.toString());
            feedbackList.add(jsonObject.toString());

        }

        if (cursor1!=null);
        cursor1.close();

        cursor2 = db.rawQuery("SELECT * FROM " + Database.DEVICE_TABLE +" order by Timestamp desc limit 1", null);

        if (cursor2 != null) {
            cursor2.moveToFirst();

            if (cursor2.getCount() > 0) {

                // get values from cursor here

                String deviceId= cursor2.getString(cursor2.getColumnIndex(Database.DEVICE_ID));
                String temperature = cursor2.getString(cursor2.getColumnIndex(Database.TEMPERATURE));
                String humidity = cursor2.getString(cursor2.getColumnIndex(Database.HUMIDITY));

                deviceList.add(deviceId);
                deviceList.add(temperature);
                deviceList.add(humidity);

            }

        }
        cursor2.close();


        // Instantiate the RequestQueue.
        final RequestQueue requestQueue = Volley.newRequestQueue(getContext());

        String url = "https://co2.awareframework.com:8443/insert";
        // String url = "https://co2.awareframework.com:3306/insert";


        try {

            // send device data to server
            Calendar calendar = Calendar.getInstance();

            /*J
            for (int index = 0; index < feedbackList.size(); index++) {
               String feedback = feedbackList.get(index);
                final JSONObject appendJsonObject = new JSONObject(feedback);

                symptomsJsonObject + = appendJsonObject;
            }*/

            JSONObject symptomsJsonObject = new JSONObject();
            symptomsJsonObject.put("symptoms",symptomsList);
            symptomsJsonObject.put("feedback",feedbackList);
            symptomsJsonObject.put("uuid","xxxxx");
            //symptomsJsonObject.

            final JSONObject jsonObject = new JSONObject();

            jsonObject.put("tableName", "RuuviTag");
            jsonObject.put("deviceId", "ba890ac9-55a3-416b-bd38-387ffeeb8c42");

            jsonObject.put("data", symptomsJsonObject);
            jsonObject.put("timestamp", calendar.getTimeInMillis());




            final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            Log.d("Response is: ", response.toString());
                            Log.d("Json data: ", jsonObject.toString());
// now create a new asynchronous task or background method, that will iterate through the sent JSON data, in their respective database tables
                            // and flag it as sent.. for this you need to extend the database tables with an additonal column "sent"
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            error.printStackTrace();
                            // error
                            Log.d("Error is: ", error.toString());
                            //Toast.makeText(this, "Data not sent to server", Toast.LENGTH_SHORT).show();


                        }
                    }

            ) {    //this is the part, that adds the header to the request
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> params = new HashMap<String, String>();
                    // params.put("Content-Type", "application/json; charset=utf-8");
                    //params.put("Content-Type", "application/json");
                    return params;
                }
            };


            requestQueue.add(jsonObjectRequest);

        } catch (JSONException e1) {
            e1.printStackTrace();
        }
        ;


    }



}
