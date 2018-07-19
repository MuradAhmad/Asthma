
package com.example.murahmad.asthma;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.RemoteException;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.neovisionaries.bluetooth.ble.advertising.ADPayloadParser;
import com.neovisionaries.bluetooth.ble.advertising.ADStructure;
import com.neovisionaries.bluetooth.ble.advertising.EddystoneURL;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {


    private class LeScanResult {
        BluetoothDevice device;
        int rssi;
        byte[] scanData;
    }


    private static final String TAG = "MainActivity";
    //   private static final String ALTBEACON_LAYOUT = "m:2-3=beac,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25";


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


    TextView txtTemperature;

    private BeaconManager beaconManager = null;

    private Region beaconRegion = null;


    private boolean entryMessageRaised = false;
    private boolean exitMessageRaised = false;
    private boolean rangingMessageRaised = false;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        txtTemperature = (TextView) findViewById(R.id.txtTemperature);

     /*   if(ruuviTag.getTemperature() != null) {
            txtTemperature.setText(ruuviTag.getTemperature());

        } else
            txtTemperature.setText("100");*/

        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1234);


        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();


        beaconManager = BeaconManager.getInstanceForApplication(this);



        handler = new Database(this);
        db = handler.getReadableDatabase();

        String time = new SimpleDateFormat("dd-MM-yyyy, hh:mm:ss").format(new Date());


        ContentValues values = new ContentValues();
        values.put(Database.DEVICE_ID, 12);
        values.put(Database.URL, "URL");
        values.put(Database.RSSI, "RSSI");
        values.put(Database.TEMPERATURE, "12345");
        //values.put(Database.HUMIDITY, ruuvitag.getHumidity());
        //values.put(Database.PRESSURE, ruuvitag.getPressure());
        values.put(Database.DATE, time);

        handler.insertDeviceData( values);


        cursor = db.rawQuery("SELECT * FROM " + Database.DEVICE_TABLE, null);

        if(cursor != null) {
            cursor.moveToFirst();

            if(cursor.getCount() > 0){
// get values from cursor here


            String deviceId = cursor.getString(cursor.getColumnIndex(Database.DEVICE_ID));
            String temperature = cursor.getString(cursor.getColumnIndex(Database.TEMPERATURE));

            txtTemperature.setText(temperature);
            }
        }


/*
        runOnUiThread(new Runnable() {
            public void run() {
                //Whatever task you wish to perform
                //For eg. textView.setText("SOME TEXT")
                cursor = db.rawQuery("SELECT * FROM " + Database.DEVICE_TABLE, null);

                if(cursor != null) {
                    cursor.moveToFirst();

                    if(cursor.getCount() > 0){
// get values from cursor here


                        String deviceId = cursor.getString(cursor.getColumnIndex(Database.DEVICE_ID));
                        String temperature = cursor.getString(cursor.getColumnIndex(Database.TEMPERATURE));

                        txtTemperature.setText(temperature);
                    }
                }
            }
        });*/

    }







   /*
        beaconManager = BeaconManager.getInstanceForApplication(this);
        // Detect the URL frame:
        beaconManager.getBeaconParsers().add(new BeaconParser()
                .setBeaconLayout(BeaconParser.EDDYSTONE_URL_LAYOUT));
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(BeaconParser.))
        beaconManager.bind(this);*//*


      */
/*  beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(BeaconParser.ALTBEACON_LAYOUT));
    beaconManager.bind(this);

    beaconManager.setMonitorNotifier(new MonitorNotifier() {
        @Override
        public void didEnterRegion(Region region) {
            if(!entryMessageRaised){

                Toast.makeText(MainActivity.this, "Beacon found"+"Beacon Unique Id"+region.getUniqueId() +"Beacon UUID/Major/Minor" + region.getId1() +"major" +region.getId2()
                                +"minor"+region.getId3() ,
                        Toast.LENGTH_LONG).show();

                Log.d("TAG","Beacon found"+"Beacon Unique Id"+region.getUniqueId() +"Beacon UUID/Major/Minor" + region.getId1() +"major" +region.getId2()
                        +"minor"+region.getId3());
                entryMessageRaised = true;
            }
        }

        @Override
        public void didExitRegion(Region region) {

            if(!exitMessageRaised) {

                Toast.makeText(MainActivity.this, "Beacon not found/ Leaving" + "Beacon Unique Id" + region.getUniqueId() + "Beacon UUID/Major/Minor" + region.getId1() + "major" + region.getId2()
                                + "minor" + region.getId3(),
                        Toast.LENGTH_LONG).show();
                Log.d("TAG","Beacon not found/ Leaving"+"Beacon Unique Id"+region.getUniqueId() +"Beacon UUID/Major/Minor" + region.getId1() +"major" +region.getId2()
                                +"minor"+region.getId3());

                exitMessageRaised = true;

            }

        }

        @Override
        public void didDetermineStateForRegion(int i, Region region) {

        }
    });


    beaconManager.setRangeNotifier(new RangeNotifier() {
        @Override
        public void didRangeBeaconsInRegion(Collection<Beacon> beaconCollection, Region region) {
            if(!rangingMessageRaised && beaconCollection != null && !beaconCollection.isEmpty()){
                for(Beacon beacon:beaconCollection ){

                    Toast.makeText(MainActivity.this, "Beacon exits" + "Beacon ranging Unique Id" + region.getUniqueId() + "Beacon UUID/Major/Minor" + region.getId1() + "major" + region.getId2()
                                    + "minor" + region.getId3(),
                            Toast.LENGTH_LONG).show();
                    Log.d("TAG","Beacon exits"+"Beacon ranging Unique Id"+region.getUniqueId() +"Beacon UUID/Major/Minor" + region.getId1() +"major" +region.getId2()
                            +"minor"+region.getId3());
                    rangingMessageRaised = true;

                }
            }


        }
    });




    }

    public static boolean isBluetoothEnabled() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        return bluetoothAdapter != null && bluetoothAdapter.isEnabled();
    }


    @Override
    public void onBeaconServiceConnect() {

    }
    private  void startBeaconMonitong (){
        Log.d("TAG", "Beacon  Monitoring Started");
        try {

            beaconRegion = new Region("MyBeacons", Identifier.parse("unique id"), Identifier.parse("4"),Identifier.parse("200"));

        beaconManager.startMonitoringBeaconsInRegion(beaconRegion);
        beaconManager.startRangingBeaconsInRegion(beaconRegion);
        }
          catch (RemoteException e){
            e.printStackTrace();

          }
        
    }
    private void stopBeaconMonitoring (){
        Log.d("TAG", "Beacon  Monitoring Stoped");
        try {

               beaconManager.stopMonitoringBeaconsInRegion(beaconRegion);
               beaconManager.stopRangingBeaconsInRegion(beaconRegion);


        }   catch (RemoteException e){
            e.printStackTrace();
        }
*//*









}

*/


    @Override
    protected void onStart() {

Intent intent = new Intent(MainActivity.this, RuuviTagScanner.class);
        startService(intent);

        super.onStart();
    }
    @Override
    protected void onResume() {
        super.onResume();
        // Permission check for Marshmallow and newer
        int permissionCoarseLocation = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);

        int permissionWriteExternal = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        List<String> listPermissionsNeeded = new ArrayList<>();

        if(permissionCoarseLocation != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }

        if(permissionWriteExternal != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if(!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), 1);
        }


    }
   @Override
   protected void onStop() {
       super.onStop();
   }

}