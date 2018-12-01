package com.example.murahmad.asthma;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;
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
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.Region;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.UUID;
import java.util.concurrent.ScheduledExecutorService;

import static android.content.Context.BLUETOOTH_SERVICE;
import static android.content.Context.CONNECTIVITY_SERVICE;
import static android.support.v4.content.ContextCompat.getSystemService;

/**
 * Created by muradahmad on 07/08/2018.
 */

public class Dashboard extends Fragment {


    // array lists for local database tables
    private List<String> symptomsList;

    private static final String DEBUG_TAG = "NetworkStatus";

    boolean isWifiConn;
    boolean isMobileConn;


    String timestamp;
    String symptoms;
    String strUUID;

    Database handler;
    SQLiteDatabase db;
    Cursor cursor,  cursor1;

    private Timer timer;
    private Handler scanTimerHandler;
    private static int MAX_SCAN_TIME_MS = 1000;


    private TextView txtTemperature, txtHumidity, txtDeviceId, txtRssi;
    private Button btnSync;

    final JSONObject symptomsJsonObject = new JSONObject();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       View view = inflater.inflate(R.layout.activity_dashboard, container,false);


        txtTemperature = (TextView)  view.findViewById(R.id.txtTemperature);
        txtDeviceId = (TextView) view.findViewById(R.id.txtDeviceId);
        txtHumidity = (TextView) view.findViewById(R.id.txtHumidity);
        txtRssi = (TextView) view.findViewById(R.id.txtRssi);


        btnSync = (Button) view.findViewById(R.id.btnSync);

        txtTemperature.setText(R.string.temperature);
        txtDeviceId.setText(R.string.deviceid);
        txtHumidity.setText(R.string.humidity);
        txtRssi.setText(R.string.rssi);

        btnSync.setText(R.string.sync);






        handler = new Database(getContext());
        db = handler.getReadableDatabase();


        // array list to store data from local DB
        symptomsList = new ArrayList<>();




        final Handler threadHandler = new Handler();
        Runnable runnable = new Runnable()
         {
            public void run() {
                //Whatever task you wish to perform
                //For eg. textView.setText("SOME TEXT")
                cursor = db.rawQuery("SELECT * FROM " + Database.DEVICE_TABLE +" order by Date desc", null);


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





        /// string UUID TEsting









        // check network wifi status

       ConnectivityManager connMgr =
                (ConnectivityManager) getContext().getSystemService(CONNECTIVITY_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            for (Network network : connMgr.getAllNetworks()) {
                NetworkInfo networkInfo = connMgr.getNetworkInfo(network);
                if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                    isWifiConn |= networkInfo.isConnected();
                }
                if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                    isMobileConn |= networkInfo.isConnected();
                }
            }
        }
        Log.d(DEBUG_TAG, "Wifi connected: " + isWifiConn);
        Log.d(DEBUG_TAG, "Mobile connected: " + isMobileConn);


            btnSync.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(isOnline()) {

                        if (isMobileConn) {
                            sendPostRequest();
                        } else if (isWifiConn) {
                            sendPostRequest();
                        } else {
                            Toast.makeText(getContext(),
                                    "No internet", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext(),
                                "No internet", Toast.LENGTH_SHORT).show();}

                }
            });











/*
        ConnectivityManager connManager = (ConnectivityManager) getContext().getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (mWifi.isConnected()) {
            // Do whatever
        }*/

        return view;

    }




    public boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getContext().getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }




    private void sendPostRequest() {


        cursor = db.rawQuery("SELECT * FROM " + Database.SYMPTOMS_TABLE +" order by Timestamp desc", null);
        if (cursor != null) {
            cursor.moveToFirst();

            if (cursor.getCount() > 0) {

       // while (cursor != null &&  cursor.moveToFirst()) {
            //get the record id from the database
            symptoms= cursor.getString(cursor.getColumnIndex(Database.SYMPTOMS));
            timestamp= cursor.getString(cursor.getColumnIndex(Database.SYMPTOMS_timestamp));
            Log.d("Symptoms dashboard", symptoms);
                Log.d("timestamp dashboard", timestamp);



            try {
                //add the record id to the jsonObject
                symptomsJsonObject.put("Symptoms", symptoms);
                Log.d("symptomsJson data: ", symptomsJsonObject.toString());
                //jsonObjectSymptoms.put("timestamp",timestamp);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }}

        cursor.close();
        //db.execSQL("delete from "+ Database.SYMPTOMS_TABLE);

        cursor1 = db.rawQuery("SELECT * FROM " + Database.REGISTRATION_TABLE, null);


        if (cursor1 != null) {
            cursor1.moveToFirst();

            if (cursor1.getCount() > 0) {

                // get values from cursor here

                strUUID = cursor1.getString(cursor1.getColumnIndex(Database.UUID));
                Log.d("UUID DB",strUUID);
            }
        }
        cursor1.close();



/*

        final JSONObject symptomsJsonObject = new JSONObject();



        try {
            //add the record id to the jsonObject
            String  symptoms = "testing";
            symptomsJsonObject.put("Symptoms", symptoms);
            //jsonObjectSymptoms.put("timestamp",timestamp);

        } catch (JSONException e) {
            e.printStackTrace();
        }

*/





        // server code


        try{
            // Instantiate the RequestQueue.
            final RequestQueue requestQueue = Volley.newRequestQueue(getContext());

            String url = "https://co2.awareframework.com:8443/insert";
            // testing connectivity
             //String url = "https://co2.awareframework.com:3306/insert";

            // "ba890ac9-55a3-416b-bd38-387ffeeb8c42"


            Log.d("UUID before Json",strUUID);

            final JSONObject jsonObject = new JSONObject();
            jsonObject.put("tableName", "RuuviTag");
            // get user UUID from DB
           // jsonObject.put("deviceId", strUUID);
            jsonObject.put("deviceId", "35d0ee9c-229e-49a8-a1dc-34c2d2c8c0eb");
            jsonObject.put("data", symptomsJsonObject);
            //Log.d("Symptoms sync", symptoms);
            //jsonObject.put("data", symptoms);
            jsonObject.put("timestamp", timestamp);




            final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            Log.d("Response is: ", response.toString());
                            Log.d("Json data: ", jsonObject.toString());

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {




                            // As of f605da3 the following should work
                            NetworkResponse response = error.networkResponse;
                            if (error instanceof ServerError && response != null) {
                                try {
                                    String res = new String(response.data,
                                            HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                                    // Now you can use any deserializer to make sense of data
                                    JSONObject obj = new JSONObject(res);
                                } catch (UnsupportedEncodingException e1) {
                                    // Couldn't properly decode data to string
                                    e1.printStackTrace();
                                } catch (JSONException e2) {
                                    // returned data is not JSONObject?
                                    e2.printStackTrace();
                                }
                            }

                            error.printStackTrace();
                            // error
                            Log.d("Error is: ", error.toString());
                            Toast.makeText(getContext(),
                                    "Data not sent to server", Toast.LENGTH_SHORT).show();


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

        //server code ends




/*


        // Instantiate the RequestQueue.
        final RequestQueue requestQueue = Volley.newRequestQueue(getContext());

        String url = "https://co2.awareframework.com:8443/insert";
        // String url = "https://co2.awareframework.com:3306/insert";


        try {



            JSONObject symptomsJsonObject = new JSONObject();
            symptomsJsonObject.put("symptoms",symptomsList);
            //symptomsJsonObject.put("feedback",feedbackList);
            symptomsJsonObject.put("uuid","xxxxx");

            // send device data to server
            Calendar calendar = Calendar.getInstance();

*/
/*
            for (int index = 0; index < symptomsList.size(); index++) {
               String symptoms = symptomsList.get(index);
                final JSONObject appendJsonObject = new JSONObject(symptoms);

                symptomsJsonObject += appendJsonObject;
            }*//*


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
*/


    }



}
