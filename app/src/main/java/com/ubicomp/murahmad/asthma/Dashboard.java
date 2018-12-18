package com.ubicomp.murahmad.asthma;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.GpsSatellite;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.aware.utils.DatabaseHelper;
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
import java.util.Timer;

import static android.content.Context.CONNECTIVITY_SERVICE;

/**
 * Created by muradahmad on 07/08/2018.
 */

public class Dashboard extends Fragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, android.location.GpsStatus.Listener{

    // array lists for local database tables
    private List<String> symptomsList;

    private static final String DEBUG_TAG = "NetworkStatus";

    // location data
    private int satellites = 0;
    private int satellitesInFix = 0;

    private LocationManager locationManager;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;

    private FusedLocationProviderApi locationProvider = LocationServices.FusedLocationApi;
    private GoogleApiClient googleApiClient;

    Context context;

    private Double myLatitude;
    private Double myLongitude;

    private static final int MY_PERMISSION_REQUEST_FINE_LOCATION = 104;
    private static final int MY_PERMISSION_REQUEST_COARSE_LOCATION = 105;
    private boolean permissionIsGranted = false;

    boolean isWifiConn;
    boolean isMobileConn;

    Double timestamp;
    String symptoms;
    String strUUID;

    private Timer timer;
    private Handler scanTimerHandler;
    private static int MAX_SCAN_TIME_MS = 1000;

    Database handler;
    SQLiteDatabase db;

    private TextView txtTemperature, txtHumidity, txtDeviceId, txtRssi;
    private Button btnSync;

    final JSONObject symptomsJsonObject = new JSONObject();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_dashboard, container, false);

        context = this.getContext();

        btnSync = (Button) view.findViewById(R.id.btnSync);

        if (!RuuviTagScanner.isBLEAvailable(getContext())) {
            view.findViewById(R.id.lldevicedata).setVisibility(View.INVISIBLE);
        } else {
            txtTemperature = (TextView) view.findViewById(R.id.txtTemperature);
            txtDeviceId = (TextView) view.findViewById(R.id.txtDeviceId);
            txtHumidity = (TextView) view.findViewById(R.id.txtHumidity);
            txtRssi = (TextView) view.findViewById(R.id.txtRssi);

            txtTemperature.setText(R.string.temperature);
            txtDeviceId.setText(R.string.deviceid);
            txtHumidity.setText(R.string.humidity);
            txtRssi.setText(R.string.rssi);

            btnSync.setText(R.string.sync);

            // array list to store data from local DB
            symptomsList = new ArrayList<>();

            handler = new Database(getContext());
            db = handler.getReadableDatabase();

            final Handler threadHandler = new Handler();
            Runnable runnable = new Runnable() {
                public void run() {
                    Cursor cursor = db.rawQuery("SELECT * FROM " + Database.DEVICE_TABLE + " order by Date desc", null);
                    if (cursor != null && cursor.moveToFirst()) {
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

                    threadHandler.postDelayed(this, 1000);
                }
            };

            threadHandler.postDelayed(runnable, 1000);
        }

        ConnectivityManager connMgr = (ConnectivityManager) getContext().getSystemService(CONNECTIVITY_SERVICE);
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

        btnSync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isOnline() && (isMobileConn || isWifiConn)) {
                    sendPostRequest();
                } else {
                    Toast.makeText(getContext(), getString(R.string.feedback_internet_missing), Toast.LENGTH_SHORT).show();
                }
            }
        });






        //user Location
        locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
            //return;

        }

        locationManager.addGpsStatusListener(this);
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


        return view;
    }

    public boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getContext().getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    private void sendPostRequest() {
        Database handler = new Database(getContext());
        SQLiteDatabase db = handler.getReadableDatabase();
        JSONArray registrationData = new JSONArray();

        Cursor cursor1 = db.rawQuery("SELECT * FROM " + Database.REGISTRATION_TABLE, null);
        if (cursor1 != null && cursor1.moveToFirst()) {
            try {
                registrationData = new JSONArray(DatabaseHelper.cursorToString(cursor1));
                strUUID = registrationData.getJSONObject(0).getString(Database.UUID);
                Log.d("UUID DB", strUUID);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (!cursor1.isClosed()) cursor1.close();
        }

        SharedPreferences appData = getContext().getSharedPreferences(getContext().getPackageName(), Context.MODE_PRIVATE);
        if (!appData.contains("registrationSync")) {
            try {
                pushRegistrationToServer(strUUID, registrationData.getJSONObject(0), registrationData.getJSONObject(0).getDouble(Database.reg_timestamp));
                appData.edit().putBoolean("registrationSync", true).apply();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        Cursor cursor = db.rawQuery("SELECT * FROM " + Database.SYMPTOMS_TABLE + " order by Timestamp asc", null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                symptoms = cursor.getString(cursor.getColumnIndex(Database.SYMPTOMS));
                timestamp = cursor.getDouble(cursor.getColumnIndex(Database.SYMPTOMS_timestamp));
                try {
                    //add the record id to the jsonObject
                    symptomsJsonObject.put("Symptoms", symptoms);
                    pushToServer(strUUID, symptomsJsonObject, timestamp);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } while (cursor.moveToNext());
            if (!cursor.isClosed()) cursor.close();
        }

        Cursor medications = db.query(Database.MEDICATION_TABLE, null, null, null, null, null, Database.MED_DATE + " ASC");
        if (medications != null && medications.moveToFirst()) {
            try {
                JSONArray medicationJSON = new JSONArray(DatabaseHelper.cursorToString(medications));
                for (int i = 0; i < medicationJSON.length(); i++) {
                    JSONObject medication = medicationJSON.getJSONObject(i);
                    pushToServerMedication(strUUID, medication, medication.getDouble(Database.MED_DATE));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (!medications.isClosed()) medications.close();
        }

        Cursor location = db.query(Database.LOCATION_TABLE, null, null, null, null, null, Database.LOCATION_timestamp + " ASC");
        if (location != null && location.moveToFirst()) {
            try {
                JSONArray locationJSON = new JSONArray(DatabaseHelper.cursorToString(location));
                for (int i = 0; i < locationJSON.length(); i++) {
                    JSONObject locationObject = locationJSON.getJSONObject(i);
                    pushToServerLocation(strUUID, locationObject, locationObject.getDouble(Database.LOCATION_timestamp));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (!location.isClosed()) location.close();
        }
        db.close();




    }


    private void pushRegistrationToServer(String strUUID, JSONObject registrationJsonObject, Double timestamp) {

        try {
            final RequestQueue requestQueue = Volley.newRequestQueue(getContext());
            String url = "https://co2.awareframework.com:8443/insert";

            final JSONObject jsonObject = new JSONObject();
            jsonObject.put("tableName", "participants");
            jsonObject.put("deviceId", strUUID);
            jsonObject.put("data", registrationJsonObject);
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
                            Log.d("Error is: ", error.getMessage());
                            Toast.makeText(getContext(),
                                    getString(R.string.feedback_server_failure), Toast.LENGTH_SHORT).show();
                        }
                    }

            );

            requestQueue.add(jsonObjectRequest);

        } catch (JSONException e1) {
            e1.printStackTrace();
        }
    }


    private void pushToServerMedication(String strUUID, JSONObject symptomsJsonObject, final Double timestamp) {
        try {

            final RequestQueue requestQueue = Volley.newRequestQueue(getContext());

            String url = "https://co2.awareframework.com:8443/insert";

            final JSONObject jsonObject = new JSONObject();
            jsonObject.put("tableName", "medications");
            jsonObject.put("deviceId", strUUID);
            jsonObject.put("data", symptomsJsonObject);
            jsonObject.put("timestamp", timestamp);

            final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            db.delete(Database.MEDICATION_TABLE, Database.MED_DATE + " <= " + timestamp, null);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d("Error is: ", error.getMessage());
                            Toast.makeText(getContext(),
                                    getString(R.string.feedback_server_failure), Toast.LENGTH_SHORT).show();
                        }
                    }
            );

            requestQueue.add(jsonObjectRequest);

        } catch (JSONException e1) {
            e1.printStackTrace();
        }
    }

    private void pushToServer(String strUUID, JSONObject symptomsJsonObject, final Double timestamp) {
        try {
            final RequestQueue requestQueue = Volley.newRequestQueue(getContext());

            String url = "https://co2.awareframework.com:8443/insert";

            final JSONObject jsonObject = new JSONObject();
            jsonObject.put("tableName", "symptoms");
            jsonObject.put("deviceId", strUUID);
            jsonObject.put("data", symptomsJsonObject);
            jsonObject.put("timestamp", timestamp);

            final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            db.delete(Database.SYMPTOMS_TABLE, Database.SYMPTOMS_timestamp + " <= " + timestamp, null);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d("Error is: ", error.getMessage());
                            Toast.makeText(getContext(),
                                    getString(R.string.feedback_server_failure), Toast.LENGTH_SHORT).show();
                        }
                    }

            );

            requestQueue.add(jsonObjectRequest);

        } catch (JSONException e1) {
            e1.printStackTrace();
        }
    }

    private void pushToServerLocation(String strUUID, JSONObject locationJsonObject, final Double timestamp){



        try {
            final RequestQueue requestQueue = Volley.newRequestQueue(getContext());

            String url = "https://co2.awareframework.com:8443/insert";

            final JSONObject jsonObject = new JSONObject();
            jsonObject.put("tableName", "locations");
            jsonObject.put("deviceId", strUUID);
            jsonObject.put("data", locationJsonObject);
            jsonObject.put("timestamp", timestamp );

            final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            db.delete(Database.LOCATION_TABLE, Database.LOCATION_timestamp + " <= " + timestamp, null);

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d("Error is: ", error.getMessage());
                            Toast.makeText(getContext(),
                                    getString(R.string.feedback_server_failure), Toast.LENGTH_SHORT).show();
                        }
                    }

            );

            requestQueue.add(jsonObjectRequest);

        } catch (JSONException e1) {
            e1.printStackTrace();
        }

    }
    public void saveLocation(){

        JSONArray data = new JSONArray();

        final JSONObject locationJsonObject = new JSONObject();
        try {
            locationJsonObject.put("latitude", myLatitude);
            locationJsonObject.put("longitude", myLongitude);
            locationJsonObject.put("Number of satellites", satellitesInFix);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        data.put(locationJsonObject);

        Calendar calendar = Calendar.getInstance();

        ContentValues values = new ContentValues();
        values.put(Database.LOCATION, data.toString());
        values.put(Database.LOCATION_timestamp, calendar.getTimeInMillis());

        handler.insertLocationData(values);

      //  handler.close();



    }





    @Override
    public void onConnected(Bundle bundle) {
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

        //Toast.makeText(getContext(), String.valueOf(myLatitude) + String.valueOf(myLongitude) +"Satellite.. "+ String.valueOf(location.getExtras().getInt("satellites")), Toast.LENGTH_SHORT).show();
       // Log.d("satellites", String.valueOf(location.getExtras().getInt("satellites")));

        saveLocation();

    }



    @Override
    public void onGpsStatusChanged(int event) {


        if (ActivityCompat.checkSelfPermission((Activity)context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity)context, new String[]{
                    android.Manifest.permission.ACCESS_FINE_LOCATION
            }, 10);
        }



        int timetofix = locationManager.getGpsStatus(null).getTimeToFirstFix();
        Log.d(" ", "Time to first fix = " + timetofix);
        for (GpsSatellite sat : locationManager.getGpsStatus(null).getSatellites()) {
            if (sat.usedInFix()) {
                satellitesInFix++;
            }
            satellites++;
        }
         Log.d(" satellites", satellites + " Used In Last Fix (" + satellitesInFix + ")");
        //Toast.makeText(getContext(), String.valueOf(satellitesInFix), Toast.LENGTH_SHORT).show();

    }


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

    private void requestLocationUpdates() {


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


}
