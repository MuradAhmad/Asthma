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
import android.widget.TextView;

import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.Region;

import java.util.ArrayList;
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






    private ArrayList<LeScanResult> scanResults;

    private BluetoothAdapter bluetoothAdapter;
    private boolean scanning;


    private ScheduledExecutorService scheduler;
    private ScheduledExecutorService alertScheduler;

    SharedPreferences settings;

    RuuviTag ruuviTag;


    Database handler;
    SQLiteDatabase db;
    Cursor cursor;

    private Timer timer;
    private Handler scanTimerHandler;
    private static int MAX_SCAN_TIME_MS = 1000;




    private BeaconManager beaconManager = null;

    private Region beaconRegion = null;




    TextView txtTemperature, txtHumidity, txtDeviceId, txtRssi;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       View view = inflater.inflate(R.layout.activity_dashboard, container,false);


        txtTemperature = (TextView)  view.findViewById(R.id.txtTemperature);
        txtDeviceId = (TextView) view.findViewById(R.id.txtDeviceId);
        txtHumidity = (TextView) view.findViewById(R.id.txtHumidity);
        txtRssi = (TextView) view.findViewById(R.id.txtRssi);






        handler = new Database(getContext());
        db = handler.getReadableDatabase();



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




        return view;

    }
}
